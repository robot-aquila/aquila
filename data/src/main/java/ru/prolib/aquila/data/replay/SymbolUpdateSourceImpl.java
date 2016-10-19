package ru.prolib.aquila.data.replay;

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
import ru.prolib.aquila.data.replay.sus.SusHandler;
import ru.prolib.aquila.data.replay.sus.SusHandlerFactory;
import ru.prolib.aquila.data.replay.sus.SusReaderFactory;

/**
 * Standard implementation of update source of symbol state.
 * <p>
 * This class is used in combination with the reader factory which should
 * provide access to set of delta-updates associated with the symbol. This class
 * may also be used for managing any symbol-related data streams targeted to one
 * or several delta-update consumers.
 */
public class SymbolUpdateSourceImpl implements SymbolUpdateSource {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SymbolUpdateSourceImpl.class);
	}
	
	private final SusHandlerFactory handlerFactory;
	private final Map<Symbol, SusHandler> handlers;
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
	SymbolUpdateSourceImpl(SusHandlerFactory handlerFactory, Map<Symbol,
			SusHandler> handlers)
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
	SymbolUpdateSourceImpl(SusHandlerFactory handlerFactory) {
		this(handlerFactory, new HashMap<>());
	}
	
	/**
	 * Constructor.
	 * <p>
	 * @param scheduler - scheduler which is used to schedule state updates
	 * @param readerFactory - the reader factory which is used to produce an
	 * iterator through set of delta-updates of specified symbol
	 */
	public SymbolUpdateSourceImpl(Scheduler scheduler, SusReaderFactory readerFactory) {
		this(new SusHandlerFactory(scheduler, readerFactory));
	}

	@Override
	public void close() {
		lock.lock();
		try {
			Iterator<Map.Entry<Symbol, SusHandler>> it = handlers.entrySet().iterator();
			while ( it.hasNext() ) {
				Map.Entry<Symbol, SusHandler> pair = it.next();
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
			SusHandler handler = handlers.get(symbol);
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
			SusHandler handler = handlers.get(symbol);
			if ( handler != null ) {
				handler.unsubscribe(consumer);
			}
		} finally {
			lock.unlock();
		}
	}

}
