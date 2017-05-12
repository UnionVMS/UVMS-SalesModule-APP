package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesReportMessage;
import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.sales.service.RulesService;

import javax.ejb.Stateless;

@Stateless
public class RulesServiceImpl implements RulesService {

    @Override
    public void sendResponseToRules(FLUXSalesResponseMessage response, String recipient) throws ServiceException {
        //TODO STIJN/MATHIAS
    }

    @Override
    public void sendReportToRules(FLUXSalesReportMessage report, String recipient) throws ServiceException {
        //TODO STIJN/MATHIAS
    }
}
