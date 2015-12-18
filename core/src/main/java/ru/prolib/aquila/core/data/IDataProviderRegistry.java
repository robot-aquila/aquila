package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;

/**
 * Data provider interface.
 */
public interface IDataProviderRegistry {
	
	public DataHandler subscribeForStateUpdates(EditableSecurity security)
			throws DataProviderException;
	
	public DataHandler subscribeForTradeUpdates(EditableSecurity security)
			throws DataProviderException;

}
