package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.exchange.v1.ExchangeLogStatusTypeType;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils;
import eu.europa.ec.fisheries.uvms.exchange.model.exception.ExchangeModelMarshallException;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMarshallException;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.RulesModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.message.producer.bean.ExchangeMessageProducerBean;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesMarshallException;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.sales.service.ConfigService;
import eu.europa.ec.fisheries.uvms.sales.service.ExchangeService;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.Queue;
import java.util.Date;

@Stateless
public class ExchangeServiceBean implements ExchangeService {

    @EJB
    private ConfigService configService;

    @EJB
    private ReportHelper reportHelper;

    @EJB
    private ExchangeMessageProducerBean messageProducer;

    private Queue replyToSalesQueue;

    @PostConstruct
    public void init() {
        this.replyToSalesQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_SALES);
    }


    @Override
    public void sendReport(FLUXSalesReportMessage fluxSalesReportMessage, String recipient, String pluginToSendResponseThrough) {
        try {
            Report report = new Report().withFLUXSalesReportMessage(fluxSalesReportMessage);
            String reportAsString = JAXBMarshaller.marshallJaxBObjectToString(report);
            String fluxDataFlow = getFluxDataFlow();
            Date now = new Date();
            String reportGuid = reportHelper.getId(fluxSalesReportMessage);


            String request = ExchangeModuleRequestMapper.createSendSalesReportRequest(reportAsString,
                    reportGuid,
                    fluxDataFlow,
                    recipient,
                    now,
                    ExchangeLogStatusTypeType.SUCCESSFUL,
                    pluginToSendResponseThrough);

            messageProducer.sendModuleMessage(request, replyToSalesQueue);
        } catch (ExchangeModelMarshallException | SalesMarshallException | MessageException e) {
            throw new SalesServiceException("Could not send the sales report to Exchange", e);
        }
    }

    private String getFluxDataFlow() {
        return configService.getParameter(ParameterKey.FLUX_DATA_FLOW);
    }

}
