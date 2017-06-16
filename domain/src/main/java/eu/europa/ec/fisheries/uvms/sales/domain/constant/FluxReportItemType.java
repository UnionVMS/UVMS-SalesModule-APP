package eu.europa.ec.fisheries.uvms.sales.domain.constant;

public enum FluxReportItemType {
    SALES_NOTE("SN"),
    TAKE_OVER_DOCUMENT("TOD");

    private final String code;

    FluxReportItemType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static FluxReportItemType forCode(String code) {
        if (code == null) {
            return null;
        }

        for (FluxReportItemType itemType : FluxReportItemType.values()) {
            if (itemType.code.equalsIgnoreCase(code)) {
                return itemType;
            }
        }
        throw new IllegalArgumentException("No FluxReportItemType exists for code " + code);
    }
}
