package org.cf.community.broker.riak;

/**
 * Config specific to the Riak service.
 * Todo: This should seriously be a properties file. SERIOUSLY.
 *
 * User: tastle
 * Date: 10/10/2013
 * Time: 12:07
 */
public class RiakBrokerConfig {

    // Define things about a service plan
    private static final String planName = "Alpha";
    private static final String planDescription = "Alpha Riak Service";
    private static final String planUniqueID = "riak-alpha-service-id";


    private static final int riakPort = 8098;


    public RiakBrokerConfig(){
        // TODO, load the config externally.
    }

    public String getPlanName() {
        return planName;
    }

    public String getPlanDescription() {
        return planDescription;
    }

    public String getPlanUniqueID() {
        return planUniqueID;
    }
}
