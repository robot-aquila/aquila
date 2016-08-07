package ru.prolib.aquila.data.storage.file;

import ru.prolib.aquila.data.DataFormatException;

/**
 * Interface of a delta-update content converter.
 * <p>
 * Delta-update is domain specific. It may contains different types of
 * objects. Derived classes should implement this interface to convert every
 * possible type to a string and vice-versa.
 */
public interface PtmlDeltaUpdateConverter {
	
	public String toString(int token, Object value) throws DataFormatException;
	
	public Object toObject(int token, String value) throws DataFormatException;

}
