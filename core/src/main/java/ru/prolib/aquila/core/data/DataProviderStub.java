package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class DataProviderStub implements DataProvider {
	public long lastOrderID = 0;

	@Override
	public long getNextOrderID() {
		return ++lastOrderID;
	}

	@Override
	public void subscribeRemoteObjects(EditableTerminal terminal) {

	}

	@Override
	public void unsubscribeRemoteObjects(EditableTerminal terminal) {

	}

	@Override
	public void registerNewOrder(EditableOrder order) throws OrderException {

	}

	@Override
	public void cancelOrder(EditableOrder order) throws OrderException {

	}

	@Override
	public SubscrHandler subscribe(Symbol symbol, MDLevel level, EditableTerminal terminal) {
		return null;
	}

	@Override
	public SubscrHandler subscribe(Account account, EditableTerminal terminal) {
		return null;
	}

	@Override
	public void close() {
		
	}

}
