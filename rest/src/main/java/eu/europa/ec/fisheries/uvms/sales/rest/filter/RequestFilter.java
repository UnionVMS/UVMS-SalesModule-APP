package eu.europa.ec.fisheries.uvms.sales.rest.filter;

import eu.europa.ec.fisheries.uvms.sales.rest.constants.RestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 *
 * @author jojoha
 */
@WebFilter("/*")
public class RequestFilter implements Filter {

    final static Logger LOG = LoggerFactory.getLogger(RequestFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.info("Request filter starting up");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader(RestConstants.ACCESS_CONTROL_ALLOW_ORIGIN, RestConstants.ACCESS_CONTROL_ALLOW_METHODS_ALL);
        response.setHeader(RestConstants.ACCESS_CONTROL_ALLOW_METHODS, RestConstants.ACCESS_CONTROL_ALLOWED_METHODS);
        response.setHeader(RestConstants.ACCESS_CONTROL_ALLOW_HEADERS, RestConstants.ACCESS_CONTROL_ALLOW_HEADERS_ALL);
        chain.doFilter(request, res);
    }

    @Override
    public void destroy() {
        LOG.info("Request filter shutting down");
    }

}
