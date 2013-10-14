package org.cf.community.broker.riak;

import cf.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This is the only place you'll find RIAK specific code in this project.
 *
 * Implements this interface:
 * https://github.com/cloudfoundry-community/cf-java-component/blob/master/cf-service-broker/src/main/java/cf/service/Provisioner.java
 *
 * User: tastle
 * Date: 12/10/2013
 * Time: 00:33
 */
public class RiakProvisioner implements Provisioner{

    /**
     * When the user types 'cf create-service ...' this method will get called.
     *
     * @param createRequest the create request sent by the Cloud Controller
     * @return
     */
    @Override
    public ServiceInstance create(CreateRequest createRequest) {

        // Should probably think of a better way to do this.
        return new ServiceInstance(UUID.randomUUID().toString());
    }

    /**
     * When the user types 'cf delete-service ...' this method will probably get called.
     *
     * @param instanceID the id of the service instance to be deleted. The value of this is the instance id that is
     *                   returned by {@link #create(CreateRequest)}.
     */
    @Override
    public void delete(String instanceID) {

        // Currently provisioning does nothing to Riak.
        // Need to update this later with
    }

    /**
     * When the user types 'cf bind-service ...' this method will get called.
     */
    @Override
    public ServiceBinding bind(BindRequest bindRequest) {

        ServiceBinding binding = new ServiceBinding(bindRequest.getServiceInstanceId(), "riak-service-binding-id");

        List<String> servers = new ArrayList<String>();
        servers.add("riak1.db.bxb.cpgpaas.net");
        servers.add("riak2.db.bxb.cpgpaas.net");
        servers.add("riak3.db.bxb.cpgpaas.net");
        servers.add("riak4.db.bxb.cpgpaas.net");
        servers.add("riak5.db.bxb.cpgpaas.net");
        binding.addCredential("riak-servers", servers);
        binding.addCredential("riak-port", 8098);

        return binding;
    }

    /**
     * When the user types 'cf unbind-service ...' this method will probably get called.
     *
     * @param instanceID
     * @param bindingID
     */
    @Override
    public void unbind(String instanceID, String bindingID) {

        // Currently binding does nothing
        // Need to update this to undo any future work done when binding.
    }

    /**
     * Returns iterable for each service ids for the services the gateway is aware of.
     *
     * @return service ids for the services the gateway is aware of, or {@code null} if this service gateway does not
     *         track service instances.
     */
    @Override
    public Iterable<String> serviceInstanceIds() {

        // Todo it would be better to track instance Ids.
        return null;
    }

    /**
     * Returns binding ids for the given service instance id.
     *
     * @return binding ids for the given service instance id, or {@code null} if this service broker does not track
     *         bindings.
     */
    @Override
    public Iterable<String> bindingIds(String serviceInstanceID) {

        // Todo it would be better to track bindings
        return null;
    }

    /**
     * This gets called when the cc deletes a service binding but the delete request didn't make it to the broker.
     */
    @Override
    public void removeOrphanedBinding(String s, String s2) {

        // This is currently irrelevant as bindings are not tracked.
    }

    /**
     * This gets called when the cc deletes a service instance but the delete request didn't make it to the broker.
     * @param instanceID
     */
    @Override
    public void removeOrphanedServiceInstance(String instanceID) {

        // This is currently irrelevant as instances are not tracked.

    }


    public RiakBrokerConfig getConfig() {
        return new RiakBrokerConfig();
    }
}

