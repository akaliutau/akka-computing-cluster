
About
=======

This is the implementation of distributed execution engine on the basis of Akka cluster (PoC).

As a PoC it does not do much and used to demonstrate the possibility to create a general-purpose distributed computing cluster.
The core functionality covers the following topics:

* creating the cluster of Data Nodes with Workers (de-facto these are just threads in JVM environment used as the main workforce to perform calculations, do some work, etc)
* adding to the cluster the Master Node which implements a primitive orchestrator
* implementation of simple Execution Service to instantiate classes (see cluster.core.engine.ObjectCreator)


Running
========
Run Driver.main() method to instantiate all nodes and to execute a sample process defined in <code>cluster.core.engine.NoneProcessor</code>. 
Obviously one can implement any business logic, the only condition - it must implement Processor interface.

Note the artificial delay before spin up of the cluster and the actual execution of Processor code - it was added due to asynchronous nature of Akka functionality (all routers follow the eventual consistency pattern and need time to propagate all changes between nodes)

References
===========

* [Akka actors](https://doc.akka.io/docs/akka/current/typed/actors.htm)

* [Akka cluster](https://doc.akka.io/docs/akka/2.6/typed/cluster.html) features
