package ru.prolib.aquila.ib.subsys.api;

import ru.prolib.aquila.core.EventType;

/**
 * Интерфейс диспетчера событий Interactive Brokers API.
 * <p>
 * 2012-11-14<br>
 * $Id: IBApiEventDispatcher.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public interface IBApiEventDispatcher {
	
	public EventType OnConnectionClosed();
	
	public EventType OnError();
	
	public EventType OnNextValidId();
	
	public EventType OnContractDetails();
	
	public EventType OnManagedAccounts();
	
	public EventType OnUpdateAccount();
	
	public EventType OnUpdatePortfolio();
	
	public EventType OnOpenOrder();
	
	public EventType OnOrderStatus();
	
	public EventType OnTick();

}
