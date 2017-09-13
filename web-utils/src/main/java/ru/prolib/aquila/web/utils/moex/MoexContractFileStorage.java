package ru.prolib.aquila.web.utils.moex;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.CloseableIteratorStub;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.DeltaUpdateWriter;
import ru.prolib.aquila.data.storage.file.FileConfig;
import ru.prolib.aquila.data.storage.file.SymbolFileStorage;
import ru.prolib.aquila.data.storage.file.SymbolFileStorageImpl;
import ru.prolib.aquila.data.storage.file.PtmlFactory;

/**
 * MOEX contract data storage.
 * <p>
 * This class to store and read files of delta-updates of MOEX contract changes.
 */
public class MoexContractFileStorage {	
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(MoexContractFileStorage.class);
	}
	
	private static final String STORAGE_ID = "MOEX_CONTRACT";
	private final SymbolFileStorage fileStorage;
	private final PtmlFactory ptmlFactory;
		
	public MoexContractFileStorage(File root) {
		fileStorage = new SymbolFileStorageImpl(root, STORAGE_ID,
				new FileConfig("-moex-contract-details-daily.txt", "-moex-contract-details-daily.tmp"));
		ptmlFactory = new PtmlFactory(new MoexContractPtmlConverter());
	}
	
	/**
	 * Get set of available symbols stored to the storage.
	 * <p>
	 * @return set of available symbols
	 * @throws DataStorageException - an error occurred
	 */
	public Set<Symbol> getSymbols() throws DataStorageException {
		return fileStorage.scanForSymbols();
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
		logger.debug("Opening data file for reading: {}", file);
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
		logger.debug("Opening data file for writing: {}", file);
		return ptmlFactory.createWriter(file);
	}

	/**
	 * Get filename of symbol data.
	 * <p>
	 * This utility method returns a filename of the symbol data. This may
	 * helpful to access the raw data or to show the path to it.
	 * <p>
	 * @param symbol - the symbol
	 * @return path to data file
	 */
	public File getDataFile(Symbol symbol) {
		return fileStorage.getDataFile(symbol);
	}
	
}
