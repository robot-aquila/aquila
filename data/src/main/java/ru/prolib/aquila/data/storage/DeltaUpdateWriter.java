package ru.prolib.aquila.data.storage;

import java.io.Closeable;
import java.io.IOException;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.data.DataFormatException;

public interface DeltaUpdateWriter extends Closeable {
	
	/**
	 * Write a next delta update to the underlying stream.
	 * <p>
	 * @param update - the delta update to be written
	 * @throws IOException - an IO error occurred
	 * @throws DataFormatException - conversion error
	 */
	public void writeUpdate(DeltaUpdate update) throws IOException, DataFormatException;

}
