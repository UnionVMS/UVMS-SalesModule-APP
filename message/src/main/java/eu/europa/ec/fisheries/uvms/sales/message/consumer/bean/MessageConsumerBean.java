package eu.europa.ec.fisheries.uvms.sales.message.consumer.bean;

import eu.europa.ec.fisheries.schema.sales.SalesBaseRequest;
import eu.europa.ec.fisheries.schema.sales.SalesModuleMethod;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.context.MappedDiagnosticContext;
import eu.europa.ec.fisheries.uvms.sales.message.event.*;
import eu.europa.ec.fisheries.uvms.sales.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesMarshallException;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.JAXBMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(mappedName = MessageConstants.QUEUE_SALES_EVENT, activationConfig = {
    @ActivationConfigProperty(propertyName = "messagingType", propertyValue = MessageConstants.CONNECTION_TYPE),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = MessageConstants.DESTINATION_TYPE_QUEUE),
    @ActivationConfigProperty(propertyName = "destination", propertyValue = "UVMSSalesEvent")
})
public class MessageConsumerBean implements MessageListener {

    static final Logger LOG = LoggerFactory.getLogger(MessageConsumerBean.class);

    @Inject
    @ReportReceivedEvent
    private Event<EventMessage> reportReceivedEvent;

    @Inject
    @QueryReceivedEvent
    private Event<EventMessage> queryReceivedEvent;

    @Inject
    @InvalidMessageReceivedEvent
    private Event<EventMessage> invalidMessageReceivedEvent;

    @Inject
    @FindReportReceivedEvent
    private Event<EventMessage> findReportReceivedEvent;

    @Inject
    @UniqueIdReceivedEvent
    private Event<EventMessage> uniqueIdReceivedEvent;

    @Inject
    @ErrorEvent
    private Event<EventMessage> errorEvent;

    @Override
    public void onMessage(Message message) {
        MDC.remove("requestId");
        LOG.info("Message received in sales. Times redelivered: " + getTimesRedelivered(message));
        TextMessage textMessage = (TextMessage) message;
        MappedDiagnosticContext.addMessagePropertiesToThreadMappedDiagnosticContext(textMessage);
        SalesBaseRequest salesRequest = null;

        try {
            salesRequest = JAXBMarshaller.unmarshallTextMessage(textMessage, SalesBaseRequest.class);
        } catch (SalesMarshallException e) {
            LOG.error("[ Error when unmarshalling SalesBaseRequest in sales: ] {}", e.getStackTrace());
            errorEvent.fire(new EventMessage(textMessage, "Invalid content in message: " + textMessage));
            return;
        }

        SalesModuleMethod method = salesRequest.getMethod();
        if (method == null) {
            errorEvent.fire(new EventMessage(textMessage, "Invalid method 'null' in SalesBaseRequest. Did you add the enum value to the contract?"));
            return;
        }

        EventMessage eventWithOriginalJmsMessage = new EventMessage(salesRequest);
        eventWithOriginalJmsMessage.setJmsMessage(textMessage);

        switch (method) {
            case SAVE_REPORT: reportReceivedEvent.fire(new EventMessage(salesRequest)); break;
            case QUERY: queryReceivedEvent.fire(new EventMessage(salesRequest)); break;
            case FIND_REPORT_BY_ID:
                findReportReceivedEvent.fire(eventWithOriginalJmsMessage); break;
            case CHECK_UNIQUE_ID:
                uniqueIdReceivedEvent.fire(eventWithOriginalJmsMessage); break;
            case CREATE_INVALID_MESSAGE: invalidMessageReceivedEvent.fire(new EventMessage(salesRequest)); break;
            default: errorEvent.fire(new EventMessage(textMessage, "Invalid method '" + method + "' in SalesBaseRequest")); break;
        }
    }

    private int getTimesRedelivered(Message message) {
        try {
            return (message.getIntProperty("JMSXDeliveryCount") - 1);
        } catch (Exception e) {
            return 0;
        }
    }
}
