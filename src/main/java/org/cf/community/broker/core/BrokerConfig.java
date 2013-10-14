package org.cf.community.broker.core;

/**
 * Generic config required by all service brokers
 * Todo: This should seriously be a properties file.
 *
 * User: tastle
 * Date: 10/10/2013
 * Time: 12:07
 */
public class BrokerConfig {

    // Define things about our environment
    private static final String cloudControllerURI = "http://api.int.ft3.cpgpaas.net";

    // How do we authenticate with the UAA
    // NOTE THESE NEED TO EXIST IN THE UAA, see the README.
    private static final String uaaClientID = "riak-service";
    private static final String uaaClientSecret = "ELVIS_IS_ALIVE";

    // Define things about our service
    private static final String serviceLabel = "Riak-Test-Service";
    private static final String serviceProvider = "Riak-Test-Provider";
    private static final String serviceVersion = "0.1";
    private static final String serviceDescription = "This is a description";
    private static final String serviceURL = "http://riak-service-broker.app.ft3.cpgpaas.net";
    private static final String serviceInfoURL = "http://riak-service-broker.app.ft3.cpgpaas.net/info";
    private static final String serviceUniqueID = "THIS_STRING_IS_UNIQUE_WOO_YEAH_UNIQUE_YEEHAAA";

    // Authentication token
    private static final String serviceAuthenticationToken = "RIAK_SERVICE_AUTH_TOKEN";




    public BrokerConfig(){
        // TODO, load the config externally.
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public String getServiceLabel() {
        return serviceLabel;
    }

    public String getServiceProvider() {
        return serviceProvider;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public String getServiceInfoURL() {
        return serviceInfoURL;
    }

    public String getServiceUniqueID() {
        return serviceUniqueID;
    }

    /**
     * The Client ID used to authenticate with the UAA.
     * You will have added this in your UAA clients in th CF.yml
     * @return the client ID to authenticate with the UAA.
     */
    public String getUaaClientID() {
        return uaaClientID;
    }

    /**
     * The Client Secret used to authenticate with the UAA.
     * You will have added this in your UAA clients in th CF.yml
     * @return the client secret.
     */
    public String getUaaClientSecret() {
        return uaaClientSecret;
    }

    public String getCloudControllerURI() {
        return cloudControllerURI;
    }

    public static String getServiceAuthenticationToken() {
        return serviceAuthenticationToken;
    }
}
