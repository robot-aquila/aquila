package ru.prolib.aquila.datatools.tickdatabase;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.BusinessEntities.SecurityTickEvent;
import ru.prolib.aquila.core.BusinessEntities.TaskHandler;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.Tick;
import ru.prolib.aquila.core.BusinessEntities.Trade;
import ru.prolib.aquila.ui.AquilaPlugin;
import ru.prolib.aquila.ui.AquilaUI;
import ru.prolib.aquila.ui.ServiceLocator;

/**
 * Plugin to write to tick database.
 */
public class TickDatabasePlugin implements AquilaPlugin, EventListener, Runnable {
	private static final Logger logger;
	private static final ZoneOffset zone;
	
	static {
		logger = LoggerFactory.getLogger(TickDatabasePlugin.class);
		zone = ZoneOffset.UTC;
	}
	
	private final Lock lock;
	private Terminal terminal;
	private TickDatabase database;
	private Instant marker;
	private TaskHandler taskHandler;
	
	public TickDatabasePlugin() {
		super();
		lock = new ReentrantLock();
	}

	@Override
	public void start() throws StarterException {
		lock.lock();
		try {
			terminal.onSecurityLastTrade().addListener(this);
			scheduleNextMarker();
		} finally {
			lock.unlock();
		}
	}
	
	private void scheduleNextMarker() {
		marker = getMarkerTime();
		taskHandler = terminal.schedule(this, getTimeToSendMarker(marker));
	}
	
	private Instant getTimeToSendMarker(Instant x) {
		Instant dummy = x.plus(5, ChronoUnit.MINUTES);
		logger.debug("Time to send marker: {}", dummy);
		return dummy;
	}
	
	private Instant getMarkerTime() {
		Instant dummy = LocalDateTime.ofInstant(terminal.getCurrentTime(), zone)
				.toLocalDate()
				.atStartOfDay()
				.plusDays(1)
				.minus(1, ChronoUnit.MILLIS)
				.toInstant(zone);
		logger.debug("Marker time: {}", dummy);
		return dummy;
	}

	@Override
	public void stop() throws StarterException {
		lock.lock();
		try {
			if ( taskHandler != null ) {
				taskHandler.cancel();
				taskHandler = null;
			}
			terminal.onSecurityLastTrade().removeListener(this);
			try {
				database.close();
				database = null;
			} catch ( IOException e ) {
				throw new StarterException(e);
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void
		initialize(ServiceLocator locator, Terminal terminal, String dbpath)
			throws Exception
	{
		lock.lock();
		try {
			this.terminal = terminal;
			//database = (TickDatabase)
			//	locator.getApplicationContext().getBean("tickDatabase");
		} finally {
			lock.unlock();
		}
		throw new UnsupportedOperationException("The program is outdated");
	}

	@Override
	public void createUI(AquilaUI facade) throws Exception {

	}

	@Override
	public void onEvent(Event event) {
		SecurityTickEvent e = (SecurityTickEvent) event;
		lock.lock();
		try {
			database.write(e.getSecurity().getSymbol(), e.getTick());
		} catch (IOException x) {
			logger.error("Error writing tick: ", x);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void run() {
		lock.lock();
		try {
			database.sendMarker(marker);
			scheduleNextMarker();
		} catch ( IOException e ) {
			logger.error("Error sending marker: {}", e);
		} finally {
			lock.unlock();
		}
	}

}
