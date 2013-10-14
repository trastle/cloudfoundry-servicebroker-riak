package org.cf.community.broker.web;

import cf.service.BindRequest;
import cf.service.BindResponse;
import org.cf.community.broker.core.BrokerConfig;
import org.cf.community.broker.core.BrokerEngine;
import org.cf.community.broker.riak.RiakProvisioner;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This servlet is the entry point for Binding and Unbinding Services.
 *
 * It takes care of the following:
 * 1) Make sure requests are authorized.
 * 2) Delegate to the BrokerEngine to do the real work.
 * 3) Setup the response to go back to the Cloud Controller
 *
 * User: tastle
 * Date: 09/10/2013
 * Time: 15:46
 */
public class ServiceBindingServlet extends AbstractServiceServlet {

    private static final Logger logger = LoggerFactory.getLogger(ServiceBindingServlet.class);

    /**
     * Binding
     * -------
     *
     * Binding is a developer-initiated intention (using cf bind-service) to make a service instance available to a
     * specific application, and it usually has side effects (such as creating new credentials for an application to
     * access a database). Like provisioning, the developer makes an API call to CC including the instance ID and app
     * ID to be bound, and CC makes an API call to the GW including just the service instance ID (gateways don’t know
     * about apps).
     *
     * The GW must then create a binding, if possible, and respond with both a GWID and credentials. For services where
     * there is only 1 set of credentials to a logical resource, binding can simply return the same credentials to each
     * application, though it will be impossible to properly revoke access to an application during unbind.
     *
     * To bind, CC sends a bind request to the gateway. The full request URL is determined by concatenating the URL in
     * the catalog + the path below. For legacy reasons, a binding is known as a “handle” on the gateway side.
     *
     * POST /gateway/v1/configurations/:service_id/handles
     *
     * @param request the Servlet request
     * @param response the Servlet response
     * @throws ServletException
     * @throws IOException
     */
    public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.info("Binding service...");

        // Build a server to do the work.
        BrokerEngine server = new BrokerEngine(new RiakProvisioner(), new BrokerConfig().getServiceAuthenticationToken());

        // Make sure we are Authorized
        if (server.isAuthorized(getAuthorizationToken(request))) {
            logger.info("Request was AUTHORIZED.");
            final BindRequest bindRequest = decode(BindRequest.class, request);
            final BindResponse bindResponse = server.bindService(bindRequest);
            response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            response.getWriter().append(toString(bindResponse));
            response.setStatus(HttpStatus.SC_OK);
        } else {
            logger.info("Request NOT AUTHORIZED.");
            response.getWriter().append(HttpStatus.getStatusText(HttpStatus.SC_UNAUTHORIZED));
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
        }
    }

    /**
     * Unbinding
     * ---------
     *
     * Simply the opposite of bind, unbind is the developer-initiated intention to revoke a specific application’s
     * access to a service instance. The developer makes an API call to CC, which makes an API call to the GW including
     * the GWID of the binding that should be destroyed. If possible, the bound credentials should be destroyed so that
     * application can no longer access the resource.
     *
     * To delete a binding, CC sends an unbind request to the gateway. The full request URL is determined by
     * concatenating the URL in the catalog + the path below.
     *
     * Note: the service_id and handle_id exist in both the URL and BODY, see table:
     *
     * DELETE /gateway/v1/configurations/:service_id/handles/:handle_id
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doDelete (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.info("Un-binding service...");
    }

}
