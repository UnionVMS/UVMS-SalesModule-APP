package eu.europa.ec.fisheries.uvms.sales.domain.event;

import javax.jms.TextMessage;

public class DomainEventMessage {

    private TextMessage requestMessage;
    private String responseMessage;
    private EventFault errorMessage;

    public DomainEventMessage(TextMessage requestMessage, EventFault errorMessage) {
        this.requestMessage = requestMessage;
        this.errorMessage = errorMessage;
    }

    public DomainEventMessage(TextMessage requestMessage, String responseMessage) {
        this.requestMessage = requestMessage;
        this.responseMessage = responseMessage;
    }

    public TextMessage getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(TextMessage requestMessage) {
        this.requestMessage = requestMessage;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public EventFault getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(EventFault errorMessage) {
        this.errorMessage = errorMessage;
    }

}
