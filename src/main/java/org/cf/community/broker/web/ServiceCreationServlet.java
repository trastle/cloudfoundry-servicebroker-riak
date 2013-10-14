package org.cf.community.broker.web;

import cf.service.CreateRequest;
import cf.service.CreateResponse;
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
 * This servlet is the entry point for creating and deleting instances of services.
 * Ie 'cf create-service' and 'cf celete-service'
 *
 * It takes care of the following:
 * 1) Make sure requests are authorized.
 * 2) Delegate to the BrokerEngine to do the real work.
 * 3) Setup the responce to go back to the Cloud Controller
 *
 * User: tastle
 * Date: 09/10/2013
 * Time: 15:46
 */
public class ServiceCreationServlet extends AbstractServiceServlet {

    private static final Logger logger = LoggerFactory.getLogger(ServiceCreationServlet.class);


    /**
     * Provisioning
     * ------------
     *
     * When a developer asks to provision a service (using cf create-service), they issue an API request to CC
     * including the offering, plan, and space. CC uses its database to determine what gateway is responsible for this
     * offering and issues an API request synchronously to the GW to provision the resources. This request includes
     * less information than the user-initiated request, because GWs are supposed to not care about some CF concepts
     * like users, spaces, and organizations.
     *
     * The GW must then provision the resource, if possible, and respond with a “gateway identifier” (GWID) that can be
     * used to identify the resource for future operations. In CF terms, what is created after provisioning is a
     * “service instance”. No application should have access to the service instance until it has been bound by the
     * user, so usually no service credentials exist at the time of provisioning.
     *
     * To provision, CC sends a provision request to the gateway. The full request URL is determined by concatenating
     * the URL in the catalog + the path below.
     *
     * POST :service_url/gateway/v1/configurations
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Creating service...");

        BrokerEngine server = new BrokerEngine(new RiakProvisioner(), new BrokerConfig().getServiceAuthenticationToken());

        // Make sure we are Authorized
        if (server.isAuthorized(getAuthorizationToken(request))) {
            logger.info("Request was AUTHORIZED.");
            final CreateRequest createRequest = decode(CreateRequest.class, request);
            final CreateResponse createResponse = server.createService(createRequest);
            response.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            response.getWriter().append(toString(createResponse));
            response.setStatus(HttpStatus.SC_OK);
        } else {
            logger.info("Request NOT AUTHORIZED.");
            response.getWriter().append(HttpStatus.getStatusText(HttpStatus.SC_UNAUTHORIZED));
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
        }
    }


    /**
     * Deprovisioning
     * --------------
     *
     * Simply the opposite of provision, deprovision is the developer-initiated intention to remove a specific service
     * instance. The developer makes an API call to CC, which makes an API call to the GW including the GWID of the
     * instance that should be destroyed. The resources consumed by the service instance should be released, and
     * hopefully made available to future requests.
     *
     * To delete a service instance, CC sends a deprovision request to the gateway. The full request URL is determined
     * by concatenating the URL in the catalog + the path below.
     *
     * DELETE /gateway/v1/configurations/:service_id
     *
     * No body
     * No query parameters
     * The only argument, service_id is the GW service_id that was returned from the provision response, and there is no body to the request or response.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void doDelete (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Deleting service...");

        BrokerEngine server = new BrokerEngine(new RiakProvisioner(), new BrokerConfig().getServiceAuthenticationToken());

        // Make sure we are Authorized
        if (server.isAuthorized(getAuthorizationToken(request))) {
            logger.info("Request was AUTHORIZED.");

            response.setStatus(HttpStatus.SC_OK);
        } else {
            logger.info("Request NOT AUTHORIZED.");
            response.getWriter().append(HttpStatus.getStatusText(HttpStatus.SC_UNAUTHORIZED));
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
        }

    }
}


