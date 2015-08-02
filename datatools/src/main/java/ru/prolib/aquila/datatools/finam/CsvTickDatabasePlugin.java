package ru.prolib.aquila.datatools.finam;

import java.io.File;
import java.io.IOException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.BusinessEntities.SecurityTradeEvent;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.Trade;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.tickdatabase.TickDatabase;
import ru.prolib.aquila.ui.AquilaPlugin;
import ru.prolib.aquila.ui.AquilaUI;
import ru.prolib.aquila.ui.ServiceLocator;

public class CsvTickDatabasePlugin implements AquilaPlugin, EventListener, Runnable {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(CsvTickDatabasePlugin.class);
	}
	
	private Terminal terminal;
	private TickDatabase database;
	private DateTime marker;

	@Override
	public void start() throws StarterException {
		terminal.OnSecurityTrade().addListener(this);
		scheduleNextMarker();
	}
	
	private void scheduleNextMarker() {
		marker = getMarkerTime();
		terminal.schedule(this, getTimeToSendMarker(marker));
	}
	
	private DateTime getTimeToSendMarker(DateTime x) {
		DateTime dummy = x.plusMinutes(5);
		logger.debug("Time to send marker: {}", dummy);
		return dummy;
	}
	
	private DateTime getMarkerTime() {
		DateTime dummy = terminal.getCurrentTime()
				.withTimeAtStartOfDay()
				.plusDays(1)
				.minus(1);
		logger.debug("Marker time: {}", dummy);
		return dummy;
	}

	@Override
	public void stop() throws StarterException {
		terminal.cancel(this);
		terminal.OnSecurityTrade().removeListener(this);
		try {
			database.close();
		} catch ( IOException e ) {
			throw new StarterException(e);
		}
	}

	@Override
	public void
		initialize(ServiceLocator locator, Terminal terminal, String dbpath)
			throws Exception
	{
		this.terminal = terminal;
		database = FinamTools.newTickDatabase(terminal, new File(dbpath));
	}

	@Override
	public void createUI(AquilaUI facade) throws Exception {

	}

	@Override
	public void onEvent(Event event) {
		SecurityTradeEvent e = (SecurityTradeEvent) event;
		Trade trade = e.getTrade();
		try {
			database.write(trade.getSecurityDescriptor(),
				new Tick(trade.getTime(), trade.getPrice(),
					trade.getQty().intValue()));
		} catch (IOException x) {
			logger.error("Error writing tick: ", x);
		}
	}
	

	@Override
	public void run() {
		try {
			database.sendMarker(marker);
		} catch ( IOException e ) {
			logger.error("Error sending marker: {}", e);
		}
		scheduleNextMarker();
	}

}
