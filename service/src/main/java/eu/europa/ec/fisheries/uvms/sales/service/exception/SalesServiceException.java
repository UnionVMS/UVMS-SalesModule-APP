package eu.europa.ec.fisheries.uvms.sales.service.exception;

public class SalesServiceException extends RuntimeException {

    public SalesServiceException(String s) {
        super(s);
    }

    public SalesServiceException(String s, Throwable throwable) {
        super(s, throwable);
    }

}
