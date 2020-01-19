package cluster.core.engine;

import java.util.HashMap;
import java.util.Map;

public class ObjectCreator {
	private static final Map<Class<? extends Splitter>, Splitter> splitters = new HashMap<>();
	static {
		splitters.put(IdentitySplitter.class, new IdentitySplitter());
	}

	private static final Map<Class<? extends Aggregator>, Aggregator> aggregators = new HashMap<>();
	static {
		aggregators.put(SimpleAggregator.class, new SimpleAggregator());
	}

	private static final Map<Class<? extends Processor>, Processor> processors = new HashMap<>();
	static {
		processors.put(NoneProcessor.class, new NoneProcessor());
	}

	@SuppressWarnings("unchecked")
	public static <T> T create(String className) {
		try {
			Class<?> clazz = Class.forName(className);
			if (splitters.containsKey(clazz)) {// f.e. cluster.core.engine
				return (T) splitters.get(clazz);
			}
			if (aggregators.containsKey(clazz)) {
				return (T) aggregators.get(clazz);
			}
			if (processors.containsKey(clazz)) {
				return (T) processors.get(clazz);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("cannot figure out classname " + className);
		}
		throw new IllegalArgumentException("cannot instantiate object of type " + className);
	}
}
