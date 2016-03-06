package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.MDUpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class DataProviderStub implements DataProvider {

	@Override
	public void subscribeStateUpdates(EditableSecurity security) {
		
	}

	@Override
	public void subscribeLevel1Data(Symbol symbol, L1UpdateConsumer consumer) {

	}

	@Override
	public void subscribeLevel2Data(Symbol symbol, MDUpdateConsumer consumer) {

	}

	@Override
	public void subscribeStateUpdates(EditablePortfolio portfolio) {

	}

	@Override
	public long getNextOrderID() {
		return 0;
	}

	@Override
	public void subscribeRemoteObjects(EditableTerminal terminal) {

	}

	@Override
	public void unsubscribeRemoteObjects() {

	}

	@Override
	public void registerNewOrder(EditableOrder order) {

	}

	@Override
	public void cancelOrder(EditableOrder order) {

	}

}
