package ru.prolib.aquila.probe.internal;

import org.joda.time.DateTime;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.data.DataException;

/**
 * Интерфейс поставщика данных.
 */
public interface DataProvider {
	
	/**
	 * Начать симуляцию инструмента.
	 * <p>
	 * @param descr дескриптор инструмента
	 * @param start время начала данных
	 * @throws DataException ошибка инициализации поставщика
	 */
	public void startSupply(SecurityDescriptor descr, DateTime startTime)
			throws DataException;

}
