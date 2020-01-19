package cluster.core.command.worker;

import cluster.core.command.WorkerCommand;
import cluster.core.model.AsyncData;

public class UnitResult implements WorkerCommand {
	private final AsyncData asyncData;

	public UnitResult(AsyncData asyncData) {
		this.asyncData = asyncData;
	}

	public AsyncData getAsyncData() {
		return asyncData;
	}
}
