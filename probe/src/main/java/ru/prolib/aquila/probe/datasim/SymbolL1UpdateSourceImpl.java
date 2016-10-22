package ru.prolib.aquila.probe.datasim;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.L1UpdateSource;
import ru.prolib.aquila.probe.datasim.l1.SymbolL1UpdateHandler;
import ru.prolib.aquila.probe.datasim.l1.SymbolL1UpdateHandlerFactory;
import ru.prolib.aquila.probe.datasim.l1.SymbolL1UpdateReaderFactory;

public class SymbolL1UpdateSourceImpl implements L1UpdateSource {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SymbolL1UpdateSourceImpl.class);
	}
	
	private final SymbolL1UpdateHandlerFactory handlerFactory;
	private final Map<Symbol, SymbolL1UpdateHandler> handlers;
	private final Lock lock;
	
	SymbolL1UpdateSourceImpl(SymbolL1UpdateHandlerFactory handlerFactory,
			Map<Symbol, SymbolL1UpdateHandler> handlers)
	{
		this.handlerFactory = handlerFactory;
		this.handlers = handlers;
		this.lock = new ReentrantLock();
	}
	
	SymbolL1UpdateSourceImpl(SymbolL1UpdateHandlerFactory handlerFactory) {
		this(handlerFactory, new HashMap<>());
	}
	
	/**
	 * Constructor.
	 * <p>
	 * @param scheduler - scheduler which is used to schedule L1 updates
	 * @param readerFactory - the reader factory which is used to produce an
	 * iterator through set of L1 updates of specified symbol
	 */
	public SymbolL1UpdateSourceImpl(Scheduler scheduler,
			SymbolL1UpdateReaderFactory readerFactory)
	{
		this(new SymbolL1UpdateHandlerFactory(scheduler, readerFactory));
	}
	

	@Override
	public void close() throws IOException {
		lock.lock();
		try {
			for ( SymbolL1UpdateHandler handler : handlers.values() ) {
				handler.close();
			}
			handlers.clear();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void subscribeL1(Symbol symbol, L1UpdateConsumer consumer) {
		lock.lock();
		try {
			SymbolL1UpdateHandler handler = handlers.get(symbol);
			if ( handler == null ) {
				handler = handlerFactory.produce(symbol);
				handlers.put(symbol, handler);
			}
			handler.subscribe(consumer);
		} catch ( IOException e ) {
			logger.error("Error subscription symbol: {}", symbol, e);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void unsubscribeL1(Symbol symbol, L1UpdateConsumer consumer) {
		lock.lock();
		try {
			SymbolL1UpdateHandler handler = handlers.get(symbol);
			if ( handler != null ) {
				handler.unsubscribe(consumer);
			}
		} finally {
			lock.unlock();
		}
	}

}
