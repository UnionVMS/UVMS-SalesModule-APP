package eu.europa.ec.fisheries.uvms.sales.message.constants;

//TODO: move to MessageConstants in UVMS-commons, when we have Git
public interface SalesMessageConstants {

    String INTERNAL_QUEUE_JNDI = "java:/jms/queue/UVMSSales";

    String EVENT_QUEUE_JNDI = "java:/jms/queue/UVMSSalesEvent";
    String EVENT_QUEUE_NAME = "UVMSSalesEvent";

    String QUEUE_ECB_PROXY = "java:/jms/queue/UVMSSalesEcbProxy";
}
