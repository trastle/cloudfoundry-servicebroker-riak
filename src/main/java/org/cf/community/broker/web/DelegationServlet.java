package org.cf.community.broker.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This servlet sucks.
 *
 * This is a hack to get around the fact that Servlets were a bad choice here.
 * There is no way to make these decisions using URL mappings in the web.xml so I am making them here, damn.
 *
 * TODO Replace the whole servlet layer with Play!
 *
 * User: tastle
 * Date: 12/10/2013
 * Time: 12:46
 */
public class DelegationServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(DelegationServlet.class);

    /**
     * Select between the following and delegate:
     *
     * Service Creation:
     * POST /gateway/v1/configurations
     *
     * Service Binding:
     * POST /gateway/v1/configurations/:service_id/handles
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<String> tokens = DelegationServlet.getPathTokens(request);

        if(tokens.size() == 3){
            // Create Service
            new ServiceCreationServlet().doPost(request, response);
            return;
        } else if (tokens.size() == 5) {
            // Bind Service
            new ServiceBindingServlet().doPost(request, response);
            return;
        }

        logger.info("doPost could not delegate, returning 404.");
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * Select between the following and delegate:
     *
     * Delete service:
     * DELETE /gateway/v1/configurations/:service_id
     *
     * Unbind Service:
     * DELETE /gateway/v1/configurations/:service_id/handles/:handle_id
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doDelete (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<String> tokens = DelegationServlet.getPathTokens(request);

        if(tokens.size() == 4){
            // Create Service
            new ServiceCreationServlet().doPost(request, response);
            return;
        } else if (tokens.size() == 6) {
            // Bind Service
            new ServiceBindingServlet().doPost(request, response);
            return;
        }

        logger.info("doDelete could not delegate, returning 404.");
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * Tokenize the URL path.
     *
     * @param request the servlet request that cane into this servlet
     * @return a list of all the "/" separated tokens from the URL.
     */
    public static List<String> getPathTokens(HttpServletRequest request){

        List<String> tokens = Collections.emptyList();
        String path = request.getServletPath();

        if (path != null) {

            // Avoid leading and trailing empty strings.
            path = path.trim().replaceFirst("^/", "").replaceFirst("/$", "");
            tokens = Arrays.asList(path.split("/"));
        }

        logger.debug("Returning: " + tokens);
        return tokens;
    }
}
