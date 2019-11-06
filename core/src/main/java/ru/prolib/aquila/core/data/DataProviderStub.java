package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
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
	public void subscribe(Symbol symbol, MDLevel type, EditableTerminal terminal) {
		
	}

	@Override
	public void unsubscribe(Symbol symbol, MDLevel type, EditableTerminal terminal) {
		
	}

	@Override
	public void subscribe(Account account, EditableTerminal terminal) {
		
	}

	@Override
	public void unsubscribe(Account account, EditableTerminal terminal) {
		
	}

	@Override
	public void close() {
		
	}

}
