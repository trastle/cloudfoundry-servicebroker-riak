package com.cisco.ft.sample.test.service;

import cf.client.CloudController;
import cf.client.TokenProvider;
import org.cf.community.broker.web.InstallBrokerServlet;
import org.cf.community.broker.riak.RiakBrokerConfig;
import org.cf.community.broker.cfclients.SimpleCloudController;
import org.cf.community.broker.cfclients.SimpleTokenProvider;
import junit.framework.TestCase;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;

/**
 * Make sure we can parse various services JSON.
 *
 * User: tastle
 * Date: 07/10/2013
 */
public class TestServiceParse extends TestCase {


    private final String JSON = "{\"name\":\"vcap\",\"build\":\"2222\",\"support\":\"http://support.cloudfoundry.com\",\"version\":2,\"description\":\"Cloud Foundry sponsored by Pivotal\",\"authorization_endpoint\":\"http://uaa.int.ft3.cpgpaas.net\",\"token_endpoint\":\"http://uaa.int.ft3.cpgpaas.net\",\"allow_debug\":true}";


    public void testName() throws Exception {


//            ObjectMapper mapper = new ObjectMapper();
//            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//
//        Info info = mapper.readValue(JSON, Info.class);
//
//        System.out.print(info.getVersion());


        new InstallBrokerServlet().bootStrap(new RiakBrokerConfig());

    }

    public void testName2() throws Exception {

        RiakBrokerConfig config = new RiakBrokerConfig();

        URI ccURI = new URI(config.getCloudControllerURI());
        HttpClient httpClient = new DefaultHttpClient();

        CloudController cc = new SimpleCloudController(httpClient, ccURI);





        cc.getInfo();
        TokenProvider tp = new SimpleTokenProvider(cc, "login", "ELVIS_IS_ALIVE");

        tp.get();
    }



}
