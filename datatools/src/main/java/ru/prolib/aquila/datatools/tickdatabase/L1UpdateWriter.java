package ru.prolib.aquila.datatools.tickdatabase;

import java.io.Closeable;
import java.io.IOException;
import java.time.Instant;

public interface L1UpdateWriter extends Closeable {
	
	public void writeUpdate(L1Update update) throws IOException;
	
	public void flush(Instant time) throws IOException;

}
