package eu.europa.ec.fisheries.uvms.sales.domain.constant;

public enum Purpose {
    ORIGINAL(9),
    CORRECTION(5),
    DELETE(3);

    private final int numericCode;

    Purpose(int numericCode) {
        this.numericCode = numericCode;
    }

    public int getNumericCode() {
        return numericCode;
    }

    public static Purpose forNumericCode(int numericCode) {
        for (Purpose purpose : Purpose.values()) {
            if (purpose.numericCode == numericCode) {
                return purpose;
            }
        }
        throw new IllegalArgumentException("No purpose code exists for number " + numericCode);
    }
}
