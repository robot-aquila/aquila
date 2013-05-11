package ru.prolib.aquila.quik.subsys;

import ru.prolib.aquila.core.utils.Dependencies;
import ru.prolib.aquila.dde.utils.table.DDETableListener;

/**
 * Интерфейс фабрики обозревателей таблиц QUIK.
 * <p> 
 * 2012-09-08<br>
 * $Id$
 */
public interface QUIKListenerFactory {
	
	/**
	 * Создать обозреватель таблицы инструментов.
	 * <p>
	 * @return обозреватель таблицы
	 */
	@Deprecated
	public DDETableListener listenSecurities();
	
	/**
	 * Создать обозреватель таблицы всех сделок.
	 * <p>
	 * @return обозреватель таблицы
	 */
	public DDETableListener listenAllDeals();
	
	/**
	 * Создать обозреватель таблицы портфелей по бумагам.
	 * <p>
	 * @return обозреватель таблицы
	 */
	public DDETableListener listenPortfoliosSTK();
	
	/**
	 * Создать обозреватель позиций по бумагам.
	 * <p>
	 * @return обозреватель таблицы
	 */
	public DDETableListener listenPositionsSTK();
	
	/**
	 * Создать обозреватель таблицы портфелей по деривативам.
	 * <p>
	 * @return обозреватель таблицы
	 */
	public DDETableListener listenPortfoliosFUT();
	
	/**
	 * Создать обозреватель таблицы позиций по деривативам.
	 * <p>
	 * @return обозреватель таблицы
	 */
	public DDETableListener listenPositionsFUT();
	
	/**
	 * Создать обозреватель таблицы заявок.
	 * <p>
	 * @return обозреватель таблицы
	 */
	public DDETableListener listenOrders();
	
	/**
	 * Создать обозреватель таблицы стоп-заявок.
	 * <p>
	 * @return обозреватель таблицы
	 */
	public DDETableListener listenStopOrders();
	
	/**
	 * Создать набор зависимостей между таблицами.
	 * <p>
	 * @return набор зависимостей
	 */
	public Dependencies<String> createDependencies();
	
}
