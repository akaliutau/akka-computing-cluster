package cluster.core.command.session;

import cluster.core.command.SessionCommand;
import cluster.core.model.AsyncData;

public final class StartCommand implements SessionCommand {
	private final AsyncData asyncData;

	public StartCommand(AsyncData asyncData) {
		this.asyncData = asyncData;
	}

	public AsyncData getAsyncData() {
		return asyncData;
	}
}
