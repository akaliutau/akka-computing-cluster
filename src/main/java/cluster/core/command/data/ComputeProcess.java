package cluster.core.command.data;

import akka.actor.typed.ActorRef;
import cluster.core.command.DataCommand;
import cluster.core.command.MasterCommand;
import cluster.core.model.AsyncData;

public class ComputeProcess implements DataCommand {
	private final AsyncData asyncData;
	private final ActorRef<DataCommand> dataNodeRef;
	private final ActorRef<MasterCommand> masterNodeRef;

	public ComputeProcess(AsyncData asyncData, ActorRef<DataCommand> dataNodeRef, ActorRef<MasterCommand> masterNodeRef) {
		this.dataNodeRef = dataNodeRef;
		this.masterNodeRef = masterNodeRef;
		this.asyncData = asyncData;
	}

	public AsyncData getAsyncData() {
		return asyncData;
	}

	public ActorRef<DataCommand> getDataNodeRef() {
		return dataNodeRef;
	}

	public ActorRef<MasterCommand> getMasterNodeRef() {
		return masterNodeRef;
	}


}
