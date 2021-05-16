
About
=======

This is the implementation of distributed execution engine on the basis of Akka cluster (PoC).

As a PoC id does not do much and covers the following topics:

* creating the cluster of Data Nodes with Workers (de-facto these are just threads in JVM environment used as the main workforce to perform calculations, do some work, etc)
* adding to the cluster the Master Node which implements a primitive orchestrator
* implementation of simple Execution Service to instantiate classes (see cluster.core.engine.ObjectCreator)


Running
========
Run Driver.main() method to instantiate all nodes and to execute a sample process defined in <code>cluster.core.engine.NoneProcessor</code>. 
Obviously one can implement any business logic, the only condition - it must implement Processor interface.


References
===========

* [Akka actors](https://doc.akka.io/docs/akka/current/typed/actors.htm)

* [Akka cluster](https://doc.akka.io/docs/akka/2.6/typed/cluster.html) features.
