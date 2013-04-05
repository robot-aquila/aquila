package ru.prolib.aquila.ib.subsys.api;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.ib.IBException;

import com.ib.client.Contract;
import com.ib.client.Order;

/**
 * Интерфейс обертки подключения к терминалу.
 * <p>
 * 2012-11-14<br>
 * $Id: IBClient.java 499 2013-02-07 10:43:25Z whirlwind $
 */
public interface IBClient extends IBApiEventDispatcher {
	
	public boolean isConnected();
	
	public void eConnect(String host, int port, int clientId)
		throws IBException;

	public void eDisconnect();
	
	public void reqContractDetails(int reqId, Contract contract)
		throws IBException;
	
	public void reqAccountUpdates (boolean subscribe, String account)
		throws IBException;
	
	public void reqManagedAccts()
		throws IBException;
	
	public void placeOrder(int orderId, Contract contract, Order order)
		throws IBException;
	
	public void cancelOrder(int orderId)
		throws IBException;
	
	public void reqMktData(int tickerId, Contract contract,
			String genericTickList, boolean snapshot)
				throws IBException;
	
	public void cancelMktData(int tickerId);
	
	public void reqAutoOpenOrders(boolean bAutoBind) throws IBException;
	
	public EventType OnConnectionOpened();

}
