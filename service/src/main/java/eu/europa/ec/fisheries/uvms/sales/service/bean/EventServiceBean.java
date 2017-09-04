/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.message.MessageException;
import eu.europa.ec.fisheries.uvms.sales.message.event.*;
import eu.europa.ec.fisheries.uvms.sales.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesMarshallException;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.SalesModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.sales.service.EventService;
import eu.europa.ec.fisheries.uvms.sales.service.InvalidMessageService;
import eu.europa.ec.fisheries.uvms.sales.service.ReportService;
import eu.europa.ec.fisheries.uvms.sales.service.UniqueIdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import java.util.List;


@Stateless
public class EventServiceBean implements EventService {

    final static Logger LOG = LoggerFactory.getLogger(EventServiceBean.class);

    @EJB
    private ReportService reportService;

    @EJB
    private InvalidMessageService invalidMessageService;

    @EJB
    private SalesMessageProducer salesMessageProducer;

    @EJB
    private UniqueIdService uniqueIdService;

    @Override
    public void createReport(@Observes @ReportReceivedEvent EventMessage event) {
        try {
            SalesReportRequest salesReportRequest = (SalesReportRequest) event.getSalesBaseRequest();

            Report report = JAXBMarshaller.unmarshallString(salesReportRequest.getReport(), Report.class);
            List<ValidationQualityAnalysisType> validationResults = salesReportRequest.getValidationResults();
            String pluginToSendResponseThrough = salesReportRequest.getPluginToSendResponseThrough();
            String messageValidationResult = salesReportRequest.getMessageValidationStatus().name();

            reportService.saveReport(report, pluginToSendResponseThrough, validationResults, messageValidationResult);
        } catch (SalesMarshallException e) {
            throw new SalesServiceException("Something went wrong during unmarshalling of a sales report", e);
        } catch (SalesServiceException e) {
            throw new SalesServiceException("Something went wrong when saving a sales report", e);
        }
    }

    public void executeQuery(@Observes @QueryReceivedEvent EventMessage event) {
        SalesQueryRequest salesQueryRequest = (SalesQueryRequest) event.getSalesBaseRequest();

        try {
            FLUXSalesQueryMessage salesQueryType = JAXBMarshaller.unmarshallString(salesQueryRequest.getQuery(), FLUXSalesQueryMessage.class);
            String pluginToSendResponseThrough = salesQueryRequest.getPluginToSendResponseThrough();
            List<ValidationQualityAnalysisType> validationResults = salesQueryRequest.getValidationResults();
            String messageValidationResult = salesQueryRequest.getMessageValidationStatus().name();

            reportService.search(salesQueryType, pluginToSendResponseThrough, validationResults, messageValidationResult);
        } catch (SalesMarshallException e) {
            throw new SalesServiceException("Something went wrong during unmarshalling of a sales query", e);
        }
    }

    public void respondToInvalidMessage(@Observes @InvalidMessageReceivedEvent EventMessage event) {
        RespondToInvalidMessageRequest respondToInvalidMessageRequest = (RespondToInvalidMessageRequest) event.getSalesBaseRequest();

        String pluginToSendResponseThrough = respondToInvalidMessageRequest.getPluginToSendResponseThrough();
        List<ValidationQualityAnalysisType> validationResults = respondToInvalidMessageRequest.getValidationResults();
        String sender = respondToInvalidMessageRequest.getSender();
        String messageGuid = respondToInvalidMessageRequest.getMessageGuid();

        invalidMessageService.sendResponseToInvalidIncomingMessage(messageGuid, validationResults, sender, pluginToSendResponseThrough);
    }

    public void respondToFindReportMessage(@Observes @FindReportReceivedEvent EventMessage event) {
        FindReportByIdRequest request = ((FindReportByIdRequest) event.getSalesBaseRequest());
        Report report = reportService.findByExtIdOrNull(request.getId());

        try {
            String marshalledReport = "";

            if (report != null) {
                marshalledReport = JAXBMarshaller.marshallJaxBObjectToString(report.getFLUXSalesReportMessage());
            }

            String marshalledFindReportByIdResponse = SalesModuleRequestMapper.createFindReportByIdResponse(marshalledReport);
            salesMessageProducer.sendModuleResponseMessage(event.getJmsMessage(), marshalledFindReportByIdResponse);
        } catch (SalesMarshallException e) {
            throw new SalesServiceException("Something went wrong during marshalling of a FindReportById", e);
        } catch (MessageException e) {
            throw new SalesServiceException("Something went wrong while sending a findReportByIdResponse", e);
        }

    }

    public void respondToUniqueIdMessage(@Observes @UniqueIdReceivedEvent EventMessage event) {
        CheckForUniqueIdRequest request = ((CheckForUniqueIdRequest) event.getSalesBaseRequest());

        Boolean response = false;

        switch (request.getType()) {
            case TRANSPORT_DOCUMENT:
                break;
            case SALES_DOCUMENT:
                response = !uniqueIdService.doesAnySalesDocumentExistWithAnyOfTheseIds(request.getIds());
                break;
            case TAKEOVER_DOCUMENT:
                response = !uniqueIdService.doesAnyTakeOverDocumentExistWithAnyOfTheseIds(request.getIds());
                break;
            case SALES_QUERY:
                break;
            default:
                throw new RuntimeException("No case implemented for " + request.getType());
        }

        try {
            String checkForUniqueIdResponse = SalesModuleRequestMapper.createCheckForUniqueIdResponse(response);
            salesMessageProducer.sendModuleResponseMessage(event.getJmsMessage(), checkForUniqueIdResponse);
        } catch (SalesMarshallException e) {
            throw new SalesServiceException("Something went wrong during marshalling of a uniqueIdResponse", e);
        } catch (MessageException e) {
            throw new SalesServiceException("Something went wrong while sending a uniqueIdResponse", e);
        }

    }

    @Override
    public void returnError(@Observes @ErrorEvent EventMessage event) {
        salesMessageProducer.sendModuleErrorMessage(event);
        LOG.error(event.getErrorMessage());
    }
}
