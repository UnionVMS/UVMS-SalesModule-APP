package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import eu.europa.ec.fisheries.uvms.config.constants.ConfigHelper;
import eu.europa.ec.fisheries.uvms.sales.model.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.service.constants.ServiceConstants;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class SalesConfigHelperBean implements ConfigHelper {

    @Override
    public List<String> getAllParameterKeys() {
        List<String> allParameterKeys = new ArrayList<>();
        for (ParameterKey parameterKey : ParameterKey.values()) {
            allParameterKeys.add(parameterKey.getKey());
        }

        return allParameterKeys;
    }

    @Override
    public String getModuleName() {
        return ServiceConstants.SALES_CONFIG_NAME;
    }
}
