package cluster.core.command.master;

import akka.actor.typed.ActorRef;
import cluster.core.command.MasterCommand;
import cluster.core.event.SessionEvent;
import cluster.core.model.DistributedProcess;

public final class InitExecutionSession implements MasterCommand {
	private final DistributedProcess process;
	private final ActorRef<SessionEvent> replyTo;

	public InitExecutionSession(DistributedProcess process, ActorRef<SessionEvent> replyTo) {
		this.process = process;
		this.replyTo = replyTo;
	}

	public DistributedProcess getProcess() {
		return process;
	}

	public ActorRef<SessionEvent> getReplyTo() {
		return replyTo;
	}
}
