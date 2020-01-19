package cluster.core.command.data;

import akka.actor.typed.ActorRef;
import cluster.core.command.DataCommand;
import cluster.core.command.MasterCommand;
import cluster.core.model.AsyncData;

public class CompleteProcess implements DataCommand {

	private final AsyncData asyncData;
	private final ActorRef<MasterCommand> masterRef;

	public CompleteProcess(AsyncData asyncData, ActorRef<MasterCommand> masterRef) {
		this.asyncData = asyncData;
		this.masterRef = masterRef;
	}

	public AsyncData getAsyncData() {
		return asyncData;
	}

	public ActorRef<MasterCommand> getMasterRef() {
		return masterRef;
	}
}
