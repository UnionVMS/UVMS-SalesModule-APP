/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesQueryMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.SalesQueryRequest;
import eu.europa.ec.fisheries.schema.sales.SalesReportRequest;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.sales.message.event.ErrorEvent;
import eu.europa.ec.fisheries.uvms.sales.message.event.MessageReceivedEvent;
import eu.europa.ec.fisheries.uvms.sales.message.event.QueryReceivedEvent;
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
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.inject.Inject;


@Stateless
public class EventServiceBean implements EventService {

    final static Logger LOG = LoggerFactory.getLogger(EventServiceBean.class);

    @Inject
    ReportService reportService;

    @EJB
    private SalesMessageProducer salesMessageProducer;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createReport(@Observes @MessageReceivedEvent EventMessage message) {
        try {
            SalesReportRequest salesReportRequest = (SalesReportRequest) message.getSalesBaseRequest();
            Report report = JAXBMarshaller.unmarshallString(salesReportRequest.getReport(), Report.class);
            reportService.saveReport(report);
        } catch (SalesMarshallException e) {
            LOG.error("Something went wrong during unmarshalling of a sales report", e);
        } catch (ServiceException e) {
            LOG.error("Something went wrong when saving a sales report", e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void executeQuery(@Observes @QueryReceivedEvent EventMessage message) throws ServiceException {
        SalesQueryRequest salesReportRequest = (SalesQueryRequest) message.getSalesBaseRequest();

        FLUXSalesQueryMessage salesQueryType;
        try {
            salesQueryType = JAXBMarshaller.unmarshallString(salesReportRequest.getQuery(), FLUXSalesQueryMessage.class);
        } catch (SalesMarshallException e) {
            LOG.error("Something went wrong during unmarshalling of a sales query", e);
            return;
        }

        reportService.search(salesQueryType);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void returnError(@Observes @ErrorEvent EventMessage message) {
        salesMessageProducer.sendModuleErrorMessage(message);
        LOG.info("Received Error event in Sales");
    }
}
