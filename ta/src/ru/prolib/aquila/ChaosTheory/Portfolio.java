package ru.prolib.aquila.ChaosTheory;

/**
 * Интерфейс портфеля по отдельному инструменту.
 */
public interface Portfolio {
	
	/**
	 * Получить актив.
	 * @return
	 */
	public Asset getAsset();
	
	/**
	 * Получить объем доступных денежных средств.
	 * @return доступный объем денежных средств
	 * @throws PortfolioException
	 */
	@Deprecated // TODO: ткнуть в стейт портфеля
	public double getMoney() throws PortfolioException;

	/**
	 * Получить размер текущей позиции.
	 * @return 0 - нейтральная позиция, < 0 - short, > 0 - long,
	 * @throws PortfolioException
	 */
	@Deprecated // TODO: ткнуть в стейт портфеля
	public int getPosition() throws PortfolioException;

	/**
	 * Выставить лимитированную заявку на покупку.
	 * @param qty количество лотов
	 * @param price цена
	 * @return объект заявки
	 * @throws PortfolioException
	 * @throws InterruptedException
	 */
	public Order limitBuy(int qty, double price)
		throws PortfolioException,InterruptedException;
	
	/**
	 * Выставить лимитрированную заявку на покупку.
	 * @param qty количество лотов
	 * @param price цена
	 * @param comment комментарий
	 * @return объект заявки
	 * @throws PortfolioException
	 * @throws InterruptedException
	 */
	public Order limitBuy(int qty, double price, String comment)
		throws PortfolioException,InterruptedException;

	/**
	 * Выставить стоп-заявку на покупку. 
	 * @param qty количество лотов
	 * @param stopPrice стоп-цена
	 * @param price цена
	 * @return объект заявки
	 * @throws PortfolioException
	 * @throws InterruptedException
	 */
	public Order stopBuy(int qty, double stopPrice, double price)
		throws PortfolioException,InterruptedException;

	/**
	 * Выставить стоп-заявку на покупку.
	 * @param qty количество лотов
	 * @param stopPrice стоп-цена
	 * @param price цена
	 * @param msg комментарий к заявке
	 * @return объект заявки
	 * @throws PortfolioException
	 * @throws InterruptedException
	 */
	public Order stopBuy(int qty, double stopPrice, double price, String msg)
		throws PortfolioException,InterruptedException;
	
	/**
	 * Выставить лимитированную заявку на продажу.
	 * @param qty количество лотов
	 * @param price цена
	 * @return объект заявки
	 * @throws PortfolioException
	 * @throws InterruptedException
	 */
	public Order limitSell(int qty, double price)
		throws PortfolioException,InterruptedException;
	
	/**
	 * Выставить лимитрированную заявку на продажу.
	 * @param qty количество лотов
	 * @param price цена
	 * @param comment комментарий
	 * @return объект заявки
	 * @throws PortfolioException
	 * @throws InterruptedException
	 */
	public Order limitSell(int qty, double price, String comment)
		throws PortfolioException,InterruptedException;

	/**
	 * Выставить стоп-заявку на продажу.
	 * @param qty количество лотов
	 * @param stopPrice стоп-цена
	 * @param price цена
	 * @return объект заявки
	 * @throws PortfolioException
	 * @throws InterruptedException
	 */
	public Order stopSell(int qty, double stopPrice, double price)
		throws PortfolioException,InterruptedException;

	/**
	 * Выставить стоп-заявку на продажу.
	 * @param qty количество лотов
	 * @param stopPrice стоп-цена
	 * @param price цена
	 * @param msg комментарий к заявке
	 * @return объект заявки
	 * @throws PortfolioException
	 * @throws InterruptedException
	 */
	public Order stopSell(int qty, double stopPrice, double price, String msg)
		throws PortfolioException,InterruptedException;

	/**
	 * Снять указанную заявку.
	 * @param order объект заявки
	 * @throws PortfolioException
	 * @throws InterruptedException
	 */
	public void kill(Order order)
		throws PortfolioException,InterruptedException;

	/**
	 * Снять все заявки.
	 * 
	 * Снимаются все лимитные и стоп-заявки.
	 * 
	 * @throws PortfolioException
	 * @throws InterruptedException
	 */
	public void killAll() throws PortfolioException,InterruptedException;
	
	/**
	 * Снять все заявки по типу операции.
	 * 
	 * Снимаются все лимитные и стоп-заявки указанного типа.
	 * 
	 * @param type тип операции {@link Order#SELL} или {@link Order#BUY}
	 * @throws PortfolioException
	 * @throws InterruptedException
	 */
	public void killAll(int type)
		throws PortfolioException,InterruptedException;
	
	/**
	 * Дожидаться исполнения или отмены заявки не дольше указанного времени.
	 * @param order заявка
	 * @param timeout таймаут в миллисекундах
	 * @throws PortfolioException
	 * @throws PortfolioTimeoutException
	 * @throws InterruptedException
	 */
	@Deprecated // TODO: ткнуть в заявки
	public void waitForComplete(Order order, long timeout)
		throws PortfolioTimeoutException,
			   PortfolioException,
			   InterruptedException;
	
	/**
	 * Дожидаться выхода в нейтральную позицию не дольше указанного времени.
	 * @param timeout тймаут в миллисекундах
	 * @throws PortfolioTimeoutException
	 * @throws PortfolioException
	 * @throws InterruptedException
	 */
	@Deprecated // TODO: ткнуть в стейт портфеля
	public void waitForNeutralPosition(long timeout)
		throws PortfolioTimeoutException,
			   PortfolioException,
			   InterruptedException;

}
