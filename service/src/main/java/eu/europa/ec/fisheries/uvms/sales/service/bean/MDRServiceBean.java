/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.uvms.mdr.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.mdr.model.mapper.MdrModuleMapper;
import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.consumer.SalesMessageConsumer;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.service.MDRService;
import eu.europa.ec.fisheries.uvms.sales.service.constants.MDRCodeListKey;
import lombok.SneakyThrows;
import un.unece.uncefact.data.standard.mdr.communication.MdrGetCodeListResponse;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.jms.TextMessage;
import java.util.List;

@Singleton
public class MDRServiceBean implements MDRService {

    @EJB
    private SalesMessageConsumer consumer;

    @EJB
    private SalesMessageProducer producer;

    @SneakyThrows
    public List<ObjectRepresentation> findCodeList(MDRCodeListKey acronym) {
        String request = MdrModuleMapper.createFluxMdrGetCodeListRequest(acronym.getInternalName());
        String correlationId = producer.sendModuleMessage(request, Union.MDR);
        TextMessage message = consumer.getMessage(correlationId, TextMessage.class);


        MdrGetCodeListResponse response = JAXBMarshaller.unmarshallTextMessage(message.getText(), MdrGetCodeListResponse.class);
        return response.getDataSets();
    }
}