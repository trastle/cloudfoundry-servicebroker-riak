package org.cf.community.broker.cfclients;

import cf.client.CloudController;
import cf.client.Token;
import cf.client.TokenProvider;
import cf.client.Uaa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Wrote this to avoid pulling in the entire Spring Library.
 *
 * Forked from this Spring Token Factory:
 * https://github.com/cloudfoundry-community/cf-java-component/blob/master/cf-spring/src/main/java/cf/spring/ClientTokenProviderFactoryBean.java
 *
 * User: tastle
 * Date: 10/10/2013
 * Time: 12:28
 */
public class SimpleTokenProvider implements TokenProvider {

    private final CloudController cloudController;
    private final String client;
    private final String clientSecret;
    private Token token;

    private static final Logger logger = LoggerFactory.getLogger(SimpleTokenProvider.class);

    public SimpleTokenProvider(CloudController cloudController, String client, String clientSecret) {
        this.cloudController = cloudController;
        this.client = client;
        this.clientSecret = clientSecret;
    }

    private Token fetchToken() {
        synchronized (this) {
            if (token == null || token.hasExpired()) {
                Uaa uaa = cloudController.getUaa();

                token = uaa.getClientToken(client, clientSecret);
            }
            return token;
        }
    }

    @Override
    public Token get() {
        return fetchToken();
    }
}