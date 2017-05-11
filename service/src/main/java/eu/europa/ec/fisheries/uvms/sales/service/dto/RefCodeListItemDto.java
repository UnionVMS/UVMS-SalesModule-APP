package eu.europa.ec.fisheries.uvms.sales.service.dto;

public class RefCodeListItemDto {
    private String code;
    private String text;

    public RefCodeListItemDto() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public RefCodeListItemDto code(final String code) {
        setCode(code);
        return this;
    }

    public RefCodeListItemDto text(final String text) {
        setText(text);
        return this;
    }
}
