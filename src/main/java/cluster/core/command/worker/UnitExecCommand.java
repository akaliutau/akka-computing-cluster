package cluster.core.command.worker;

import akka.actor.typed.ActorRef;
import cluster.core.command.WorkerCommand;
import cluster.core.model.AsyncData;

public final class UnitExecCommand implements WorkerCommand {
	private final AsyncData work;
	private final ActorRef<UnitResult> callback;

	public UnitExecCommand(AsyncData work, ActorRef<UnitResult> callback) {
		this.work = work;
		this.callback = callback;
	}

	public AsyncData getWork() {
		return work;
	}

	public ActorRef<UnitResult> getCallback() {
		return callback;
	}
}

