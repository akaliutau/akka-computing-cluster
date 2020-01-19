package cluster.core.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Transfer object used to pass data from actor to actor
 * 
 * Must contain the full description of the process
 * 
 * @author Alex
 *
 */
public class AsyncData implements Serializable {
	private static final long serialVersionUID = -1551534343979408912L;

	private Map<String,String> data = new HashMap<>();
	
	public AsyncData() {
	}
	
	public AsyncData(Map<String, String> data) {
		this.data = data;
	}
	
	public Set<String> keys(){
		return data.keySet();
	}
	
	public AsyncData addEntry(Object key, String value) {
		if (key == null) {
			throw new IllegalArgumentException("attempt to add null key");
		}
		this.data.put(key.toString(), value);
		return this;
	}
	
	public Set<Entry<String, String>> entries() {
		return data.entrySet();
	}
	
	public String get(Object key) {
		if (key == null) {
			return null;
		}
		return data.get(key.toString());
	}

	
	public String getOrDefault(Object key, String defaultValue) {
		if (key == null) {
			return null;
		}
		return data.containsKey(key.toString()) ? data.get(key.toString()) : defaultValue;
	}
	
	public AsyncData clone() {
		return new AsyncData(this.data);
	}

	@Override
	public String toString() {
		return "AsyncData " + data;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}
	
	
}
