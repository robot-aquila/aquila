package ru.prolib.aquila.probe.datasim.symbol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdate;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class SymbolUpdateHandler implements SymbolUpdateConsumer {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SymbolUpdateHandler.class);
	}
	
	private final Lock lock;
	private final Symbol symbol;
	private final Scheduler scheduler;
	private final List<DeltaUpdateConsumer> consumers;
	private final SymbolUpdateReaderFactory readerFactory;
	private int sequenceID = 1;
	private CloseableIterator<DeltaUpdate> reader;
	
	public SymbolUpdateHandler(Symbol symbol, Scheduler scheduler, SymbolUpdateReaderFactory readerFactory) {
		this.lock = new ReentrantLock();
		this.symbol = symbol;
		this.scheduler = scheduler;
		this.consumers = new ArrayList<>();
		this.readerFactory = readerFactory;
	}
	
	int getCurrentSequenceID() {
		lock.lock();
		try {
			return sequenceID;
		} finally {
			lock.unlock();
		}
	}
	
	public void subscribe(DeltaUpdateConsumer consumer) throws IOException {
		lock.lock();
		try {
			if ( ! consumers.contains(consumer) ) {
				consumers.add(consumer);
			}
			if ( reader == null ) {
				startNewSequence();
			}
		} finally {
			lock.unlock();
		}
	}
	
	public void unsubscribe(DeltaUpdateConsumer consumer) {
		lock.lock();
		try {
			consumers.remove(consumer);
			if ( consumers.size() == 0 ) {
				finishSequence();
			}
		} finally {
			lock.unlock();
		}
	}
	
	public void startNewSequence() throws IOException {
		lock.lock();
		try {
			finishSequence();
			if ( consumers.size() > 0 ) {
				reader = readerFactory.createReader(symbol, scheduler.getCurrentTime());
				scheduleUpdate();
			}
		} finally {
			lock.unlock();
		}
	}
	
	public void close() {
		lock.lock();
		try {
			finishSequence();
			consumers.clear();
		} finally {
			lock.unlock();
		}
	}
	
	@Override
	public void consume(Symbol symbol, DeltaUpdate update, int sequenceID) {
		List<DeltaUpdateConsumer> list = null;
		lock.lock();
		try {
			if ( this.sequenceID != sequenceID ) {
				return; // skip obsolete task
			}
			list = new ArrayList<>(consumers);
		} finally {
			lock.unlock();
		}
		for ( DeltaUpdateConsumer consumer : list ) {
			consumer.consume(update);
		}
		scheduleUpdate();
	}
	
	private void finishSequence() {
		lock.lock();
		try {
			if ( reader != null ) {
				IOUtils.closeQuietly(reader);
				reader = null;
				sequenceID ++;
			}
		} finally {
			lock.unlock();
		}
	}
	
	private void scheduleUpdate() {
		lock.lock();
		try {
			if ( consumers.size() > 0 && reader.next() ) {
				DeltaUpdate update = reader.item();
				scheduler.schedule(new SymbolUpdateTask(symbol, update, sequenceID, this),
						update.getTime());
				return;
			}
		} catch ( IOException e ) {
			logger.error("Error reading update of {}: ", symbol, e);
		} finally {
			lock.unlock();
		}
		// No more updates 
		finishSequence();
	}

}
