package ru.prolib.aquila.data.storage.file;

import java.io.File;
import java.io.IOException;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.data.FileReaderFactory;
import ru.prolib.aquila.data.TimeConverter;

public class SimpleCsvL1UpdateReaderFactory implements FileReaderFactory<L1Update> {
	private File file;
	private final TimeConverter timeConverter;
	
	public SimpleCsvL1UpdateReaderFactory(File file, TimeConverter timeConverter) {
		this.file = file;
		this.timeConverter = timeConverter;
	}
	
	public SimpleCsvL1UpdateReaderFactory(File file) {
		this(file, null);
	}
	
	public SimpleCsvL1UpdateReaderFactory() {
		this(null, null);
	}

	@Override
	public synchronized CloseableIterator<L1Update> createReader() throws IOException {
		if ( file == null ) {
			throw new IllegalStateException("File was not defined");
		}
		if ( timeConverter != null ) {
			timeConverter.reset();
		}
		return new SimpleCsvL1UpdateReader(file);
	}
	
	public synchronized File getFile() {
		return file;
	}
	
	public TimeConverter getTimeConverter() {
		return timeConverter;
	}
	
	public synchronized void setFile(File file) {
		this.file = file;
	}
	
	@Override
	public void setDataFile(File file) {
		setFile(file);
	}
	
}