package com.cisco.ft.sample.test.service;

import org.cf.community.broker.web.InstallBrokerServlet;
import org.cf.community.broker.riak.RiakBrokerConfig;
import junit.framework.TestCase;

/**
 * Make sure we can parse various services JSON.
 *
 * User: tastle
 * Date: 07/10/2013
 */
public class TestBasicAuth extends TestCase {

           // YWRtaW46Y3BncGFhc3Bhc3M=


           // c2VydmljZXM6YmFuZVlFU3RoYXRzTVluYW1l

    public void testName2() throws Exception {



        RiakBrokerConfig config = new RiakBrokerConfig();
        new InstallBrokerServlet().bootStrap(config);



    }



}
