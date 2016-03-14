package ru.prolib.aquila.datatools.tickdatabase;

import java.io.File;
import java.io.IOException;

public class SimpleCsvL1UpdateReaderFactory implements L1UpdateReaderFactory {
	private File file;
	
	public SimpleCsvL1UpdateReaderFactory(File file) {
		this.file = file;
	}
	
	public SimpleCsvL1UpdateReaderFactory() {
		this(null);
	}

	@Override
	public synchronized L1UpdateReader createReader() throws IOException {
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