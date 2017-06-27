/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.uvms.config.model.exception.ModelMapperException;
import eu.europa.ec.fisheries.uvms.config.model.mapper.ModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.config.model.mapper.ModuleResponseMapper;
import eu.europa.ec.fisheries.uvms.message.MessageException;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.ParameterKey;
import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.consumer.SalesMessageConsumer;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.service.ConfigService;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.List;

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
@Stateless
@Slf4j
public class ConfigServiceBean implements ConfigService {

    @EJB
    private SalesMessageConsumer consumer;

    @EJB
    private SalesMessageProducer producer;

    public String getParameter(ParameterKey parameterKey) {
        try {
            String request = ModuleRequestMapper.toPullSettingsRequest("sales");
            String jmsMessageID = producer.sendModuleMessage(request, Union.CONFIG);
            TextMessage message = consumer.getMessage(jmsMessageID, TextMessage.class);
            List<SettingType> settingTypeList = ModuleResponseMapper.getSettingsFromPullSettingsResponse(message);
            for(SettingType setting : settingTypeList){
                if(parameterKey.getKey().equals(setting.getKey())){
                    return setting.getValue();
                }
            }
        } catch (MessageException | JMSException | ModelMapperException e) {
            throw new SalesServiceException("Could not retrieve a setting with key " + parameterKey.getKey() + " from Config.", e);
        }
        return null;
    }
}