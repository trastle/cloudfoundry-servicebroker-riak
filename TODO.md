## Riak-Service-Broker Todo List

This project is not complete, this is a place to keep track of pending work.

### Needs

* Cleanup
  * Kill the servlet code and replace it with play.
  * Write some tests.
  * Un-fork the cloud-foundry-community java stuff. 
* create-service
  * Support more than one hard-coded bucket name.
  * Allow a user to specify their bucket name.
  * Create a new bucket and provide that bucket to the user.
* delete-service
  * rm -rf the bucket	

### Wants 

* Understand how 'plans' would work.
  * Is there a quota system for buckets? 
  * Need to read the docs on this.
  * Need to read the Riak docs on Quotas & Users 


