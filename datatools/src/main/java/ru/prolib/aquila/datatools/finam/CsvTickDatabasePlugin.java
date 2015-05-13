package ru.prolib.aquila.datatools.finam;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.BusinessEntities.SecurityTradeEvent;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.Trade;
import ru.prolib.aquila.core.data.Tick;
import ru.prolib.aquila.datatools.GeneralException;
import ru.prolib.aquila.datatools.tickdatabase.TickDatabase;
import ru.prolib.aquila.ui.AquilaPlugin;
import ru.prolib.aquila.ui.AquilaUI;
import ru.prolib.aquila.ui.ServiceLocator;

public class CsvTickDatabasePlugin implements AquilaPlugin, EventListener {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(CsvTickDatabasePlugin.class);
	}
	
	private Terminal terminal;
	private TickDatabase database;

	@Override
	public void start() throws StarterException {
		terminal.OnSecurityTrade().addListener(this);
	}

	@Override
	public void stop() throws StarterException {
		terminal.OnSecurityTrade().removeListener(this);
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
				new Tick(trade.getTime(), trade.getPrice(), trade.getVolume()));
		} catch (GeneralException x) {
			logger.error("Error writing tick: ", x);
		}
	}

}
