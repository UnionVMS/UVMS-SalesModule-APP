package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import eu.europa.ec.fisheries.schema.sales.FLUXSalesResponseMessage;

import javax.ejb.Stateless;

/**
 * Helper class to provide convenience methods to retrieve information from a sales response.
 * A more ideal solution would be: have these methods in the domain classes. But since we work with a generated
 * contract, this is not an option.
 */
@Stateless
public class FLUXSalesResponseMessageHelper {

    public String getId(FLUXSalesResponseMessage response) {
        return response.getFLUXResponseDocument().getIDS().get(0).getValue();
    }

}
