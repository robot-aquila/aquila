package ru.prolib.aquila.core.BusinessEntities;

import ru.prolib.aquila.core.EventType;

/**
 * Интерфейс позиции по инструменту.
 * <p>
 * 2012-08-02<br>
 * $Id: Position.java 529 2013-02-19 08:49:04Z whirlwind $
 */
public interface Position {
	
	/**
	 * Получить торговый счет позиции.
	 * <p>
	 * @return торговый счет
	 */
	public Account getAccount();
	
	/**
	 * Получить портфель по которому открыта позиция.
	 * <p>
	 * @return портфель
	 */
	public Portfolio getPortfolio() throws PortfolioException;
	
	/**
	 * Получить дескриптор инструмента.
	 * <p>
	 * @return дескриптор инструмента
	 */
	public SecurityDescriptor getSecurityDescriptor();
	
	/**
	 * Получить инструмент.
	 * <p>
	 * @return инструмент
	 */
	public Security getSecurity() throws SecurityException;
	
	/**
	 * Получить тип события: при изменении позиции.
	 * <p>
	 * @return тип события
	 */
	public EventType OnChanged();
	
	/**
	 * Получить величину вариационной маржи.
	 * <p>
	 * @return вариационная маржа
	 */
	public double getVarMargin();
	
	/**
	 * Получить размер позиции на начало сессии.
	 * <p>
	 * @return размер позиции
	 */
	public long getOpenQty();
	
	/**
	 * Получить размер позиции, заблокированный под текущие операции.
	 * <p>
	 * @return размер позиции
	 */
	public long getLockQty();
	
	/**
	 * Получить размер текущей позиции.
	 * <p>
	 * Суммарная позиция по инструменту.
	 * <p>
	 * @return размер позиции
	 */
	public long getCurrQty();
	
	/**
	 * Получить тип позиции.
	 * <p>
	 * @return тип позиции
	 */
	public PositionType getType();
	
	/**
	 * Получить рыночную стоимость позиции.
	 * <p>
	 * Текущая стоимость позиции не может отражать точную стоимость актива,
	 * так как это можно будет узнать только после продажи. Текущая стоимость
	 * отражает приблизительную стоимость по текущей рыночной цене. Алгоритм
	 * расчета рыночной цены так же может различаться в зависимости от специфики
	 * терминала.
	 * <p>
	 * @return рыночная стоимость позиции или null, если стоимость не расчитана
	 */
	public Double getMarketValue();
	
	/**
	 * Получить балансовую стоимость позиции.
	 * <p>
	 * В зависимости от специфики терминала, балансовая стоимость может отражать
	 * стоимость позиции на момент открытия сессии или стоимость приобретения. 
	 * <p>
	 * @return балансовая стоимость или null, если стоимость не расчитана
	 */
	public Double getBookValue();

}
