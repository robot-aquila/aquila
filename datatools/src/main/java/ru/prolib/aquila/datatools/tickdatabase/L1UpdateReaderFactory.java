package ru.prolib.aquila.datatools.tickdatabase;

import java.io.IOException;

public interface L1UpdateReaderFactory {

	public L1UpdateReader createReader() throws IOException;
	
}