package ru.prolib.aquila.probe.datasim.l1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class L1UpdateHandler implements L1UpdateConsumerEx {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(L1UpdateHandler.class);
	}
	
	private final Lock lock;
	private final Symbol symbol;
	private final Scheduler scheduler;
	private final List<L1UpdateConsumer> consumers;
	private final L1UpdateReaderFactory readerFactory;
	private int sequenceID = 1;
	private CloseableIterator<L1Update> reader;
	
	public L1UpdateHandler(Symbol symbol, Scheduler scheduler,
			L1UpdateReaderFactory readerFactory)
	{
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
	
	public void subscribe(L1UpdateConsumer consumer) throws IOException {
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
	
	public void unsubscribe(L1UpdateConsumer consumer) {
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
	public void consume(L1Update update, int sequenceID) {
		List<L1UpdateConsumer> list = null;
		lock.lock();
		try {
			if ( this.sequenceID != sequenceID ) {
				return; // skip obsolete task
			}
			list = new ArrayList<>(consumers);
		} finally {
			lock.unlock();
		}
		for ( L1UpdateConsumer consumer : list ) {
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
				L1Update update = reader.item();
				scheduler.schedule(new L1UpdateTask(update, sequenceID, this),
						update.getTime());
				return;
			}
		} catch ( IOException e ) {
			logger.error("Error reading update of {}:", symbol, e);
		} finally {
			lock.unlock();
		}
		finishSequence();
	}

}
