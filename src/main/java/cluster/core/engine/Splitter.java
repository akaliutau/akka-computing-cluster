package cluster.core.engine;

import java.util.List;

import cluster.core.model.AsyncData;

public interface Splitter {
	List<AsyncData> split(AsyncData data);
}
