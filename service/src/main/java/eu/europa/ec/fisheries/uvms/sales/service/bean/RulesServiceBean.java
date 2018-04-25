package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMarshallException;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.RulesModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.FLUXSalesResponseMessageHelper;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesMarshallException;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.sales.service.ConfigService;
import eu.europa.ec.fisheries.uvms.sales.service.RulesService;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;

@Stateless
@Slf4j
public class RulesServiceBean implements RulesService {

    @EJB
    private SalesMessageProducer messageProducer;

    @EJB
    private ConfigService configService;

    @EJB
    private FLUXSalesResponseMessageHelper responseHelper;

    @EJB
    private ReportHelper reportHelper;

    @Override
    public void sendResponseToRules(FLUXSalesResponseMessage response, String recipient, String pluginToSendResponseThrough) {
        try {
            String responseAsString = JAXBMarshaller.marshallJaxBObjectToString(response);
            String dataFlow = getFluxDataFlow();
            Date now = new Date();
            String responseGuid = responseHelper.getId(response);

            String request = RulesModuleRequestMapper.createSendSalesResponseRequest(responseAsString, responseGuid, recipient, pluginToSendResponseThrough, dataFlow, now);
            String messageSelector = "SendSalesResponseRequest";
            messageProducer.sendModuleMessage(request, Union.RULES, messageSelector);

        } catch (RulesModelMarshallException | SalesMarshallException | MessageException e) {
            throw new SalesServiceException("Could not send the sales response to Rules", e);
        }
    }

    @Override
    public void sendReportToRules(FLUXSalesReportMessage fluxSalesReportMessage, String recipient, String pluginToSendResponseThrough) {
        try {
            Report report = new Report().withFLUXSalesReportMessage(fluxSalesReportMessage);
            String reportAsString = JAXBMarshaller.marshallJaxBObjectToString(report);
            String fluxDataFlow = getFluxDataFlow();
            Date now = new Date();
            String reportGuid = reportHelper.getId(fluxSalesReportMessage);

            String request = RulesModuleRequestMapper.createSendSalesReportRequest(reportAsString, reportGuid, recipient, pluginToSendResponseThrough, fluxDataFlow, now);
            String messageSelector = "SendSalesReportRequest";
            messageProducer.sendModuleMessage(request, Union.RULES);

        } catch (RulesModelMarshallException | SalesMarshallException | MessageException e) {
            throw new SalesServiceException("Could not send the sales report to Rules", e);
        }
    }


    private String getFluxDataFlow() {
        return configService.getParameter(ParameterKey.FLUX_DATA_FLOW);
    }
}
