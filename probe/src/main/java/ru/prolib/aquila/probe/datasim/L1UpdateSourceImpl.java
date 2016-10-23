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
import ru.prolib.aquila.probe.datasim.l1.L1UpdateHandler;
import ru.prolib.aquila.probe.datasim.l1.L1UpdateHandlerFactory;
import ru.prolib.aquila.probe.datasim.l1.L1UpdateReaderFactory;

/**
 * The common data source to simulate L1 data updates.
 */
public class L1UpdateSourceImpl implements L1UpdateSource {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(L1UpdateSourceImpl.class);
	}
	
	private final L1UpdateHandlerFactory handlerFactory;
	private final Map<Symbol, L1UpdateHandler> handlers;
	private final Lock lock;
	
	L1UpdateSourceImpl(L1UpdateHandlerFactory handlerFactory,
			Map<Symbol, L1UpdateHandler> handlers)
	{
		this.handlerFactory = handlerFactory;
		this.handlers = handlers;
		this.lock = new ReentrantLock();
	}
	
	L1UpdateSourceImpl(L1UpdateHandlerFactory handlerFactory) {
		this(handlerFactory, new HashMap<>());
	}
	
	/**
	 * Constructor.
	 * <p>
	 * @param scheduler - scheduler which is used to schedule L1 updates
	 * @param readerFactory - the reader factory which is used to produce an
	 * iterator through set of L1 updates of specified symbol
	 */
	public L1UpdateSourceImpl(Scheduler scheduler,
			L1UpdateReaderFactory readerFactory)
	{
		this(new L1UpdateHandlerFactory(scheduler, readerFactory));
	}
	

	@Override
	public void close() throws IOException {
		lock.lock();
		try {
			for ( L1UpdateHandler handler : handlers.values() ) {
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
			L1UpdateHandler handler = handlers.get(symbol);
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
			L1UpdateHandler handler = handlers.get(symbol);
			if ( handler != null ) {
				handler.unsubscribe(consumer);
			}
		} finally {
			lock.unlock();
		}
	}

}
