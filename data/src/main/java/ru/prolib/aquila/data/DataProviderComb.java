package ru.prolib.aquila.data;

import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.L1UpdatableStreamContainer;
import ru.prolib.aquila.core.BusinessEntities.MDUpdatableStreamContainer;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProvider;

/**
 * Combined data provider. This class combines three sources: symbol updates,
 * L1 data updates and underlying data provider.
 */
public class DataProviderComb implements DataProvider {
	private final SymbolUpdateSource symbolUpdateSource;
	private final L1UpdateSource l1UpdateSource;
	private final DataProvider parent;
	
	public DataProviderComb(SymbolUpdateSource symbolUpdateSource,
			L1UpdateSource l1UpdateSource, DataProvider parent)
	{
		this.symbolUpdateSource = symbolUpdateSource;
		this.l1UpdateSource = l1UpdateSource;
		this.parent = parent;
	}

	@Override
	public void subscribeStateUpdates(EditableSecurity security) {
		symbolUpdateSource.subscribeSymbol(security.getSymbol(), security);
		parent.subscribeStateUpdates(security);
	}

	@Override
	public void subscribeLevel1Data(Symbol symbol, L1UpdatableStreamContainer container) {
		l1UpdateSource.subscribeL1(symbol, container);
		parent.subscribeLevel1Data(symbol, container);
	}

	@Override
	public void subscribeLevel2Data(Symbol symbol, MDUpdatableStreamContainer container) {
		parent.subscribeLevel2Data(symbol, container);
	}

	@Override
	public void subscribeStateUpdates(EditablePortfolio portfolio) {
		parent.subscribeStateUpdates(portfolio);
	}

	@Override
	public long getNextOrderID() {
		return parent.getNextOrderID();
	}

	@Override
	public void subscribeRemoteObjects(EditableTerminal terminal) {
		parent.subscribeRemoteObjects(terminal);
	}

	@Override
	public void unsubscribeRemoteObjects(EditableTerminal terminal) {
		parent.unsubscribeRemoteObjects(terminal);
	}

	@Override
	public void registerNewOrder(EditableOrder order) throws OrderException {
		parent.registerNewOrder(order);
	}

	@Override
	public void cancelOrder(EditableOrder order) throws OrderException {
		parent.cancelOrder(order);
	}

}
