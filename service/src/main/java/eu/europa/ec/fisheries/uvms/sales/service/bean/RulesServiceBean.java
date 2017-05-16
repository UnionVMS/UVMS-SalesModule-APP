package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.message.MessageException;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMarshallException;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.RulesModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesMarshallException;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.sales.service.RulesService;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class RulesServiceBean implements RulesService {

    @EJB
    private SalesMessageProducer messageProducer;

    @Override
    public void sendResponseToRules(FLUXSalesResponseMessage response, String recipient) throws ServiceException {
        try {
            String responseAsString = JAXBMarshaller.marshallJaxBObjectToString(response);
            String request = RulesModuleRequestMapper.createSendSalesResponseRequest(responseAsString);
            messageProducer.sendModuleMessage(request, Union.RULES);
        } catch (RulesModelMarshallException | SalesMarshallException | MessageException e) {
            throw new ServiceException("Could not send the sales response to Rules", e);
        }

    }

    @Override
    public void sendReportToRules(FLUXSalesReportMessage report, String recipient) throws ServiceException {
        try {
            String reportAsString = JAXBMarshaller.marshallJaxBObjectToString(report);
            String request = RulesModuleRequestMapper.createSendSalesReportRequest(reportAsString);
            messageProducer.sendModuleMessage(request, Union.RULES);
        } catch (RulesModelMarshallException | SalesMarshallException | MessageException e) {
            throw new ServiceException("Could not send the sales report to Rules", e);
        }
    }
}
