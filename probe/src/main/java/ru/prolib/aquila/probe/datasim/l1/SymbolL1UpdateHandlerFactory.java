package ru.prolib.aquila.probe.datasim.l1;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class SymbolL1UpdateHandlerFactory {
	private final Scheduler scheduler;
	private final SymbolL1UpdateReaderFactory readerFactory;
	
	public SymbolL1UpdateHandlerFactory(Scheduler scheduler,
			SymbolL1UpdateReaderFactory readerFactory)
	{
		this.scheduler = scheduler;
		this.readerFactory = readerFactory;
	}
	
	/**
	 * Produce symbol handler.
	 * <p>
	 * @param symbol - the symbol
	 * @return the handler
	 */
	public SymbolL1UpdateHandler produce(Symbol symbol) {
		return new SymbolL1UpdateHandler(symbol, scheduler, readerFactory);
	}

}
