package cluster.core.engine.api;

import cluster.core.model.AsyncData;

public interface Processor {
	AsyncData process(AsyncData data);
}
