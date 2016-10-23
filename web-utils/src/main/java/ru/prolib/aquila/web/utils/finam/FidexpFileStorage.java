package ru.prolib.aquila.web.utils.finam;

import java.io.File;

import ru.prolib.aquila.data.storage.file.FileConfig;
import ru.prolib.aquila.data.storage.file.FileStorage;
import ru.prolib.aquila.data.storage.file.FileStorageImpl;

public class FidexpFileStorage {
	private static final String DEFAULT_STORAGE_ID = "FINAM";

	public static FileStorage createStorage(File root) {
		return new FileStorageImpl(root, DEFAULT_STORAGE_ID, new FileConfig(".csv.gz", ".part.csv.gz"));
	}
	
}
