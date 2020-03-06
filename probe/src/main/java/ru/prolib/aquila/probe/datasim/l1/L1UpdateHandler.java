package ru.prolib.aquila.probe.datasim.l1;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
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
	
	public interface IBlockReader {
		
		/**
		 * Set underlying reader.
		 * <p>
		 * @param reader
		 */
		void setReader(CloseableIterator<L1Update> reader);
		
		/**
		 * Read next block.
		 * <p>
		 * @return next data block or null if no more data
		 * @throws NoSuchElementException 
		 * @throws IOException
		 */
		List<L1Update> readBlock() throws NoSuchElementException, IOException;
	}
	
	static class TimeBlockReader implements IBlockReader {
		private CloseableIterator<L1Update> reader;
		private L1Update pending;
		
		public TimeBlockReader() {
			
		}
		
		public TimeBlockReader(CloseableIterator<L1Update> reader) {
			this.reader = reader;
		}
		
		@Override
		public void setReader(CloseableIterator<L1Update> reader) {
			this.reader = reader;
			pending = null;
		}
		
		public L1Update getPendingUpdate() {
			return pending;
		}
		
		@Override
		public List<L1Update> readBlock()
				throws NoSuchElementException, IOException
		{
			List<L1Update> result = new ArrayList<>();
			L1Update prev = null, curr = null;
			if ( pending != null ) {
				result.add(pending);
				prev = pending;
				pending = null;
			}
			while ( reader.next() ) {
				curr = reader.item();
				if ( prev == null ) {
					result.add(curr);
					prev = curr;
				} else {
					if ( prev.getTime().equals(curr.getTime()) ) {
						result.add(curr);
						prev = curr;
					} else {
						pending = curr;
						break;
					}
				}
			}
			return result.size() > 0 ? result : null;
		}
		
	}
	
	static class TickByTickBlockReader implements IBlockReader {
		private final IBlockReader reader;
		private final List<L1Update> lastBlock;
		
		TickByTickBlockReader(IBlockReader reader, List<L1Update> lastBlock) {
			this.reader = reader;
			this.lastBlock = lastBlock;
		}
		
		public TickByTickBlockReader() {
			this(new TimeBlockReader(), new ArrayList<>());
		}

		@Override
		public void setReader(CloseableIterator<L1Update> reader) {
			lastBlock.clear();
			this.reader.setReader(reader);
		}

		@Override
		public List<L1Update> readBlock() throws NoSuchElementException, IOException {
			if ( lastBlock.size() == 0 ) {
				List<L1Update> list = reader.readBlock();
				if ( list != null ) {
					lastBlock.addAll(list);
				}
			}
			return lastBlock.size() > 0 ? Arrays.asList(lastBlock.remove(0)) : null;
		}
		
	}
	
	private final Lock lock;
	private final Symbol symbol;
	private final Scheduler scheduler;
	private final Set<L1UpdateConsumer> consumers;
	private final L1UpdateReaderFactory readerFactory;
	private final IBlockReader blockReader;
	private int sequenceID = 1;
	private boolean sequenceFirstUpdate;
	private CloseableIterator<L1Update> reader;
	private Instant startTime;
	
	L1UpdateHandler(Symbol symbol,
			Scheduler scheduler,
			L1UpdateReaderFactory readerFactory,
			Set<L1UpdateConsumer> consumers,
			IBlockReader blockReader)
	{
		this.lock = new ReentrantLock();
		this.symbol = symbol;
		this.scheduler = scheduler;
		this.readerFactory = readerFactory;
		this.consumers = consumers;
		this.blockReader = blockReader;
	}
	
	public L1UpdateHandler(Symbol symbol, Scheduler scheduler,
			L1UpdateReaderFactory readerFactory)
	{
		this(symbol, scheduler, readerFactory, new HashSet<>(), new TickByTickBlockReader());
	}
	
	int getCurrentSequenceID() {
		lock.lock();
		try {
			return sequenceID;
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Set currently active update reader. For testing purposes only.
	 * <p>
	 * @param reader - reader to use
	 */
	void setCurrentReader(CloseableIterator<L1Update> reader) {
		lock.lock();
		try {
			this.reader = reader;
		} finally {
			lock.unlock();
		}
	}
	
	public IBlockReader getBlockReader() {
		return blockReader;
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
				Instant _start_time = getStartTime();
				logger.debug("Starting new sequence {} for {} at {}", new Object[] { sequenceID, symbol, _start_time });
				sequenceFirstUpdate = true;
				reader = readerFactory.createReader(symbol, _start_time);
				blockReader.setReader(reader);
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
	public void consume(List<L1Update> updates, int sequenceID) {
		List<L1UpdateConsumer> list = null;
		boolean dump_first_update = false;
		lock.lock();
		try {
			if ( this.sequenceID != sequenceID ) {
				logger.debug("Skip obsolete updates for sequence {}. Current sequence is: ", sequenceID, this.sequenceID);
				return; // skip obsolete task
			}
			list = new ArrayList<>(consumers);
			dump_first_update = sequenceFirstUpdate;
			sequenceFirstUpdate = false;
		} finally {
			lock.unlock();
		}
		for ( L1Update update : updates ) {
			if ( dump_first_update ) {
				dump_first_update = false;
				logger.debug("First update of sequence {}: {}", sequenceID, update);
			}
			for ( L1UpdateConsumer consumer : list ) {
				consumer.consume(update);
			}
		}
		scheduleUpdate();
	}
	
	/**
	 * Set start time of reading data.
	 * <p>
	 * @param time - start time of data
	 */
	public void setStartTime(Instant time) {
		lock.lock();
		try {
			startTime = time;
		} finally {
			lock.unlock();
		}
	}
	
	private void finishSequence() {
		lock.lock();
		try {
			if ( reader != null ) {
				logger.debug("Sequence {} finished for {} at {}",
						new Object[] { sequenceID, symbol, scheduler.getCurrentTime() });
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
			List<L1Update> updates = null;
			if ( consumers.size() > 0
			  && (updates = blockReader.readBlock()) != null )
			{
				scheduler.schedule(new L1UpdateTask(updates, sequenceID, this), updates.get(0).getTime());
				return;
			}
		} catch ( IOException e ) {
			logger.error("Error reading update of {}:", symbol, e);
		} finally {
			lock.unlock();
		}
		finishSequence();
	}
	
	private Instant getStartTime() {
		return startTime == null ? scheduler.getCurrentTime() : startTime;
	}
	
}
