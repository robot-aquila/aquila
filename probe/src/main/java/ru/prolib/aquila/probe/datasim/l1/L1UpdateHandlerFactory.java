package ru.prolib.aquila.probe.datasim.l1;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class L1UpdateHandlerFactory {
	private final Scheduler scheduler;
	private final L1UpdateReaderFactory readerFactory;
	
	public L1UpdateHandlerFactory(Scheduler scheduler,
			L1UpdateReaderFactory readerFactory)
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
	public L1UpdateHandler produce(Symbol symbol) {
		return new L1UpdateHandler(symbol, scheduler, readerFactory);
	}

}
