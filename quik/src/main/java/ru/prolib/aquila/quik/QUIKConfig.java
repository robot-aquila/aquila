package ru.prolib.aquila.quik;

/**
 * Интерфейс конфигурации импортируемых таблиц.
 * <p>
 * 2012-11-09<br>
 * $Id: QUIKConfig.java 547 2013-02-26 04:45:07Z whirlwind $
 */
public interface QUIKConfig {
	
	/**
	 * Имя таблицы инструментов.
	 * <p> 
	 * @return имя таблицы
	 */
	public String getSecurities();
	
	/**
	 * Имя таблицы всех сделок.
	 * <p> 
	 * @return имя таблицы
	 */
	public String getAllDeals();
	
	/**
	 * Имя таблицы собственных сделок.
	 * <p>
	 * @return имя таблицы
	 */
	public String getTrades();
	
	/**
	 * Имя таблицы портфелей по бумагам.
	 * <p>
	 * @return имя таблицы
	 */
	public String getPortfoliosSTK();

	/**
	 * Имя таблицы портфелей по деривативам.
	 * <p>
	 * @return имя таблицы
	 */
	public String getPortfoliosFUT();
	
	/**
	 * Имя таблицы позиций по бумагам.
	 * <p>
	 * @return имя таблицы
	 */
	public String getPositionsSTK();
	
	/**
	 * Имя таблицы позиций по деривативам.
	 * <p>
	 * @return имя таблицы
	 */
	public String getPositionsFUT();
	
	/**
	 * Имя таблицы заявок.
	 * <p>
	 * @return имя таблицы
	 */
	public String getOrders();
	
	/**
	 * Имя таблицы стоп-заявок.
	 * <p>
	 * @return имя таблицы
	 */
	public String getStopOrders();
	
	/**
	 * Получить имя DDE-сервиса.
	 * <p> 
	 * @return имя сервиса
	 */
	public String getServiceName();
	
	/**
	 * Получить путь к каталогу QUIK.
	 * <p>
	 * @return путь к QUIK
	 */
	public String getQUIKPath();
	
	/**
	 * Получить формат даты, соответствующий экспортируемым по DDE данным.
	 * <p>
	 * @return формат даты или null для формата по-умолчанию для локали
	 */
	public String getDateFormat();
	
	/**
	 * Получить формат времени, соответствующий экспортируемым по DDE данным.
	 * <p>
	 * @return формат времени или null для формата по-умолчанию для локали
	 */
	public String getTimeFormat();
	
	/**
	 * Получить признак необходимости подключения к TRANS2QUIK API.
	 * <p>
	 * @return true - не подключаться, false - нужно подключаться
	 */
	public boolean skipTRANS2QUIK();

}
