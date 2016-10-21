package ru.prolib.aquila.probe.datasim.symbol;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class SymbolUpdateHandlerFactory {
	private final Scheduler scheduler;
	private final SymbolUpdateReaderFactory readerFactory;
	
	public SymbolUpdateHandlerFactory(Scheduler scheduler, SymbolUpdateReaderFactory readerFactory) {
		this.scheduler = scheduler;
		this.readerFactory = readerFactory;
	}
	
	/**
	 * Produce symbol handler.
	 * <p>
	 * @param symbol - the symbol
	 * @return the handler
	 */
	public SymbolUpdateHandler produce(Symbol symbol) {
		return new SymbolUpdateHandler(symbol, scheduler, readerFactory);
	}

}
