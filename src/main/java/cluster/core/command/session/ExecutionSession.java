package cluster.core.command.session;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import cluster.core.command.DataCommand;
import cluster.core.command.MasterCommand;
import cluster.core.command.SessionCommand;
import cluster.core.command.data.ComputeProcess;
import cluster.core.event.SessionEvent;

/**
 * Session object, used for orchestrating purposes and to provide 2-way communication between Master and Data nodes
 * 
 * TODO: use new service for each Session, which will be used to distribute tasks between workers, collect results and report to master
 * @author akaliutau
 *
 */
public class ExecutionSession {
	public static Behavior<SessionCommand> create(ActorRef<MasterCommand> masterNode, ActorRef<DataCommand> dataNode, String sessionId,
			ActorRef<SessionEvent> client) {
		return Behaviors.receive(SessionCommand.class)
				.onMessage(StartCommand.class, cmd -> onExecute(masterNode, dataNode, cmd))
				.onMessage(NotifyClient.class, notification -> onNotifyClient(client, notification))
				.build();
	}

	private static Behavior<SessionCommand> onExecute(ActorRef<MasterCommand> masterNode, ActorRef<DataCommand> dataNode, StartCommand startCommand) {
		dataNode.tell(new ComputeProcess(startCommand.getAsyncData(), dataNode, masterNode));
		return Behaviors.same();
	}

	private static Behavior<SessionCommand> onNotifyClient(ActorRef<SessionEvent> client, NotifyClient notification) {
		client.tell(notification.message);
		return Behaviors.same();
	}
}
