package ru.prolib.aquila.quik.subsys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.t2q.*;

/**
 * Контроллер соединения с QUIK API.
 * <p>
 * 2013-02-11<br>
 * $Id$
 */
public class QUIKConnectionKeeper implements Starter, EventListener {
	private static final Logger logger;
	private final QUIKServiceLocator locator;
	private final T2QServiceStarter starter;
	private String lastErrorMessage = null;
	
	static {
		logger = LoggerFactory.getLogger(QUIKConnectionKeeper.class);
	}
	
	public QUIKConnectionKeeper(QUIKServiceLocator locator,
			T2QServiceStarter starter)
	{
		super();
		this.locator = locator;
		this.starter = starter;
	}
	
	/**
	 * Получить сервис-локатор.
	 * <p>
	 * @return сервис-локатор
	 */
	public QUIKServiceLocator getServiceLocator() {
		return locator;
	}
	
	/**
	 * Получить стартер соединения.
	 * <p>
	 * @return стартер
	 */
	public T2QServiceStarter getStarter() {
		return starter;
	}

	@Override
	public void start() {
		locator.getTerminal().OnStarted().addListener(this);
		locator.getTerminal().OnStopped().addListener(this);
		locator.getTerminal().OnDisconnected().addListener(this);
	}

	@Override
	public void stop() {

	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(locator.getTerminal().OnStarted()) ||
			 event.isType(locator.getTerminal().OnDisconnected()) )
		{
			restoreConnection();
		} else if ( event.isType(locator.getTerminal().OnStopped()) ) {
			try {
				starter.stop();
			} catch ( StarterException e ) {
				logger.error("Error disconnect QUIK API (ignore): ", e);
			}
		}
	}
	
	public synchronized void restoreConnection() {
		if ( locator.getTerminal().started() &&
		   ! locator.getTerminal().connected() )
		{
			try {
				starter.start();
			} catch ( StarterException e ) {
				synchronized ( this ) {
					if ( ! e.getMessage().equals(lastErrorMessage) ) {
						lastErrorMessage = e.getMessage();
						logger.error("Connect failed: {}", lastErrorMessage);
					}
				}
				scheduleTask();
			}
		}
	}
	
	private void scheduleTask() {
		locator.getTimer().schedule(new QUIKConnectionKeeperTask(this), 5000);
	}

}
