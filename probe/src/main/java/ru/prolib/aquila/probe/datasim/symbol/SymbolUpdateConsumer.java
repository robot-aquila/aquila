package ru.prolib.aquila.probe.datasim.symbol;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public interface SymbolUpdateConsumer {
	
	public void consume(Symbol symbol, DeltaUpdate update, int sequenceID);

}
