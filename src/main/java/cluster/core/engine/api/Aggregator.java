package cluster.core.engine.api;

import cluster.core.model.AsyncData;

public interface Aggregator {
	void accept(AsyncData data);
	boolean allDone();
	AsyncData result();
}
