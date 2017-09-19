package eu.europa.ec.fisheries.uvms.sales.service;

import eu.europa.ec.fisheries.uvms.sales.domain.constant.ParameterKey;

/**
 * When starting up Sales, the settings in the config module are synced with the Sales parameter table.
 * This means that the most up to date settings can be found in the parameter table.
 *
 * Config provides a ParameterService to retrieve this configuration from the Sales parameter table.
 * This service acts as a kind wrapper to invoke the Config ParameterService, to avoid having a hard dependency
 * on Config everywhere.
 */
public interface ConfigService {

    String getParameter(ParameterKey parameterKey);

}
