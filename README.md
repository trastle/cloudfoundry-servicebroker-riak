# Riak-Service-Broker


**WORK IN PROGRESS!!! CURRENTLY VERY, VERY BASIC!!!**

## Building the project

To grab a copy of this project and and build it you'll need to do the following:

    git clone https://github.com/trastle/cloudfoundry-servicebroker-riak.git
    cd riak-service-broker
    mvn clean package

## Configuring the service broker

Todo

## Adding a Client to the UAA:
In order for the service to authenticate with the UAA you will need to add the following client to the UAA properties:

      riak-service:
        id: riak-service
        override: true
        autoapprove: true
        scope: cloud_controller.admin
        authorities: cloud_controller.admin
        authorized-grant-types: authorization_code,client_credentials,refresh_token
        secret: ELVIS_IS_ALIVE
        access-token-validity: 7200
        refresh-token-validity: 1209600

Now go to the PaaS and have a look-see.

# Appendix: References

* **Dependencies**
  * [Cloud Foundry Community - cf-java-component](https://github.com/cloudfoundry-community/cf-java-component)    
* **Official Documentation**
  * [CF Service Broker V1 API](https://github.com/geapi/cf-service-example)
  * [How to integrate an application with Cloud Foundry using OAUTH2](http://blog.cloudfoundry.com/2012/11/05/how-to-integrate-an-application-with-cloud-foundry-using-oauth2/)
* **Blog Posts Etc**
  * [Building a Cloud Foundry Service Broker - Part 1](http://pivotallabs.com/creating-a-service-gateway-in-cloud-foundry/)
  * [Building a Cloud Foundry Service Broker - Part 2](http://pivotallabs.com/creating-a-service-gateway-in-cloud-foundry-part-2/)
 
