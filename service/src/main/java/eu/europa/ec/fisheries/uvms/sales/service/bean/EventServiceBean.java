/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.config.exception.ConfigServiceException;
import eu.europa.ec.fisheries.uvms.sales.message.event.*;
import eu.europa.ec.fisheries.uvms.sales.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesMarshallException;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.SalesModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.sales.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import java.util.List;


@Stateless
public class EventServiceBean implements EventService {

    static final Logger LOG = LoggerFactory.getLogger(EventServiceBean.class);

    @EJB
    private ReportService reportService;

    @EJB
    private UnsavedMessageService unsavedMessageService;

    @EJB
    private SalesMessageProducer salesMessageProducer;

    @EJB
    private UniqueIdService uniqueIdService;

    @EJB
    private QueryService queryServiceBean;

    @Override
    public void createReport(@Observes @ReportReceivedEvent EventMessage event) {
        try {
            SalesReportRequest salesReportRequest = (SalesReportRequest) event.getSalesBaseRequest();

            Report report = JAXBMarshaller.unmarshallString(salesReportRequest.getReport(), Report.class);
            List<ValidationQualityAnalysisType> validationResults = salesReportRequest.getValidationQualityAnalysises();
            String pluginToSendResponseThrough = salesReportRequest.getPluginToSendResponseThrough();
            String messageValidationResult = salesReportRequest.getMessageValidationStatus();

            reportService.saveReport(report, pluginToSendResponseThrough, validationResults, messageValidationResult);
        } catch (SalesMarshallException e) {
            throw new SalesServiceException("Something went wrong during unmarshalling of a sales report", e);
        } catch (SalesServiceException | ConfigServiceException  e) {
            throw new SalesServiceException("Something went wrong when saving a sales report", e);
        }
    }

    public void executeQuery(@Observes @QueryReceivedEvent EventMessage event) {
        SalesQueryRequest salesQueryRequest = (SalesQueryRequest) event.getSalesBaseRequest();

        try {
            FLUXSalesQueryMessage salesQueryType = JAXBMarshaller.unmarshallString(salesQueryRequest.getQuery(), FLUXSalesQueryMessage.class);
            String pluginToSendResponseThrough = salesQueryRequest.getPluginToSendResponseThrough();
            List<ValidationQualityAnalysisType> validationResults = salesQueryRequest.getValidationQualityAnalysises();
            String messageValidationResult = salesQueryRequest.getMessageValidationStatus();

            queryServiceBean.saveQuery(salesQueryType.getSalesQuery());
            reportService.search(salesQueryType, pluginToSendResponseThrough, validationResults, messageValidationResult);
        } catch (SalesMarshallException e) {
            throw new SalesServiceException("Something went wrong during unmarshalling of a sales query", e);
        } catch (SalesNonBlockingException e) {
            LOG.error("Something went wrong while executing the incoming query", e);
        }
    }

    public void respondToInvalidMessage(@Observes @InvalidMessageReceivedEvent EventMessage event) {
        RespondToInvalidMessageRequest respondToInvalidMessageRequest = (RespondToInvalidMessageRequest) event.getSalesBaseRequest();

        String pluginToSendResponseThrough = respondToInvalidMessageRequest.getPluginToSendResponseThrough();
        List<ValidationQualityAnalysisType> validationResults = respondToInvalidMessageRequest.getValidationQualityAnalysises();
        String sender = respondToInvalidMessageRequest.getSender();
        String messageGuid = respondToInvalidMessageRequest.getMessageGuid();
        String schemeId = determineSchemeId(respondToInvalidMessageRequest);

        unsavedMessageService.sendResponseToInvalidIncomingMessage(messageGuid, validationResults, sender, pluginToSendResponseThrough, schemeId);
    }

    private String determineSchemeId(RespondToInvalidMessageRequest respondToInvalidMessageRequest) {
        switch (respondToInvalidMessageRequest.getTypeOfId()) {
            case GUID:
                return "UUID";
            case FLUXTL_ON:
                return "FLUXTL_ON";
            default:
                throw new SalesServiceException("No case implemented for " + respondToInvalidMessageRequest.getTypeOfId());
        }
    }

    public void respondToFindReportMessage(@Observes @FindReportReceivedEvent EventMessage event) {
        FindReportByIdRequest request = ((FindReportByIdRequest) event.getSalesBaseRequest());
        Report report = reportService.findByExtId(request.getId())
                .orElse(null);

        try {
            String marshalledReport = "";

            if (report != null) {
                marshalledReport = JAXBMarshaller.marshallJaxBObjectToString(report.getFLUXSalesReportMessage());
            }

            String marshalledFindReportByIdResponse = SalesModuleRequestMapper.createFindReportByIdResponse(marshalledReport);
            LOG.info("Send FindReportByIdResponse message to requester");
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

        LOG.debug("Unique ID check for IDS '" + request.getIds() + "' with type " + request.getType());

        switch (request.getType()) {
            case TRANSPORT_DOCUMENT:
                break;
            case SALES_DOCUMENT:
                response = !uniqueIdService.doesAnySalesDocumentExistWithAnyOfTheseIds(request.getIds());
                break;
            case SALES_REPORT:
                response = !uniqueIdService.doesAnySalesReportExistWithAnyOfTheseIds(request.getIds());
                break;
            case SALES_QUERY:
                response = uniqueIdService.isQueryIdUnique(request.getIds().get(0));
                break;
            case SALES_RESPONSE:
                response = uniqueIdService.isResponseIdUnique(request.getIds().get(0));
                break;
            case SALES_REFERENCED_ID:
                response = !uniqueIdService.doesReferencedReportExist(request.getIds().get(0));
                break;
            default:
                throw new SalesServiceException("No case implemented for " + request.getType());
        }

        LOG.debug("Were the IDS unique? " + response);

        try {
            String checkForUniqueIdResponse = SalesModuleRequestMapper.createCheckForUniqueIdResponse(response);
            LOG.info("Send CheckForUniqueIdResponse message to requester");
            salesMessageProducer.sendModuleResponseMessage(event.getJmsMessage(), checkForUniqueIdResponse);
        } catch (SalesMarshallException e) {
            throw new SalesServiceException("Something went wrong during marshalling of a uniqueIdResponse", e);
        } catch (MessageException e) {
            throw new SalesServiceException("Something went wrong while sending a uniqueIdResponse", e);
        }

    }

    @Override
    public void returnError(@Observes @ErrorEvent EventMessage event) {
        LOG.error(event.getErrorMessage());
        try {
            salesMessageProducer.sendModuleErrorMessage(event);

        } catch (MessageException e) {
            throw new SalesServiceException("Unable to send module error message.", e);
        }
    }
}
