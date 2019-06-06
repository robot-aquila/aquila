package ru.prolib.aquila.core.config;

public interface KVStore {
	
	boolean hasKey(String key);
	String get(String key);

}
