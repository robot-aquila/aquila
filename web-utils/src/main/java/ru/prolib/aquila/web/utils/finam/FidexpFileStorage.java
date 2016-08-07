package ru.prolib.aquila.web.utils.finam;

import java.io.File;

import ru.prolib.aquila.data.storage.file.FileStorage;
import ru.prolib.aquila.data.storage.file.FileStorageImpl;
import ru.prolib.aquila.data.storage.file.FileStorageNamespaceV1;

public class FidexpFileStorage {
	private static final FidexpFiles files = new FidexpFiles();
	private static final String DEFAULT_STORAGE_ID = "FINAM";

	public static FileStorage createStorage(File root) {
		return new FileStorageImpl(new FileStorageNamespaceV1(root),
				DEFAULT_STORAGE_ID, files);
	}
	
}
