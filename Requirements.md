# Requirements

## SERVICE BROKER

**NB: In priority order**

	As a developer 
	I want to be allocated a new bucket when calling 'cf create-service'
	So that I can work without fear of collision

	As a developer  
	I want the ability to, optionally, specify a named bucket when calling 'cf create-service' 
	So that I have a bucket with a specific name 

	As a developer 
	I want buckets to be destroyed 'rm -rf' style when I delete a service  
	So that I can clean up after myself and ensure no one else gets at my data 

	As a developer 
	I want my buckets to be authenticated and inaccessible to users not bound to my service 
	So that I can ensure the isolation of the data in my bucket 

	As a developer 
	I want the ability to provision access to a shared RIAK cluster through create-service 
	Because i don't need a full siloed cluster for this application. 

	As a developer 
	I want the ability  to provision single-tenancy RIAK cluster (via BOSH) created through create-service 
	So that I can guarantee performance and isolate my data 

## BOSH (Automated Riak[CS] Cluster Deployment
**NB: In priority order**

    As a BOSH admin 
	I want to be able to specify the backend on my Riak cluster when deploying via bosh
	So that I can cater for specific use cases within my applications

	As a BOSH admin
	I want to be able to specify the number of nodes in my Riak cluster 
	So that I can guarantee my specific consistency model is catered for

	As a BOSH admin
	I want to be able to specify the quorum on my Riak cluster when deploying via bosh
	So that I can tune the consistency model for my specific use case

	As a Cloud Foundry admin
	I want to provide I/O and performance gaurantees within a shared RIAK/RIAK-CS cluster
	So that we can provide a guarinteed SLA to users

	As a perfomance tester
	I want 'cf create-service' to auto provision a new Riak cluster 
	So that I can guarantee resource isolation for test / high IO load application / data seperation.

	As a perfomance tester
	When provisioning a new Riak Cluster I want to specify: number of nodes, quora and backend types
	So that I can identify the most performant configuration

	As a perfomance tester
	I want to specify a TTL for my Riak Cluster
	So that temporary clusters can be provisioned to make efficient use of finite hardware

