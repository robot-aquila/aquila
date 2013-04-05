package ru.prolib.aquila.ib.subsys.api;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.ib.subsys.IBServiceLocator;

/**
 * Контроллер соединения с IB API.
 * <p>
 * 2013-01-15<br>
 * $Id: IBConnectionKeeper.java 516 2013-02-11 11:49:55Z whirlwind $
 */
public class IBConnectionKeeper implements Starter, EventListener {
	private final IBServiceLocator locator;
	private final IBClientStarter starter;
	private EventType lastType;
	
	public IBConnectionKeeper(IBServiceLocator locator,
			IBClientStarter starter)
	{
		super();
		this.locator = locator;
		this.starter = starter;
	}

	@Override
	public void start() {
		locator.getApiClient().OnConnectionClosed().addListener(this);
		locator.getApiClient().OnConnectionOpened().addListener(this);
		locator.getTerminal().OnStarted().addListener(this);
		locator.getTerminal().OnStopped().addListener(this);
	}

	@Override
	public void stop() {

	}

	@Override
	public void onEvent(Event event) {
		IBClient client = locator.getApiClient();
		EditableTerminal terminal = locator.getTerminal();
		if ( event.isType(client.OnConnectionOpened()) ) {
			synchronized ( this ) {
				if ( lastType != client.OnConnectionOpened() ) {
					lastType = client.OnConnectionOpened();
					terminal.fireTerminalConnectedEvent();
				}
			}
		} else if ( event.isType(client.OnConnectionClosed()) ) {
			synchronized ( this ) {
				if ( lastType != client.OnConnectionClosed() ) {
					lastType = client.OnConnectionClosed();
					terminal.fireTerminalDisconnectedEvent();
				}
			}
			scheduleTask();
		} else if ( event.isType(terminal.OnStarted()) ) {
			synchronized ( this ) {
				lastType = null;
				restoreConnection();
			}
		} else if ( event.isType(terminal.OnStopped()) ) {
			client.eDisconnect();
		}
	}
	
	/**
	 * Метод восстановления соединения.
	 * <p>
	 * Если терминал в состоянии исполнения и соединение с API не установлено,
	 * то посредством стартера выполняется попытка установить соединение. 
	 */
	public synchronized void restoreConnection() {
		if ( locator.getTerminal().started() &&
		   ! locator.getApiClient().isConnected() )
		{
			starter.start();
		}
	}
	
	private void scheduleTask() {
		locator.getTimer().schedule(new IBConnectionKeeperTask(this),
			/*starter.getConfig().getReconnectingDelay()*/ 5000);
	}

}
