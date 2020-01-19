package cluster.core;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import cluster.core.command.session.StartCommand;
import cluster.core.event.SessionEvent;
import cluster.core.event.impl.SessionDenied;
import cluster.core.event.impl.SessionGranted;
import cluster.core.event.impl.TaskCompleted;

/**
 * Abstraction for Task events
 * Used to generate and process high-level coordination events for Data Nodes in the cluster
 * @author akaliutau
 *
 */
public class SessionEventProcessor {
	public static Behavior<SessionEvent> create() {
		return Behaviors.setup(ctx -> new SessionEventProcessor(ctx).behavior());
	}

	private final ActorContext<SessionEvent> context;

	private SessionEventProcessor(ActorContext<SessionEvent> context) {
		this.context = context;
	}

	private Behavior<SessionEvent> behavior() {
		return Behaviors.receive(SessionEvent.class)
				.onMessage(SessionDenied.class, this::onSessionDenied)
				.onMessage(SessionGranted.class, this::onSessionGranted)
				.onMessage(TaskCompleted.class, this::onTaskCompleted).build();
	}

	private Behavior<SessionEvent> onSessionDenied(SessionDenied message) {
		context.getLog().info("cannot start session: {}", message.reason);
		return Behaviors.stopped();
	}

	private Behavior<SessionEvent> onSessionGranted(SessionGranted sessionGrantedMessage) {
		context.getLog().info("SessionEventProcessor: session granted");
		sessionGrantedMessage.getSessionRef().tell(new StartCommand(sessionGrantedMessage.getAsyncData()));
		return Behaviors.same();
	}

	private Behavior<SessionEvent> onTaskCompleted(TaskCompleted message) {
		context.getLog().info("work has been completed '{}': {}", message.sessionId, message.message);
		return Behaviors.stopped();
	}
}
