package ru.prolib.aquila.datatools.tickdatabase;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.SecurityTickEvent;
import ru.prolib.aquila.core.BusinessEntities.Terminal;

public class SimpleL1Recorder implements EventListener {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(SimpleL1Recorder.class);
	}
	
	public interface WriterFactory {
		
		public L1UpdateWriter createWriter(File file) throws IOException;
		
	}
	
	public static class SimpleCsvL1WriterFactory implements WriterFactory {

		@Override
		public L1UpdateWriter createWriter(File file) throws IOException {
			return new SimpleCsvL1UpdateWriter(file);
		}
		
	}
	
	private final Terminal terminal;
	private final Lock lock;
	private final WriterFactory writerFactory;
	private boolean started = false;
	private L1UpdateWriter writer;
	
	public SimpleL1Recorder(Terminal terminal, WriterFactory writerFactory) {
		super();
		this.lock = new ReentrantLock();
		this.terminal = terminal;
		this.writerFactory = writerFactory;
	}
	
	public SimpleL1Recorder(Terminal terminal) {
		this(terminal, new SimpleCsvL1WriterFactory());
	}
	
	public void close() {
		stopWritingUpdates();
	}
	
	public boolean isStarted() {
		lock.lock();
		try {
			return started;
		} finally {
			lock.unlock();
		}
	}
	
	public void startWritingUpdates(File file) throws IOException {
		lock.lock();
		try {
			if ( started ) {
				throw new IllegalStateException("Already started");
			}
			writer = writerFactory.createWriter(file);
			started = true;
			terminal.onSecurityBestAsk().addListener(this);
			terminal.onSecurityBestBid().addListener(this);
			terminal.onSecurityLastTrade().addListener(this);
		} finally {
			lock.unlock();
		}
	}
	
	public void stopWritingUpdates() {
		lock.lock();
		try {
			if ( started ) {
				terminal.onSecurityBestAsk().removeListener(this);
				terminal.onSecurityBestBid().removeListener(this);
				terminal.onSecurityLastTrade().removeListener(this);
				if ( writer != null ) {
					try {
						writer.close();
					} catch ( IOException e ) {
						logger.warn("Unexpected exception: ", e);
					}
					writer = null;
				}
				started = false;
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void onEvent(Event event) {
		lock.lock();
		try {
			if ( started ) {
				SecurityTickEvent e = (SecurityTickEvent) event;
				writer.writeUpdate(new L1UpdateImpl(e.getSecurity()
						.getSymbol(), e.getTick()));
			}
		} catch ( IOException e ) {
			logger.error("Stop recording: ", e);
			stopWritingUpdates();
		} finally {
			lock.unlock();
		}
	}

}
