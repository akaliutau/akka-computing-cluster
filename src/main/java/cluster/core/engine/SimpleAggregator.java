package cluster.core.engine;

import cluster.core.engine.api.Aggregator;
import cluster.core.model.AsyncData;

public class SimpleAggregator implements Aggregator {
	
	private AsyncData data = null;

	@Override
	public void accept(AsyncData data) {
		this.data = data;
		System.out.println("SimpleAggregator accepted " + data);
	}

	@Override
	public boolean allDone() {
		return true;
	}

	@Override
	public AsyncData result() {
		return data;
	}

}
