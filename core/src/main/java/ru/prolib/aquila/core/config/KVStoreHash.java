package ru.prolib.aquila.core.config;

import java.util.HashMap;
import java.util.Map;

public class KVStoreHash implements KVWritableStore {
	private final Map<String, String> data;

	public KVStoreHash(Map<String, String> data) {
		this.data = data;
	}
	
	public KVStoreHash() {
		this(new HashMap<>());
	}
	
	@Override
	public boolean hasKey(String key) {
		return data.containsKey(key);
	}

	@Override
	public String get(String key) {
		return data.get(key);
	}

	@Override
	public KVWritableStore add(String key, String value) throws ConfigException {
		if ( data.containsKey(key) ) {
			throw new ConfigException("Key already exists: " + key);
		}
		data.put(key, value);
		return this;
	}

}
