/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.sales.message.event.carrier;

import eu.europa.ec.fisheries.schema.sales.SalesBaseRequest;

import javax.jms.TextMessage;

/**
 *
 * @author jojoha
 */
public class EventMessage {

    private TextMessage jmsMessage;
    private SalesBaseRequest salesBaseRequest;
    private String errorMessage;

    public EventMessage(SalesBaseRequest jmsMessage) {
        this.salesBaseRequest = jmsMessage;
    }

    public EventMessage(TextMessage jmsMessage, String errorMessage) {
        this.jmsMessage = jmsMessage;
        this.errorMessage = errorMessage;
    }

    public SalesBaseRequest getSalesBaseRequest() {
        return salesBaseRequest;
    }

    public void setSalesBaseRequest(SalesBaseRequest salesBaseRequest) {
        this.salesBaseRequest = salesBaseRequest;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public TextMessage getJmsMessage() {
        return jmsMessage;
    }

    public void setJmsMessage(TextMessage jmsMessage) {
        this.jmsMessage = jmsMessage;
    }

}
