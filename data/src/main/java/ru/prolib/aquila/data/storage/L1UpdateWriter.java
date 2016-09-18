package ru.prolib.aquila.data.storage;

import java.io.Closeable;
import java.io.IOException;
import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.L1Update;

public interface L1UpdateWriter extends Closeable {
	
	public void writeUpdate(L1Update update) throws IOException;
	
	public void flush(Instant time) throws IOException;

}
