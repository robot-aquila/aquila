package ru.prolib.aquila.web.utils.moex;

import java.io.File;
import java.io.IOException;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.CloseableIteratorStub;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.DeltaUpdate;
import ru.prolib.aquila.data.storage.DeltaUpdateWriter;
import ru.prolib.aquila.data.storage.file.FileStorage;
import ru.prolib.aquila.data.storage.file.FileStorageImpl;
import ru.prolib.aquila.data.storage.file.FileStorageNamespaceV1;
import ru.prolib.aquila.data.storage.file.PtmlFactory;

public class MoexContractFileStorage {	
	private final FileStorage fileStorage;
	private final PtmlFactory ptmlFactory;
	
	public MoexContractFileStorage(File root) {
		fileStorage = new FileStorageImpl(new FileStorageNamespaceV1(root),
				"MOEX_CONTRACT", new MoexContractFiles());
		ptmlFactory = new PtmlFactory(new MoexContractPtmlConverter());
	}
	
	/**
	 * Create reader of contract's delta-updates.
	 * <p>
	 * @param symbol - the symbol
	 * @return set of delta-updates
	 * @throws IOException - an error occurred
	 */
	public CloseableIterator<DeltaUpdate> createReader(Symbol symbol)
			throws IOException
	{
		File file = fileStorage.getDataFile(symbol);
		return file.exists() ?
				ptmlFactory.createReader(file) :
				new CloseableIteratorStub<DeltaUpdate>();
	}
	
	/**
	 * Create writer of contract's delta-updates.
	 * <p>
	 * @param symbol - the symbol
	 * @return the writer of delta-updates
	 * @throws IOException - if an IO error occurred
	 * @throws DataStorageException - an error occurred
	 */
	public DeltaUpdateWriter createWriter(Symbol symbol)
			throws IOException, DataStorageException
	{
		File file = fileStorage.getDataFileForWriting(symbol);
		return ptmlFactory.createWriter(file);
	}
	
}
