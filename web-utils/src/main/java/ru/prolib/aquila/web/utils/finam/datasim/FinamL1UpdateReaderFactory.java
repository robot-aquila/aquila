package ru.prolib.aquila.web.utils.finam.datasim;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.storage.file.SymbolFileStorage;
import ru.prolib.aquila.probe.datasim.l1.L1UpdateReaderFactory;
import ru.prolib.aquila.web.utils.finam.FidexpFileStorage;

public class FinamL1UpdateReaderFactory implements L1UpdateReaderFactory {
	private final SymbolFileStorage storage;
	
	public FinamL1UpdateReaderFactory(SymbolFileStorage storage) {
		this.storage = storage;
	}
	
	public FinamL1UpdateReaderFactory(File root) {
		this(FidexpFileStorage.createStorage(root));
	}
	
	public FinamL1UpdateReaderFactory(String root) {
		this(new File(root));
	}

	@Override
	public CloseableIterator<L1Update>
		createReader(Symbol symbol, Instant startTime) throws IOException
	{
		return new FinamSeamlessL1UpdateReader(storage, symbol, startTime);
	}

}
