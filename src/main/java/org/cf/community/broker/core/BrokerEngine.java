package org.cf.community.broker.core;


import cf.service.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * The Broker Engine handles the transform of Requests into Responses by delegating work to the provided provisioner.
 *
 * Forked from here:
 * https://github.com/cloudfoundry-community/cf-java-component/blob/master/cf-service-broker/src/main/java/cf/service/AbstractBrokerServer.java
 *
 * User: tastle
 * Date: 12/10/2013
 * Time: 00:15
 */
public class BrokerEngine {

    private static final Logger logger = LoggerFactory.getLogger(BrokerEngine.class);

    /* The header used by Cloud Foundry to pass the shared Service auth token */
    public static final String VCAP_SERVICE_TOKEN_HEADER = "X-VCAP-Service-Token";

    /* The shared, static, authentication token between the broker and the Cloud Controller */
    private final String serviceSharedAuthToken;

    /* Constant key expected in the response by the Cloud Controller. */
    public static final String SERVICE_INSTANCE_ID = "service_id";

    /* Constant key expected in the response by the Cloud Controller. */
    public static final String SERVICE_BINDING_ID = "binding_id";

    final private ObjectMapper mapper = new ObjectMapper();

    /* Which ever provisioner you have decided to use for the project */
    private final Provisioner provisioner;

    public BrokerEngine(Provisioner provisioner, String authToken) {
        if (authToken == null) {
            throw new IllegalArgumentException("authToken can not be null");
        }

        this.provisioner = provisioner;
        this.serviceSharedAuthToken = authToken;

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public CreateResponse createService(CreateRequest createRequest) {
        logger.info("Creating instance for service {} in space {} in org {}",
                createRequest.getLabel(),
                createRequest.getSpaceGuid(),
                createRequest.getOrganizationGuid());

        final ServiceInstance serviceInstance = provisioner.create(createRequest);
        final ObjectNode gatewayData = mapper.createObjectNode();
        putAll(gatewayData, serviceInstance.getGatewayData());
        gatewayData.put(SERVICE_INSTANCE_ID, serviceInstance.getInstanceId());

        final ObjectNode credentials = mapper.createObjectNode();
        putAll(credentials, serviceInstance.getCredentials());

        return new CreateResponse(serviceInstance.getInstanceId(), gatewayData, credentials);
    }

    public void deleteService(String serviceInstanceId) {
        logger.info("Deleting service instance {}", serviceInstanceId);
        provisioner.delete(serviceInstanceId);
    }

    public BindResponse bindService(BindRequest bindRequest) {
        logger.info("Binding service {} for instance {}", bindRequest.getLabel(), bindRequest.getServiceInstanceId());
        final ServiceBinding serviceBinding = provisioner.bind(bindRequest);
        final Map<String,Object> gatewayData = new HashMap<>(serviceBinding.getGatewayData());
        gatewayData.put(SERVICE_INSTANCE_ID, serviceBinding.getInstanceId());
        gatewayData.put(SERVICE_BINDING_ID, serviceBinding.getBindingId());
        return new BindResponse(serviceBinding.getBindingId(), gatewayData, serviceBinding.getCredentials());

    }

    /**
     * Unbind a particular service binding from an Application.
     *
     * @param serviceInstanceUUID
     * @param bindingUUID
     */
    public void unbindService(String serviceInstanceUUID, String bindingUUID) {
        logger.info("Unbinding instance {} for binding {}", serviceInstanceUUID, bindingUUID);
        provisioner.unbind(serviceInstanceUUID, bindingUUID);
    }


    /**
     * When the Cloud Controller makes requests to the Service Broker it adds an authentication header to the request:
     * X-VCAP-Service-Token = Pre-Shared-Static-Authentication-Token-Here
     *
     * It is the responsibility of the client broker to ensure that this header is present and validate that the Cloud
     * Controller has sent the correct header to the broker.
     *
     * That check is done in this method.
     *
     * @param vCapServiceTokenHeader the request coming in from the Cloud Controller.
     * @return <code>true</code> if the shared token matches the expected token, otherwise <code>false.</code>
     */
    public boolean isAuthorized(String vCapServiceTokenHeader){
        logger.debug("Got Authorization token on request [" + vCapServiceTokenHeader + "].");
        return this.serviceSharedAuthToken.equals(vCapServiceTokenHeader);

    }

    private void putAll(ObjectNode object, Map<String,Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            final JsonNode value;
            if (entry.getValue() instanceof JsonNode) {
                value = (JsonNode) entry.getValue();
            } else {
                value = mapper.valueToTree(entry.getValue());
            }
            object.put(entry.getKey(), value);
        }
    }
}
