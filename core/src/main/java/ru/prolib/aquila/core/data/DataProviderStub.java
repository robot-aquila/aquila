package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.L1UpdatableStreamContainer;
import ru.prolib.aquila.core.BusinessEntities.MDUpdatableStreamContainer;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class DataProviderStub implements DataProvider {
	public long lastOrderID = 0;

	@Override
	public void subscribeStateUpdates(EditableSecurity security) {
		
	}

	@Override
	public void subscribeLevel1Data(Symbol symbol, L1UpdatableStreamContainer container) {

	}

	@Override
	public void subscribeLevel2Data(Symbol symbol, MDUpdatableStreamContainer container) {

	}

	@Override
	public void subscribeStateUpdates(EditablePortfolio portfolio) {

	}

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

}
