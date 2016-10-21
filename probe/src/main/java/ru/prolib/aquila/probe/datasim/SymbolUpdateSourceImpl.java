package ru.prolib.aquila.probe.datasim;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.SymbolUpdateSource;
import ru.prolib.aquila.probe.datasim.symbol.SymbolUpdateHandler;
import ru.prolib.aquila.probe.datasim.symbol.SymbolUpdateHandlerFactory;
import ru.prolib.aquila.probe.datasim.symbol.SymbolUpdateReaderFactory;

/**
 * The common data source to simulate symbol state updates. 
 * <p>
 * The common implementation is an manager which iterates through updates of
 * symbol and uses scheduler model to execute the state updates. The class is
 * used in combination with an update reader factory which provides an access to
 * set of delta-updates associated with the symbol.
 */
public class SymbolUpdateSourceImpl implements SymbolUpdateSource {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SymbolUpdateSourceImpl.class);
	}
	
	private final SymbolUpdateHandlerFactory handlerFactory;
	private final Map<Symbol, SymbolUpdateHandler> handlers;
	private final Lock lock;
	
	/**
	 * Constructor.
	 * <p>
	 * This is a service constructor for testing purposes. This constructor
	 * is not suitable for common usage.
	 * <p>
	 * @param handlerFactory - the handler factory
	 * @param handlers - the handler map
	 */
	SymbolUpdateSourceImpl(SymbolUpdateHandlerFactory handlerFactory, Map<Symbol,
			SymbolUpdateHandler> handlers)
	{
		this.handlerFactory = handlerFactory;
		this.handlers = handlers;
		this.lock = new ReentrantLock();
	}
	
	/**
	 * Constructor.
	 * <p>
	 * This is a service constructor for testing purposes. This constructor is
	 * not suitable for common usage.
	 * <p>
	 * @param handlerFactory - the handler factory
	 */
	SymbolUpdateSourceImpl(SymbolUpdateHandlerFactory handlerFactory) {
		this(handlerFactory, new HashMap<>());
	}
	
	/**
	 * Constructor.
	 * <p>
	 * @param scheduler - scheduler which is used to schedule state updates
	 * @param readerFactory - the reader factory which is used to produce an
	 * iterator through set of delta-updates of specified symbol
	 */
	public SymbolUpdateSourceImpl(Scheduler scheduler, SymbolUpdateReaderFactory readerFactory) {
		this(new SymbolUpdateHandlerFactory(scheduler, readerFactory));
	}

	@Override
	public void close() {
		lock.lock();
		try {
			Iterator<Map.Entry<Symbol, SymbolUpdateHandler>> it = handlers.entrySet().iterator();
			while ( it.hasNext() ) {
				Map.Entry<Symbol, SymbolUpdateHandler> pair = it.next();
				pair.getValue().close();
			}
			handlers.clear();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void subscribeSymbol(Symbol symbol, DeltaUpdateConsumer consumer) {
		lock.lock();
		try {
			SymbolUpdateHandler handler = handlers.get(symbol);
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
	public void unsubscribeSymbol(Symbol symbol, DeltaUpdateConsumer consumer) {
		lock.lock();
		try {
			SymbolUpdateHandler handler = handlers.get(symbol);
			if ( handler != null ) {
				handler.unsubscribe(consumer);
			}
		} finally {
			lock.unlock();
		}
	}

}
