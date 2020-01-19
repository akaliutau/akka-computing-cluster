package cluster.core.engine;

import java.util.Arrays;
import java.util.List;

import cluster.core.model.AsyncData;

import static cluster.core.model.DPParams.UNIT_ID;
import static cluster.core.model.DPParams.SESSION_ID;

public class IdentitySplitter implements Splitter {

	@Override
	public List<AsyncData> split(AsyncData data) {
		AsyncData next = data.clone();
		next.addEntry(UNIT_ID, data.getOrDefault(SESSION_ID, "") + ":0");
		System.out.println("identity splitter 1 -> 1 for " + next);
		return Arrays.asList(next);
	}

}
