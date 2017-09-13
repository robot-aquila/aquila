package ru.prolib.aquila.web.utils.finam;

import java.io.File;

import ru.prolib.aquila.data.storage.file.FileConfig;
import ru.prolib.aquila.data.storage.file.SymbolFileStorage;
import ru.prolib.aquila.data.storage.file.SymbolFileStorageImpl;

public class FidexpFileStorage {
	private static final String DEFAULT_STORAGE_ID = "FINAM";

	public static SymbolFileStorage createStorage(File root) {
		return new SymbolFileStorageImpl(root, DEFAULT_STORAGE_ID, new FileConfig(".csv.gz", ".part.csv.gz"));
	}
	
}
