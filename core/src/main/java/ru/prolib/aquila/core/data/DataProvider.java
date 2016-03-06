package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Data provider interface.
 */
public interface DataProvider {
	
	public void subscribeStateUpdates(EditableSecurity security);
	
	public void subscribeLevel1Data(Symbol symbol, L1UpdatableStreamContainer container);
	
	public void subscribeLevel2Data(Symbol symbol, MDUpdatableStreamContainer container);
	
	public void subscribeStateUpdates(EditablePortfolio portfolio);
	
	public long getNextOrderID();
	
	public void subscribeRemoteObjects(EditableTerminal terminal);
	
	public void unsubscribeRemoteObjects();
	
	public void registerNewOrder(EditableOrder order);
	
	public void cancelOrder(EditableOrder order);

}
