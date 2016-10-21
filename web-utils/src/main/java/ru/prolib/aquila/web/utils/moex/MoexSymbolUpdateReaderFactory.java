package ru.prolib.aquila.web.utils.moex;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.CloseableIteratorStub;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.TimeLimitedDeltaUpdateIterator;
import ru.prolib.aquila.probe.datasim.symbol.SymbolUpdateReaderFactory;

public class MoexSymbolUpdateReaderFactory implements SymbolUpdateReaderFactory {
	private final MoexContractFileStorage fileStorage;
	
	/**
	 * Constructor.
	 * <p>
	 * @param fileStorage - the MOEX contract data storage
	 */
	public MoexSymbolUpdateReaderFactory(MoexContractFileStorage fileStorage) {
		this.fileStorage = fileStorage;
	}
	
	/**
	 * Constructor.
	 * <p>
	 * @param root - the directory which points to the root of MOEX contract
	 * data storage
	 */
	public MoexSymbolUpdateReaderFactory(File root) {
		this(new MoexContractFileStorage(root));
	}
	
	/**
	 * Constructor.
	 * <p>
	 * @param root - the path to the directory which points to the root of MOEX
	 * contract data storage
	 */
	public MoexSymbolUpdateReaderFactory(String root) {
		this(new File(root));
	}

	@Override
	public CloseableIterator<DeltaUpdate>
		createReader(Symbol symbol, Instant startTime) throws IOException
	{
		CloseableIteratorStub<DeltaUpdate> raw = new CloseableIteratorStub<>();
		try ( CloseableIterator<DeltaUpdate> dummy = fileStorage.createReader(symbol) ) {
			while ( dummy.next() ) {
				raw.add(dummy.item());
			}
		}
		return new TimeLimitedDeltaUpdateIterator(new MoexContractSymbolUpdateReader(raw), startTime);
	}

}
