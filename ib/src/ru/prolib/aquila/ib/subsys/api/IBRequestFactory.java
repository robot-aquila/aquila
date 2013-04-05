package ru.prolib.aquila.ib.subsys.api;

import ru.prolib.aquila.core.utils.Counter;

import com.ib.client.Contract;

/**
 * Интерфейс фабрики запросов.
 * <p>
 * 2012-11-19<br>
 * $Id: IBRequestFactory.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public interface IBRequestFactory {
	
	/**
	 * Создать запрос деталей контракта.
	 * <p>
	 * @param contract дескриптор контракта
	 * @return запрос
	 */
	public IBRequestContract requestContract(Contract contract);
	
	/**
	 * Создать запрос рыночных данных по контракту.
	 * <p>
	 * @param contract дескриптор контракта
	 * @return запрос
	 */
	public IBRequestMarketData requestMarketData(Contract contract);
	
	/**
	 * Создать запрос обновления портфеля.
	 * <p>
	 * @param account код торгового счета
	 * @return запрос
	 */
	public IBRequestAccountUpdates requestAccountUpdates(String account);

	/**
	 * Получить нумератор запросов.
	 * <p>
	 * @return нумератор запросов
	 */
	public Counter getRequestNumerator();

}
