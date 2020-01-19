package cluster.core.engine;

import cluster.core.model.AsyncData;

public interface Aggregator {
	void accept(AsyncData data);
	boolean allDone();
	AsyncData result();
}
