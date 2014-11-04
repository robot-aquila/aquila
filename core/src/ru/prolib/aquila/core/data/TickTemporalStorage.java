package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.BusinessEntities.CurrencyPair;

/**
 * Интерфейс хранилища темпоральных тиковых данных.
 */
public interface TickTemporalStorage {
	
	/**
	 * Получить темпоральную переменную.
	 * <p>
	 * @param dataId идентификатор переменной
	 * @return хранилище темпорального значения
	 * @throws DataException
	 */
	public Aqtemporal<Tick> getTemporal(String dataId) throws DataException;
	
	public Aqtemporal<Tick> getTemporal(CurrencyPair pair) throws DataException;

}
