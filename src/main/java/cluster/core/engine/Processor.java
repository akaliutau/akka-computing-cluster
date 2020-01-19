package cluster.core.engine;

import cluster.core.model.AsyncData;

public interface Processor {
	AsyncData process(AsyncData data);
}
