package cluster.core.event.impl;

import cluster.core.event.SessionEvent;

public final class SessionDenied implements SessionEvent {
	public final String reason;

	public SessionDenied(String reason) {
		this.reason = reason;
	}
}
