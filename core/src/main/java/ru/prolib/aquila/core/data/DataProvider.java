package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Data provider interface.
 */
public interface DataProvider {
	
	public void subscribeStateUpdates(EditableSecurity security);
	
	public void subscribeLevel1Data(EditableSecurity security);
	
	public void subscribeLevel2Data(EditableSecurity security);
	
	public void subscribeStateUpdates(EditablePortfolio portfolio);
	
	public long getNextOrderID();
	
	public void subscribeRemoteOrders(EditableTerminal terminal);
	
	public void unsubscribeRemoteOrders(EditableTerminal terminal);
	
	public void registerNewOrder(EditableOrder order);
	
	public void cancelOrder(EditableOrder order);

}
