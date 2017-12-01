/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.message.consumer.bean;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConsumer;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractConsumer;

import javax.ejb.Stateless;

@Stateless
public class SalesMessageConsumerBean extends AbstractConsumer implements MessageConsumer {

    @Override
    public String getDestinationName() {
        return MessageConstants.QUEUE_SALES;
    }

}
