package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;

/**
 * Data provider interface.
 */
public interface DataProvider {
	
	public DataHandler subscribeForStateUpdates(EditableSecurity security);
	
	public DataHandler subscribeForTradeUpdates(EditableSecurity security);

}
