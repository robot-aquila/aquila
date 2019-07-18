package ru.prolib.aquila.core.config;

public interface KVWritableStore extends KVStore {
	/**
	 * Add option.
	 * <p>
	 * @param key - string key to identify option
	 * @param value - string value of option
	 * @return this instance
	 * @throws ConfigException - can be thrown if option with such name already exists
	 */
	KVWritableStore add(String key, String value) throws ConfigException;

}
