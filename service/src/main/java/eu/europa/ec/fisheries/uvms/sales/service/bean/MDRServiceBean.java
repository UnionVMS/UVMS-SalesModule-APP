/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConsumer;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageException;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JAXBUtils;
import eu.europa.ec.fisheries.uvms.mdr.model.exception.MdrModelMarshallException;
import eu.europa.ec.fisheries.uvms.mdr.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.mdr.model.mapper.MdrModuleMapper;
import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;
import eu.europa.ec.fisheries.uvms.sales.service.MDRService;
import eu.europa.ec.fisheries.uvms.sales.service.constants.MDRCodeListKey;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.mdr.communication.MdrGetCodeListResponse;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;
import java.util.List;

@Slf4j
@Singleton
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class MDRServiceBean implements MDRService {

    private static final long TIMEOUT = 180000;

    @EJB
    private MessageConsumer consumer;

    @EJB
    private SalesMessageProducer producer;

    public List<ObjectRepresentation> findCodeList(MDRCodeListKey acronym) {
        try {
            String request = JAXBMarshaller.marshallJaxBObjectToString(MdrModuleMapper.createFluxMdrGetCodeListRequest(acronym.getInternalName()));
            log.info("Send MdrGetCodeListRequest message to MDR. Acronym: " + acronym.name());
            String correlationId = producer.sendModuleMessage(request, Union.MDR);

            TextMessage message = consumer.getMessage(correlationId, TextMessage.class, TIMEOUT);
            log.info("Received response message");
            MdrGetCodeListResponse response = JAXBUtils.unMarshallMessage(message.getText(), MdrGetCodeListResponse.class);
            return response.getDataSets();
        } catch (MdrModelMarshallException | JAXBException | MessageException | JMSException e) {
            throw new SalesNonBlockingException("Exception thrown when retrieving codelist '" + acronym.getInternalName() + "' from MDR" , e);
        }
    }
}