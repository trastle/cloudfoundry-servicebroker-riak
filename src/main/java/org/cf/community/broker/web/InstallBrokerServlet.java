package org.cf.community.broker.web;

import cf.client.CloudController;
import cf.client.Token;
import cf.client.TokenProvider;
import cf.service.Bootstrap;
import org.cf.community.broker.cfclients.SimpleCloudController;
import org.cf.community.broker.cfclients.SimpleTokenProvider;
import org.cf.community.broker.core.BrokerConfig;
import org.cf.community.broker.riak.RiakBrokerConfig;
import org.apache.http.impl.client.DefaultHttpClient;
import org.cf.community.broker.riak.RiakProvisioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * This Servlet handles the registration of the Service Broker with the Cloud Foundry Cloud Controller.
 * It follows the following basic process:
 *
 * 1) Get an OAUTH2 token for communicating with the Cloud Controller.
 * 2) Add this service to the Cloud Controller.
 * 3) Add a service plan to the Service on the Cloud Controller.
 * 4) Create the shared authentication token needed to talk to the Cloud Controller.
 *
 * User: tastle
 * Date: 09/10/2013
 * Time: 15:46
 */
public class InstallBrokerServlet extends HttpServlet {


    private static final Logger logger = LoggerFactory.getLogger(InstallBrokerServlet.class);

    public void doGet (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        logger.debug("Entering GET /control/init");

        response.getWriter().append("<h3>About to install service...</h3>").flush();
        bootStrap(new BrokerConfig());
        response.getWriter().append("<h3>Service installation complete.</h3>").flush();
    }

    public void bootStrap(BrokerConfig config) {

        try {
            logger.debug("Creating CC URI: "+ config.getCloudControllerURI());
            URI ccURI = new URI(config.getCloudControllerURI());
            DefaultHttpClient httpClient = new DefaultHttpClient();

            CloudController cc = new SimpleCloudController(httpClient, ccURI);
            TokenProvider tp = new SimpleTokenProvider(cc, config.getUaaClientID(), config.getUaaClientSecret());

            /* This will create a UAA OAUTH token to for your service to talk to the Cloud Controller     */
            /* This will fail if you have not added the right client to the UAA, you have given the wrong */
            /* URL to your Cloud Controller OR the broker app cannot access the Cloud Controller on the   */
            /* URL you provided.                                                                          */
            logger.debug("Creating UAA access token for service...");
            Token uaaAccessTokenForService = tp.get();

            if (isValidAccessToken(uaaAccessTokenForService)){

                logger.debug("Bootstrapping...");
                Bootstrap riakBootstrap = new Bootstrap(cc, tp);

                /* First Create the Service */
                logger.debug("Creating service...");
                UUID serviceGUID =  riakBootstrap.registerService(config.getServiceLabel(),
                        config.getServiceProvider(),
                        config.getServiceVersion(),
                        new URI(config.getServiceURL()),
                        config.getServiceDescription(),
                        new URI(config.getServiceInfoURL()),
                        config.getServiceUniqueID());
                logger.debug("Created service with GUID[" + serviceGUID + "]");

                /* Second add at least one service plan */
                logger.debug("Creating plan...");
                RiakBrokerConfig riakConfig = new RiakProvisioner().getConfig();

                UUID planGUID = riakBootstrap.registerPlan(serviceGUID,
                        riakConfig.getPlanName(),
                        riakConfig.getPlanDescription(),
                        riakConfig.getPlanUniqueID());
                logger.debug("Created plan with GUID[" + planGUID + "]");

                /* Third register an authentication token */
                logger.debug("Creating authentication token...");
                UUID authTokenGUID = riakBootstrap.registerAuthToken(config.getServiceLabel(),
                                                config.getServiceProvider(),
                                                config.getServiceAuthenticationToken());
                logger.debug("Created authentication token with GUID[" + authTokenGUID + "]");
            }

        } catch (URISyntaxException e) {
            logger.error("Failed to bootstrap", e);
        }
    }

    /**
     * Make sure the Access token we got from the UAA has the scope we need to register
     * Cloud Foundry services.
     *
     * <a href="http://docs.cloudfoundry.com/docs/running/architecture/services/writing-service.html#authentication">
     * The token requirements are documented here
     * </a>.
     *
     * @param token the access token from the UAA.
     * @return <code>true</code> if valid, otherwise <code>false</code>.
     */
    private boolean isValidAccessToken(Token token) {

        boolean valid = false;

        if (token != null) {
            if (token.getScopes().contains("cloud_controller.admin")) {
                valid = true;
            } else {
                logger.error("Access token is missing scope \"cloud_controller.admin\"");
            }
        }

        return valid;
    }
}


