package ru.prolib.aquila.probe.datasim.l1;

import ru.prolib.aquila.core.BusinessEntities.L1Update;

public interface SymbolL1UpdateConsumer {

	public void consume(L1Update update, int sequenceID);
	
}
