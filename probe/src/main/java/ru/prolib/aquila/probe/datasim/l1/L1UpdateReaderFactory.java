package ru.prolib.aquila.probe.datasim.l1;

import java.io.IOException;
import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public interface L1UpdateReaderFactory {
	
	public CloseableIterator<L1Update>
		createReader(Symbol symbol, Instant startTime) throws IOException;

}
