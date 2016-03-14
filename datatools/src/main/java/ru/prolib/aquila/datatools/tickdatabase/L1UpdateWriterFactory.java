package ru.prolib.aquila.datatools.tickdatabase;

import java.io.IOException;

public interface L1UpdateWriterFactory {
	
	public L1UpdateWriter createWriter() throws IOException;
	
}