package eu.europa.ec.fisheries.uvms.sales.domain.event;

public class EventFault {

    private Integer code;
    private String message;

    public EventFault() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
