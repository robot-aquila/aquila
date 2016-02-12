package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.UpdatableMarketDepthStreamContainer;
import ru.prolib.aquila.core.BusinessEntities.UpdatableTickStreamContainer;

public class DataProviderStub implements DataProvider {

	@Override
	public void subscribeStateUpdates(EditableSecurity security) {
		
	}

	@Override
	public void subscribeLevel1Data(Symbol symbol, UpdatableTickStreamContainer container) {

	}

	@Override
	public void subscribeLevel2Data(Symbol symbol, UpdatableMarketDepthStreamContainer container) {

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
	public void unsubscribeRemoteObjects(EditableTerminal terminal) {

	}

	@Override
	public void registerNewOrder(EditableOrder order) {

	}

	@Override
	public void cancelOrder(EditableOrder order) {

	}

}
