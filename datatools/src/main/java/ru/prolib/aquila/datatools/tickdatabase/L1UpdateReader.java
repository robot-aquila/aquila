package ru.prolib.aquila.datatools.tickdatabase;

import java.io.Closeable;
import java.io.IOException;

public interface L1UpdateReader extends Closeable {
	
	public boolean nextUpdate() throws IOException;
	
	public L1Update getUpdate() throws IOException;

}
