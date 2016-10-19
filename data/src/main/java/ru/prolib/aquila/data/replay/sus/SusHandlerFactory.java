package ru.prolib.aquila.data.replay.sus;

import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class SusHandlerFactory {
	private final Scheduler scheduler;
	private final SusReaderFactory readerFactory;
	
	public SusHandlerFactory(Scheduler scheduler, SusReaderFactory readerFactory) {
		this.scheduler = scheduler;
		this.readerFactory = readerFactory;
	}
	
	/**
	 * Produce symbol handler.
	 * <p>
	 * @param symbol - the symbol
	 * @return the handler
	 */
	public SusHandler produce(Symbol symbol) {
		return new SusHandler(symbol, scheduler, readerFactory);
	}

}
