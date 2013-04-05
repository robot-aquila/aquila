package ru.prolib.aquila.core.BusinessEntities;

/**
 * Интерфейс конструктора заявок.
 * <p>
 * 2012-12-09<br>
 * $Id: OrderBuilder.java 552 2013-03-01 13:35:35Z whirlwind $
 */
public interface OrderBuilder {

	/**
	 * Создать экземпляр рыночной заявки на покупку.
	 * <p>
	 * @param account торговый счет
	 * @param sec инструмент
	 * @param qty количество
	 * @return экземпляр заявки
	 */
	public Order createMarketOrderB(Account account, Security sec, long qty)
			throws OrderException;
	
	/**
	 * Создать экземпляр рыночной заявки на продажу.
	 * <p>
	 * @param account торговый счет
	 * @param sec инструмент
	 * @param qty количество
	 * @return экземпляр заявки
	 */
	public Order createMarketOrderS(Account account, Security sec, long qty)
			throws OrderException;
	
	/**
	 * Создать экземпляр лимитной заявки на покупку.
	 * <p>
	 * @param account торговый счет
	 * @param sec инструмент
	 * @param qty количество
	 * @param price цена (округляется в соответствии с параметрами инструмента)
	 * @return заявка
	 */
	public Order createLimitOrderB(Account account, Security sec,
			long qty, double price) throws OrderException;
	
	/**
	 * Создать экземпляр лимитной заявки на продажу.
	 * <p>
	 * @param account торговый счет
	 * @param sec инструмент
	 * @param qty количество
	 * @param price цена (округляется в соответствии с параметрами инструмента)
	 * @return заявка
	 */
	public Order createLimitOrderS(Account account, Security sec,
			long qty, double price) throws OrderException;;
	
	/**
	 * Создать экземпляр стоп-заявки заявки на покупку.
	 * <p>
	 * Цены округляются в соответствии с параметрами инструмента.
	 * <p>
	 * @param account торговый счет
	 * @param sec инструмент
	 * @param qty количество
	 * @param stopPrice стоп-цена
	 * @param price цена
	 * @return заявка
	 */
	public Order createStopLimitB(Account account, Security sec,
			long qty, double stopPrice, double price) throws OrderException;
	
	/**
	 * Создать экземпляр стоп-заявки заявки на продажу.
	 * <p>
	 * Цены округляются в соответствии с параметрами инструмента.
	 * <p>
	 * @param account торговый счет
	 * @param sec инструмент
	 * @param qty количество
	 * @param stopPrice стоп-цена
	 * @param price цена
	 * @return заявка
	 */
	public Order createStopLimitS(Account account, Security sec,
			long qty, double stopPrice, double price) throws OrderException;

}
