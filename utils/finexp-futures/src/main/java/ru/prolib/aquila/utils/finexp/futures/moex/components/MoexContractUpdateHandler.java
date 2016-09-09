package ru.prolib.aquila.utils.finexp.futures.moex.components;

import java.io.IOException;
import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainer;
import ru.prolib.aquila.core.BusinessEntities.UpdatableStateContainerImpl;
import ru.prolib.aquila.data.DataFormatException;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.DeltaUpdate;
import ru.prolib.aquila.data.storage.DeltaUpdateWriter;
import ru.prolib.aquila.web.utils.WUWebPageException;
import ru.prolib.aquila.web.utils.moex.Moex;
import ru.prolib.aquila.web.utils.moex.MoexContractFileStorage;

/**
 * This is an update handler of single contract.
 * <p>
 * <b>WARNING</b>: Don't forget to close the handler to keep all available data.
 * <p>
 * This class handles the contract attribute changes at MOEX website. At first
 * execution it loads the existing local contract info and determines will be an
 * update a snapshot or a regular delta-update. With every execution the handler
 * retrieves contract details and compares it with an internal cache. The task
 * is done when at least one attribute was changed. In this case the handler
 * writes a delta-update to the storage and clears the cache. After delta-update
 * is written all subsequent executions has no effect.
 */
public class MoexContractUpdateHandler implements UpdateHandler {
	private final Moex moex;
	private final MoexContractFileStorage storage;
	private final Symbol symbol;
	private final Instant updatePlannedTime;
	private final UpdatableStateContainer container; 
	private final ChangesAccumulator accumulator;
	private boolean snapshot = true, firstTime = true, done = false;
	
	/**
	 * Constructor.
	 * <p>
	 * @param moex - the MOEX facade
	 * @param storage - storage of delta-updates
	 * @param symbol - the symbol to track updates
	 * @param updatePlannedTime - this is planned time of delta-update to write
	 */
	public MoexContractUpdateHandler(Moex moex, MoexContractFileStorage storage,
			Symbol symbol, Instant updatePlannedTime)
	{
		this.moex = moex;
		this.storage = storage;
		this.symbol = symbol;
		this.updatePlannedTime = updatePlannedTime;
		this.container = new UpdatableStateContainerImpl("CONTRACT-" + symbol);
		this.accumulator = new ChangesAccumulator(container); 
	}
	
	/**
	 * Execute an update check and store the data if available.
	 * <p>
	 * @return true if the delta-update has been written, false otherwise
	 * @throws DataStorageException - the local data storage exception. Such
	 * exceptions requires a higher attention because it points to a local issue.
	 * @throws WEWebPageException - the MOEX facade exception. It may point to a
	 * temporary problem.
	 */
	@Override
	public boolean execute() throws DataStorageException, WUWebPageException {
		if ( done ) {
			return true;
		}
		if ( firstTime ) {
			// The first-time execution - load local data to the container
			loadLocalData();
			firstTime = false;
		}
		if ( accumulator.accumulate(moex.getContractDetails(symbol)) ) {
			writeUpdate();
			done = true;
			return true;
		}
		return false;
	}

	@Override
	public void close() throws IOException {
		if ( ! done ) {
			done = true;
			if ( accumulator.hasChanges() ) {
				try {
					writeUpdate();
				} catch ( DataStorageException e ) {
					throw new IOException(e);
				}
			}
		}
	}
	
	private void loadLocalData() throws DataStorageException {
		snapshot = true;
		try ( CloseableIterator<DeltaUpdate> reader = storage.createReader(symbol) ) {
			while ( reader.next() ) {
				container.update(reader.item().getContents());
				snapshot = false;
			}
		} catch ( IOException e ) {
			throw new DataStorageException("Error reading local data: ", e);
		}
	}
	
	private void writeUpdate() throws DataStorageException {
		DeltaUpdate update = accumulator.createDeltaUpdate(snapshot, updatePlannedTime);
		try ( DeltaUpdateWriter writer = storage.createWriter(symbol) ) {
			writer.writeUpdate(update);
		} catch ( DataFormatException e ) {
			throw new DataStorageException("Error writing local data: ", e);
		} catch ( IOException e ) {
			throw new DataStorageException("Error writing local data: ", e);
		}
	}

}
