package cluster.core.engine;

import cluster.core.engine.api.Processor;
import cluster.core.model.AsyncData;

public class NoneProcessor implements Processor {

	@Override
	public AsyncData process(AsyncData data) {
		AsyncData next = data.clone();
		next.addEntry("calculated_value", "123");
		System.out.println("NoneProcessor added key to the async data object");
		return next;
	}

}
