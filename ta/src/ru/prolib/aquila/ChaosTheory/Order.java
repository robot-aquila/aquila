package ru.prolib.aquila.ChaosTheory;

import ru.prolib.aquila.util.Observable;

/**
 * Интерфейс заявки.
 * 
 * Наблюдаемый объект. Наблюдатели уведомляются об исполнении или отмене заявки.
 * 
 * TODO: методы kill и fill могут быть вызваны из тех мест, где их вызывать
 * запрещено. Желательно вынести их за пределы интерфейса только-для-чтения. 
 */
public interface Order extends Observable {

	public static final int BUY = 1;
	public static final int SELL = -1;
	public static final int ACTIVE = 0;
	public static final int FILLED = 1;
	public static final int KILLED = -1;
	public static final int PENDING = -2;

	/**
	 * Получить комментарий к заявке. 
	 * 
	 * @return комментарий или null, если комментарий не определен
	 */
	public String getComment();

	/**
	 * Получить идентификатор заявки.
	 * 
	 * @return
	 */
	public long getId();

	/**
	 * Получить тип заявки BUY или SELL.
	 * 
	 * @return
	 */
	public int getType();

	/**
	 * Проверить что это заявка на покупку.
	 *  
	 * @return
	 */
	public boolean isBuy();

	/**
	 * Проверить что эта заявка на продажу.
	 * 
	 * @return
	 */
	public boolean isSell();

	/**
	 * Проверить что это лимитная заявка.
	 * 
	 * Лимитная заявка не имеет стоп-цены и не может иметь связанной заявки.
	 * При попытке получить эти значения посредством соответствующих методов
	 * будет возбуждено исключение.
	 * 
	 * @return
	 */
	public boolean isLimitOrder();

	/**
	 * Проверить что это рыночная заявка.
	 * 
	 * Рыночная заявка не имеет цены, стоп-цены и не может иметь связанной
	 * заявки. При попытке получить эти значения посредством соответствующих
	 * методов будет возбуждено исключение.
	 * 
	 * @return
	 */
	public boolean isMarketOrder();

	/**
	 * Проверить что это стоп-заявка.
	 * 
	 * Стоп-заявка характеризуется стоп-ценой, ценой исполнения и может иметь
	 * связанную заявку, которая создается в момент активации этой заявки.
	 * 
	 * @return
	 */
	public boolean isStopOrder();
	
	/**
	 * Проверить была ли заявка отменена.
	 * 
	 * @return
	 */
	public boolean isKilled();
	
	/**
	 * Проверить была ли заявка исполнена.
	 * 
	 * @return
	 */
	public boolean isFilled();
	
	/**
	 * Проверить активна ли заявка.
	 * 
	 * @return true - заявка активна, false - заявка исполнена или снята
	 */
	public boolean isActive();

	/**
	 * Получить количество заявки.
	 * 
	 * @return
	 */
	public int getQty();

	/**
	 * Получить цену заявки.
	 * 
	 * @return
	 * @throws OrderException рыночная заявка, у которой нет установленой цены
	 */
	public double getPrice() throws OrderException;

	/**
	 * Получить стоп-цену заявки.
	 * 
	 * @return
	 * @throws OrderException рыночная или лимитная заявка, у которых нет
	 * установленной стоп-цены
	 */
	public double getStopPrice() throws OrderException;

	/**
	 * Получить статус заявки.
	 * 
	 * @return
	 */
	public int getStatus();

	/**
	 * Активировать рыночную или лимитную заявку.
	 * 
	 * @throws OrderException
	 */
	public void activate() throws OrderException;

	/**
	 * Исполнить рыночную или лимитную заявку.
	 * 
	 * Делает заявку исполненной.
	 * 
	 * @throws OrderException это стоп-заявка
	 */
	public void fill() throws OrderException;

	/**
	 * Исполнить стоп заявку.
	 * 
	 * Делает стоп-заявку исполненной.
	 * 
	 * @param newOrderId номер выставленной лимитной заявки
	 * @return лимитная заявка, созданная в результате исполнения стоп-заявки
	 * @throws OrderException это не стоп-заявка
	 */
	public Order fill(long newOrderId) throws OrderException;

	/**
	 * Отменить заявку.
	 * 
	 * @throws OrderException
	 */
	public void kill() throws OrderException;

	/**
	 * Получить связаную заявку.
	 * 
	 * Данный метод работает только для стоп-заявок.
	 * 
	 * @return заявка, выставленная по исполнению этой стоп-заявки или null,
	 * если данная стоп-заявка не исполнена
	 * @throws OrderException это не стоп-заявка
	 */
	public Order getRelatedOrder() throws OrderException;

}