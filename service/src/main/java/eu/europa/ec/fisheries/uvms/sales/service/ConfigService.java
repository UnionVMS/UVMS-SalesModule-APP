package eu.europa.ec.fisheries.uvms.sales.service;

import eu.europa.ec.fisheries.uvms.sales.domain.constant.ParameterKey;

/**
 * A temporary hack. To sync settings with config, we ought to use
 * the UVMS-ConfigLibrary, as described here:
 * https://focusfish.atlassian.net/wiki/display/UVMS/Config.
 *
 * Though, we could not get this library to work. Strangely,
 * when config sends the settings to sales, and sales wants to persist
 * the settings in its parameter table, no transaction is active.
 *
 * In order to move forward, this temporary hack is implemented: to
 * retrieve each setting live from config.
 */
public interface ConfigService {

    String getParameter(ParameterKey parameterKey);
}
