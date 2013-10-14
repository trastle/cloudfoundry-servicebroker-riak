package org.cf.community.broker.web;

import cf.common.JsonObject;
import org.cf.community.broker.core.BrokerEngine;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;

/**
 * Created with IntelliJ IDEA.
 * User: tastle
 * Date: 12/10/2013
 * Time: 12:46
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractServiceServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AbstractServiceServlet.class);
    private ObjectMapper mapper = null;

    /**
     * Get the shared secret passed on the request for authentication.
     * @param request
     * @return
     */
    protected String getAuthorizationToken(HttpServletRequest request) {
        return request.getHeader(BrokerEngine.VCAP_SERVICE_TOKEN_HEADER);
    }

    protected ObjectMapper getObjectMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        return mapper;
    }


    /**
     * Convert the JSON content of the request into a domain data object.
     *
     * @param type the type of object to create.
     * @param request the request whose content must be used for creation
     * @param <T>
     * @return
     */
    protected <T> T decode(Class<T> type, HttpServletRequest request) {

        StringBuffer buff = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null){
                buff.append(line);
            }

            logger.debug("Content:" + buff.toString());

            return getObjectMapper().readValue(buff.toString(), type);
        }
        catch (Exception e) {
            logger.error("Failed to red content from request", e);
        }
        return null;
    }

    protected String toString(JsonObject object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
