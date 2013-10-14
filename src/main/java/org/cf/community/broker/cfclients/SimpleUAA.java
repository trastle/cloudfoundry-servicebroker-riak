package org.cf.community.broker.cfclients;

import cf.client.Token;
import cf.client.TokenContents;
import cf.client.Uaa;
import cf.client.UnexpectedResponseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * A simple implementation of a UAA client.
 *
 * Originally copied from the link below for east of debugging and modification.
 * https://github.com/cloudfoundry-community/cf-java-component/blob/master/cf-client/src/main/java/cf/client/DefaultUaa.java
 *
 * User: tastle
 * Date: 11/10/2013
 * Time: 14:33
 */
public class SimpleUAA implements  Uaa{

        private static final String CHECK_TOKEN = "/check_token";
        private static final String OAUTH_TOKEN_URI = "/oauth/token";

        private static final Header ACCEPT_JSON = new BasicHeader("Accept","application/json;charset=utf-8");

        private final HttpClient httpClient;
        private final URI uaa;

        private final ObjectMapper mapper;

        public SimpleUAA(HttpClient httpClient, String uaaUri) {
            this(httpClient, URI.create(uaaUri));
        }

        public SimpleUAA(HttpClient httpClient, URI uaa) {
            this.httpClient = httpClient;
            this.uaa = uaa;

            mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }

        @Override
        public Token getClientToken(String client, String clientSecret) {
            try {
                final HttpPost post = new HttpPost(uaa.resolve(OAUTH_TOKEN_URI));

                post.setHeader(ACCEPT_JSON);
                post.setHeader(createClientCredentialsHeader(client, clientSecret));

                // TODO Do we need to make the grant type configurable?
                final NameValuePair nameValuePair = new BasicNameValuePair("grant_type", "client_credentials");
                List<NameValuePair> list = Arrays.asList(nameValuePair);

                post.setEntity(new UrlEncodedFormEntity(list));


                try {
                    final HttpResponse response = httpClient.execute(post);

                    validateResponse(response);
                    final HttpEntity entity = response.getEntity();
                    final InputStream content = entity.getContent();
                    return Token.parseJson(content);
                } catch (HttpException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        public TokenContents checkToken(String client, String clientSecret, Token token) {
            try {
                final URI checkTokenUri = uaa.resolve(CHECK_TOKEN);
                final HttpPost post = new HttpPost(checkTokenUri);
                post.setHeader(createClientCredentialsHeader(client,clientSecret));
                final NameValuePair tokenType = new BasicNameValuePair("token_type", token.getType().getValue());
                final NameValuePair tokenValue = new BasicNameValuePair("token", token.getAccessToken());
                post.setEntity(new UrlEncodedFormEntity(Arrays.asList(tokenType, tokenValue)));


                try {
                    final HttpResponse response = httpClient.execute(post);
                    validateResponse(response);

                    final HttpEntity entity = response.getEntity();
                    final InputStream content = entity.getContent();

                    return mapper.readValue(content, TokenContents.class);
                } catch (HttpException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } finally {
                    //HttpClientUtils.closeQuietly(response);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        private Header createClientCredentialsHeader(String client, String clientSecret) {

            final String encoding = Base64.encodeBase64String((client + ":" + clientSecret).getBytes());
            return new BasicHeader("Authorization", "Basic " + encoding);
        }

        private void validateResponse(HttpResponse response) {
            final StatusLine statusLine = response.getStatusLine();
            final int statusCode = statusLine.getStatusCode();
            if (statusCode != 200) {
                throw new UnexpectedResponseException(response);
            }
        }

    }

