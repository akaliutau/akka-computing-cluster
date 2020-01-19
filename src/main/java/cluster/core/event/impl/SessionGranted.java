package cluster.core.event.impl;

import akka.actor.typed.ActorRef;
import cluster.core.command.session.StartCommand;
import cluster.core.event.SessionEvent;
import cluster.core.model.AsyncData;

public final class SessionGranted implements SessionEvent {
	private final AsyncData asyncData;
	private final ActorRef<StartCommand> sessionRef;

	public SessionGranted(AsyncData asyncData, ActorRef<StartCommand> sessionRef) {
		this.asyncData = asyncData;
		this.sessionRef = sessionRef;
	}

	public AsyncData getAsyncData() {
		return asyncData;
	}

	public ActorRef<StartCommand> getSessionRef() {
		return sessionRef;
	}
}
