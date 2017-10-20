package ru.prolib.aquila.data.storage;

public interface MDStorageFactory<KeyType, DataType> {
	
	MDStorage<KeyType, DataType> createStorage(KeyType key)
		throws DataStorageException;

}
