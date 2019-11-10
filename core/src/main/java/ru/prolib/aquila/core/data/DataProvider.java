package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Data provider interface.
 */
public interface DataProvider {
	
	public long getNextOrderID();
	
	public void subscribeRemoteObjects(EditableTerminal terminal);
	
	public void unsubscribeRemoteObjects(EditableTerminal terminal);
	
	public void registerNewOrder(EditableOrder order) throws OrderException;
	
	public void cancelOrder(EditableOrder order) throws OrderException;

	SubscrHandler subscribe(Symbol symbol, MDLevel level, EditableTerminal terminal);
	SubscrHandler subscribe(Account account, EditableTerminal terminal);
	void close();
}
