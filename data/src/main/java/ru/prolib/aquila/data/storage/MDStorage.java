package ru.prolib.aquila.data.storage;

import java.time.Instant;
import java.util.Set;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;

/**
 * Market data storage interface.
 * <p>
 * Market data storage provides an access to set of dated values associated with specified key.
 * The key may be of any class. For example to store updates of security attributes it should
 * be a symbol. To store candlestick data it should be combination of symbol and timeframe.
 * To store trades it may be combination of an account and symbol or just an account.
 * <p>
 * @param <KeyType> - type of key
 * @param <DataType> - type of value
 */
public interface MDStorage<KeyType, DataType> {
	
	Set<KeyType> getKeys() throws DataStorageException;
	
	/**
	 * Get reader of all available values.
	 * <p>
	 * @param key - the key
	 * @return the reader which allow read all available values sorted by time of occurrence
	 * @throws DataStorageException an error occurred
	 */
	CloseableIterator<DataType> createReader(KeyType key)
		throws DataStorageException;
	
	/**
	 * Get reader of values limited starting from time.
	 * <p>
	 * @param key - the key
	 * @param from - read values from this time inclusive
	 * @return the reader which allow read values sorted by time of occurrence
	 * @throws DataStorageException an error occurred
	 */
	CloseableIterator<DataType> createReaderFrom(KeyType key, Instant from)
		throws DataStorageException;
	
	/**
	 * Get reader of values starting from time and limited by max count.
	 * <p>
	 * @param key - the key
	 * @param from - read values from this time inclusive
	 * @param count - maximum amount of values to read from
	 * @return the reader which allow read values sorted by time of occurrence
	 * @throws DataStorageException an error occurred
	 */
	CloseableIterator<DataType> createReader(KeyType key, Instant from, int count)
		throws DataStorageException;
	
	/**
	 * Get reader of values limited with exact period of time.
	 * <p>
	 * @param key - the key
	 * @param from - read values from this time inclusive
	 * @param to - stop reading when this time is reached (excluding this time).
	 * @return the reader which allow read values sorted by time of occurrence
	 * @throws DataStorageException an error occurred
	 */
	CloseableIterator<DataType> createReader(KeyType key, Instant from, Instant to)
		throws DataStorageException;
	
	/**
	 * Get reader of values limited with max amount and the end time.
	 * <p>
	 * This method is try to load specified amount of last values.
	 * Note that the result set may be less than expected and may be finished
	 * with value dated by any time before specified time.
	 * <p>
	 * @param key - the key
	 * @param count - maximum amount of values to load.
	 * @param to - stop reading when this time is reached (excluding this time)
	 * @return the reader which allow read values sorted by time of occurrence
	 * @throws DataStorageException an error occurred
	 */
	CloseableIterator<DataType> createReader(KeyType key, int count, Instant to)
		throws DataStorageException;

	/**
	 * Get reader of all available values from start to specified time to.
	 * <p>
	 * @param key - the key
	 * @param to - stop reading when this time is reached (excluding this time).
	 * @return the reader which allow read all available values sorted by time of occurrence
	 * @throws DataStorageException an error occurred
	 */
	CloseableIterator<DataType> createReaderTo(KeyType key, Instant to)
		throws DataStorageException;

}
