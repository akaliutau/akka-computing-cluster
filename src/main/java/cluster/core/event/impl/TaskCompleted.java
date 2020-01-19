package cluster.core.event.impl;

import cluster.core.event.SessionEvent;

public final class TaskCompleted implements SessionEvent {
	public final String sessionId;
	public final String message;

	public TaskCompleted(String screenName, String message) {
		this.sessionId = screenName;
		this.message = message;
	}
}