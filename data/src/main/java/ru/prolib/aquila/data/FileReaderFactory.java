package ru.prolib.aquila.data;

import java.io.File;

public interface FileReaderFactory<T> extends ReaderFactory<T> {
	
	public void setDataFile(File file);

}
