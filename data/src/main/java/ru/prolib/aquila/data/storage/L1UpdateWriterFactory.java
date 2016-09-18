package ru.prolib.aquila.data.storage;

import java.io.IOException;

public interface L1UpdateWriterFactory {

	public L1UpdateWriter createWriter() throws IOException;
	
}
