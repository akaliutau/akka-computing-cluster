package cluster.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import cluster.core.command.DataCommand;
import cluster.core.command.MasterCommand;
import cluster.core.command.SessionCommand;
import cluster.core.command.master.InitExecutionSession;
import cluster.core.command.master.CompleteTask;
import cluster.core.command.session.ExecutionSession;
import cluster.core.command.session.NotifyClient;
import cluster.core.event.SessionEvent;
import cluster.core.event.impl.SessionGranted;
import cluster.core.event.impl.TaskCompleted;
import cluster.core.model.AsyncData;

import static cluster.core.model.DPParams.*;

/**
 * Used to manage execution sessions, notify all participants about completion, etc
 * 
 * In other words, does all orchestration work
 * 
 * @author akaliutau
 *
 */
public class MasterNode {

	public static Behavior<MasterCommand> create(ActorRef<DataCommand> serviceRouter) {
		return Behaviors.setup(ctx -> new MasterNode(ctx, serviceRouter).create(new ArrayList<ActorRef<SessionCommand>>()));// start with empty list
	}

	private final ActorContext<MasterCommand> context;
	private final ActorRef<DataCommand> dataNodesRouter;

	private MasterNode(ActorContext<MasterCommand> context, ActorRef<DataCommand> dataNodesRouter) {
		this.context = context;
		this.dataNodesRouter = dataNodesRouter;
	}

	private Behavior<MasterCommand> create(List<ActorRef<SessionCommand>> sessions) {
		return Behaviors.receive(MasterCommand.class)
				.onMessage(InitExecutionSession.class, getSession -> onGetSession(sessions, getSession))
				.onMessage(CompleteTask.class, pub -> onCompleteTask(sessions, pub))
				.build();
	}

	private Behavior<MasterCommand> onGetSession(List<ActorRef<SessionCommand>> sessions, InitExecutionSession sessionMasterCommand)
			throws UnsupportedEncodingException {
		ActorRef<SessionEvent> client = sessionMasterCommand.getReplyTo();
		String sessionId = UUID.randomUUID().toString();
		
		context.getLog().info("starting new session, id={}, serviceRouter= {}", sessionId, dataNodesRouter.narrow().path());
		
		ActorRef<SessionCommand> sessionRef = context.spawn(ExecutionSession.create(context.getSelf(), dataNodesRouter, sessionId, client),
				URLEncoder.encode(sessionId, StandardCharsets.UTF_8.name()));

		context.getLog().info("SessionGranted");
		AsyncData asyncData = sessionMasterCommand.getProcess().getAsyncData().clone();
		asyncData.addEntry(SESSION_ID, sessionId);
		client.tell(new SessionGranted(asyncData, sessionRef.narrow()));
		
		List<ActorRef<SessionCommand>> newSessions = new ArrayList<>(sessions);
		newSessions.add(sessionRef);
		return create(newSessions);
	}

	private Behavior<MasterCommand> onCompleteTask(List<ActorRef<SessionCommand>> sessions,
			CompleteTask task) {
		context.getLog().info("task {} complete", task.getSessionId());
		//NotifyClient notification = new NotifyClient((new TaskCompleted(pub.getSessionId(), pub.getMessage())));
		//sessions.forEach(s -> s.tell(notification));// notify only those participants who is needed to know about completion
		return Behaviors.same();
	}
}
