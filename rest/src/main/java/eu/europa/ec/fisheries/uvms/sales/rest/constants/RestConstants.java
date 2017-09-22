package eu.europa.ec.fisheries.uvms.sales.rest.constants;


public class RestConstants {

    private RestConstants() {
        //Sonar wanted a private constructor here, but it'll start crying about an empty method if I don't do this
    }

    public static final String MODULE_REST = "/rest";
    public static final String MODULE_NAME = "/sales";

    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_ALLOW_METHODS_ALL = "*";

    public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String ACCESS_CONTROL_ALLOWED_METHODS = "GET, POST, DELETE, PUT, OPTIONS";

    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS_ALL = "Content-Type";
}
