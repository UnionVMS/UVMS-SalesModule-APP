/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.sales.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.sales.message.event.QueryReceivedEvent;
import eu.europa.ec.fisheries.uvms.sales.message.event.ReportReceivedEvent;
import eu.europa.ec.fisheries.uvms.sales.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesMarshallException;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.sales.service.EventService;
import eu.europa.ec.fisheries.uvms.sales.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.List;


@Stateless
public class EventServiceBean implements EventService {

    final static Logger LOG = LoggerFactory.getLogger(EventServiceBean.class);

    @Inject
    ReportService reportService;

    @EJB
    private SalesMessageProducer salesMessageProducer;

    @Override
    public void createReport(@Observes @ReportReceivedEvent EventMessage message) {
        try {
            SalesReportRequest salesReportRequest = (SalesReportRequest) message.getSalesBaseRequest();

            Report report = JAXBMarshaller.unmarshallString(salesReportRequest.getReport(), Report.class);
            List<ValidationQualityAnalysisType> validationResults = salesReportRequest.getValidationResults();
            String pluginToSendResponseThrough = salesReportRequest.getPluginToSendResponseThrough();
            String messageValidationResult = salesReportRequest.getMessageValidationStatus().name();

            reportService.saveReport(report, pluginToSendResponseThrough, validationResults, messageValidationResult);
        } catch (SalesMarshallException e) {
            LOG.error("Something went wrong during unmarshalling of a sales report", e);
        } catch (ServiceException e) {
            LOG.error("Something went wrong when saving a sales report", e);
        }
    }

    public void executeQuery(@Observes @QueryReceivedEvent EventMessage message) throws ServiceException {
        SalesQueryRequest salesQueryRequest = (SalesQueryRequest) message.getSalesBaseRequest();

        try {
            FLUXSalesQueryMessage salesQueryType = JAXBMarshaller.unmarshallString(salesQueryRequest.getQuery(), FLUXSalesQueryMessage.class);
            String pluginToSendResponseThrough = salesQueryRequest.getPluginToSendResponseThrough();
            List<ValidationQualityAnalysisType> validationResults = salesQueryRequest.getValidationResults();
            String messageValidationResult = salesQueryRequest.getMessageValidationStatus().name();

            reportService.search(salesQueryType, pluginToSendResponseThrough, validationResults, messageValidationResult);
        } catch (SalesMarshallException e) {
            LOG.error("Something went wrong during unmarshalling of a sales query", e);
            return;
        }
    }

    @Override
    public void returnError(@Observes @ErrorEvent EventMessage message) {
        salesMessageProducer.sendModuleErrorMessage(message);
        LOG.info("Received Error event in Sales");
    }
}
