package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.message.MessageException;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMarshallException;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.RulesModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.model.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesMarshallException;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.sales.model.remote.ParameterService;
import eu.europa.ec.fisheries.uvms.sales.service.RulesService;
import eu.europa.ec.fisheries.uvms.sales.service.bean.helper.FLUXSalesResponseMessageHelper;
import eu.europa.ec.fisheries.uvms.sales.service.bean.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.service.constants.ServiceConstants;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;

@Stateless
public class RulesServiceBean implements RulesService {

    @EJB
    private SalesMessageProducer messageProducer;

    @EJB(lookup = ServiceConstants.DB_ACCESS_PARAMETER_SERVICE)
    private ParameterService parameterService;

    @EJB
    private FLUXSalesResponseMessageHelper responseHelper;

    @EJB
    private ReportHelper reportHelper;

    @Override
    public void sendResponseToRules(FLUXSalesResponseMessage response, String recipient, String pluginToSendResponseThrough) throws ServiceException {
        try {
            String responseAsString = JAXBMarshaller.marshallJaxBObjectToString(response);
            String dataFlow = getFluxDataFlow();
            Date now = new Date();
            String responseGuid = responseHelper.getId(response);

            String request = RulesModuleRequestMapper.createSendSalesResponseRequest(responseAsString, responseGuid, recipient, pluginToSendResponseThrough, dataFlow, now);
            messageProducer.sendModuleMessage(request, Union.RULES);
        } catch (RulesModelMarshallException | SalesMarshallException | MessageException e) {
            throw new ServiceException("Could not send the sales response to Rules", e);
        }

    }

    @Override
    public void sendReportToRules(FLUXSalesReportMessage report, String recipient, String pluginToSendResponseThrough) throws ServiceException {
        try {
            String reportAsString = JAXBMarshaller.marshallJaxBObjectToString(report);
            String fluxDataFlow = getFluxDataFlow();
            Date now = new Date();
            String reportGuid = reportHelper.getId(report);

            String request = RulesModuleRequestMapper.createSendSalesReportRequest(reportAsString, reportGuid, recipient, pluginToSendResponseThrough, fluxDataFlow, now);
            messageProducer.sendModuleMessage(request, Union.RULES);
        } catch (RulesModelMarshallException | SalesMarshallException | MessageException e) {
            throw new ServiceException("Could not send the sales report to Rules", e);
        }
    }


    private String getFluxDataFlow() {
        return parameterService.getParameterValue(ParameterKey.FLUX_DATA_FLOW);
    }
}
