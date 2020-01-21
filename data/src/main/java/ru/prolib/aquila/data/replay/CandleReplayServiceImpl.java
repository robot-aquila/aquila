package ru.prolib.aquila.data.replay;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.SPRunnable;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.data.storage.DataStorageException;
import ru.prolib.aquila.data.storage.MDStorage;

public class CandleReplayServiceImpl implements CandleReplayService {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(CandleReplayServiceImpl.class);
	}
	
	public interface Node extends SPRunnable {
		
		/**
		 * Add a new listener.
		 * <p>
		 * @param listener - listener instance
		 * @return subscription handler or null if object is closed
		 */
		SubscrHandler subscribeIfNotClosed(CandleListener listener);
		
		/**
		 * Add a new listener.
		 * <p>
		 * @param listener - listener instance
		 * @return subscription handler
		 * @throws IllegalStateException if node is closed
		 */
		SubscrHandler subscribe(CandleListener listener);
		
		boolean closed();
		void unsubscribe(CandleListener listener);
	}
	
	public interface NodeFactory {
		Node produce(TFSymbol key, Instant time_from);
	}
	
	static class NodeSubscrHandler implements SubscrHandler {
		private final Node node;
		private final CandleListener listener;
		private final AtomicBoolean closed;
		private final boolean confirm;
		private final CompletableFuture<Boolean> f_confirm;
		
		NodeSubscrHandler(Node node, CandleListener listener, boolean confirm) {
			this.node = node;
			this.listener = listener;
			this.closed = new AtomicBoolean(false);
			this.confirm = confirm;
			this.f_confirm = new CompletableFuture<>();
			this.f_confirm.complete(confirm);
		}
		
		@Override
		public void close() {
			if ( closed.compareAndSet(false, true) ) {
				node.unsubscribe(listener);
			}
		}
		
		public boolean isEqualTo(NodeSubscrHandler other) {
			if ( other == this ) {
				return true;
			}
			if ( other == null ) {
				return false;
			}
			return other.node == node && other.listener == listener && other.confirm == confirm;
		}

		@Override
		public CompletableFuture<Boolean> getConfirmation() {
			return f_confirm;
		}
		
	}
	
	public static class NodeImpl implements Node {
		private final Symbol symbol;
		private final String id;
		private final CloseableIterator<Candle> reader;
		private final Set<CandleListener> listeners;
		private final Lock lock;
		private final AtomicBoolean closed;
		
		NodeImpl(Symbol symbol,
				String id,
				CloseableIterator<Candle> reader,
				Set<CandleListener> listeners,
				Lock lock,
				AtomicBoolean closed)
		{
			this.symbol = symbol;
			this.id = id;
			this.reader = reader;
			this.listeners = listeners;
			this.lock = lock;
			this.closed = closed;
		}
		
		public NodeImpl(Symbol symbol, String id, CloseableIterator<Candle> reader) {
			this(symbol, id, reader, new LinkedHashSet<>(), new ReentrantLock(), new AtomicBoolean(false));
		}
		
		Symbol getSymbol() {
			return symbol;
		}
		
		/**
		 * Get node ID.
		 * For testing purposes only.
		 * <p>
		 * @return node ID
		 */
		String getID() {
			return id;
		}
		
		/**
		 * Get candle iterator. For testing purposes only.
		 * <p>
		 * @return iterator
		 */
		CloseableIterator<Candle> getIterator() {
			return reader;
		}
		
		/**
		 * Get original set of listeners. For testing purposes only.
		 * <p>
		 * @return listeners
		 */
		Set<CandleListener> getListeners() {
			return listeners;
		}
		
		/**
		 * Get node lock object. For testing purposes only.
		 * <p>
		 * @return lock
		 */
		Lock getLock() {
			return lock;
		}
		
		/**
		 * Get indicator of closing. For testing purposes only.
		 * <p>
		 * @return indicator variable
		 */
		AtomicBoolean getClosed() {
			return closed;
		}
		
		/**
		 * Check node is closed.
		 * <p>
		 * @return true if node closed, false otherwise
		 */
		@Override
		public boolean closed() {
			return closed.get();
		}
		
		@Override
		public SubscrHandler subscribeIfNotClosed(CandleListener listener) {
			if ( closed() ) {
				return null;
			}
			lock.lock();
			try {
				if ( closed() ) {
					return null;
				}
				listeners.add(listener);
				return new NodeSubscrHandler(this, listener, true);
			} finally {
				lock.unlock();
			}
		}
		
		@Override
		public SubscrHandler subscribe(CandleListener listener) {
			lock.lock();
			try {
				if  ( closed() ) {
					throw new IllegalStateException("Cannot subscribe on closed node");
				}
				listeners.add(listener);
				return new NodeSubscrHandler(this, listener, true);
			} finally {
				lock.unlock();
			}
		}
		
		@Override
		public void unsubscribe(CandleListener listener) {
			if ( closed.get() ) {
				return;
			}
			lock.lock();
			try {
				if ( listeners.remove(listener) && listeners.size() == 0 ) {
					close();
				}
			} finally {
				lock.unlock();
			}
		}

		@Override
		public void run() {
			if ( closed() ) {
				return;
			}
			List<CandleListener> list = null;
			lock.lock();
			try {
				list = new ArrayList<>(listeners);
			} finally {
				lock.unlock();
			}
			if ( list.size() > 0 ) {
				try {
					Candle x = reader.item();
					for ( CandleListener listener : list ) {
						listener.onCandle(x.getEndTime(), symbol, x);
					}
				} catch ( IOException e ) {
					logger.error("Error obtaining candle: ", e);
				}
			}
		}

		@Override
		public Instant getNextExecutionTime(Instant currentTime) {
			try {
				if ( reader.next() ) {
					return reader.item().getEndTime();
				}
			} catch ( IOException e ) {
				logger.error("Replay stopped due to error: ", e);
			}
			close();
			return null;
		}

		@Override
		public boolean isLongTermTask() {
			return false;
		}
		
		private void close() {
			if ( closed.compareAndSet(false, true) ) {
				lock.lock();
				try {
					listeners.clear();
				} finally {
					lock.unlock();
				}
				IOUtils.closeQuietly(reader);
			}
		}

	}
	
	static class NodeFactoryMDS implements NodeFactory {
		private final MDStorage<TFSymbol, Candle> storage;
		
		NodeFactoryMDS(MDStorage<TFSymbol, Candle> storage) {
			this.storage = storage;
		}

		@Override
		public Node produce(TFSymbol key, Instant time_from) {
			try {
				return new NodeImpl(key.getSymbol(), key.toString(), storage.createReaderFrom(key, time_from));
			} catch ( DataStorageException e ) {
				throw new IllegalStateException("Unexpected exception: ", e);
			}
		}
		
	}
	
	
	private final Scheduler scheduler;
	private final NodeFactory factory;
	private final Map<TFSymbol, Node> nodes;
	private final Lock lock;
	
	CandleReplayServiceImpl(Scheduler scheduler,
			NodeFactory factory,
			Map<TFSymbol, Node> nodes,
			Lock lock)
	{
		this.scheduler = scheduler;
		this.factory = factory;
		this.nodes = nodes;
		this.lock = lock;
	}
	
	public CandleReplayServiceImpl(Scheduler scheduler, MDStorage<TFSymbol, Candle> storage) {
		this(scheduler, new NodeFactoryMDS(storage), new HashMap<>(), new ReentrantLock());
	}

	@Override
	public SubscrHandler subscribe(TFSymbol key, CandleListener listener) {
		// It is safe to lock here because node operations are not linked with this class directly.
		SubscrHandler handler;
		lock.lock();
		try {
			// So, nobody can create same node simultaneously but someone can close
			// existing node during search or an existing node can be closed already.
			Node node = nodes.get(key);
			if ( node != null && ! node.closed() ) {
				handler = node.subscribeIfNotClosed(listener);
				if ( handler != null ) {
					return handler;
				}
			}
			// Node was not exists or was closed. Any case the new node must be created.
			nodes.put(key, node = factory.produce(key, scheduler.getCurrentTime()));
			handler = node.subscribe(listener);
			scheduler.schedule(node);
			return handler;
			
		} finally {
			lock.unlock();
		}
	}

}
