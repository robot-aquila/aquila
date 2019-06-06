package ru.prolib.aquila.core.config;

import java.util.Map;

public class KVStoreHash implements KVStore {
	private final Map<String, String> data;

	public KVStoreHash(Map<String, String> data) {
		this.data = data;
	}
	
	@Override
	public boolean hasKey(String key) {
		return data.containsKey(key);
	}

	@Override
	public String get(String key) {
		return data.get(key);
	}

}
