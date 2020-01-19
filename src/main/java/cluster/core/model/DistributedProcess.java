package cluster.core.model;

public class DistributedProcess {
	private final AsyncData asyncData;

	public DistributedProcess(AsyncData asyncData) {
		this.asyncData = asyncData;
	}

	public AsyncData getAsyncData() {
		return asyncData;
	}

}
