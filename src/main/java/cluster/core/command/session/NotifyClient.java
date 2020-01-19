package cluster.core.command.session;

import cluster.core.command.SessionCommand;
import cluster.core.event.impl.TaskCompleted;

public final class NotifyClient implements SessionCommand {
	public final TaskCompleted message;

	public NotifyClient(TaskCompleted message) {
		this.message = message;
	}
}