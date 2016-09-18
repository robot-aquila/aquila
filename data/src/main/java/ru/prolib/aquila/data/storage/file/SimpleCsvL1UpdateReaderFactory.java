package ru.prolib.aquila.data.storage.file;

import java.io.File;
import java.io.IOException;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.data.ReaderFactory;

public class SimpleCsvL1UpdateReaderFactory implements ReaderFactory<L1Update> {
	private File file;
	
	public SimpleCsvL1UpdateReaderFactory(File file) {
		this.file = file;
	}
	
	public SimpleCsvL1UpdateReaderFactory() {
		this(null);
	}

	@Override
	public synchronized CloseableIterator<L1Update> createReader() throws IOException {
		if ( file == null ) {
			throw new IllegalStateException("File not defined");
		}
		return new SimpleCsvL1UpdateReader(file);
	}
	
	public synchronized File getFile() {
		return file;
	}
	
	public synchronized void setFile(File file) {
		this.file = file;
	}
	
}