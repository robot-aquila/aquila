package ru.prolib.aquila.ib.getter;

import com.ib.client.Order;

import ru.prolib.aquila.core.data.G;

/**
 * Геттер на основе экземпляра заявки IB.
 * <p>
 * 2012-12-15<br>
 * $Id: IBGetOrderAttr.java 433 2013-01-14 22:37:52Z whirlwind $
 */
abstract public class IBGetOrderAttr<T> implements G<T> {
	
	/**
	 * Конструктор.
	 */
	public IBGetOrderAttr() {
		super();
	}

	@Override
	public T get(Object source) {
		return source instanceof Order ? getOrderAttr((Order) source) : null;
	}
	
	/**
	 * Получить атрибут заявки.
	 * <p>
	 * @param order заявка
	 * @return значения атрибута
	 */
	abstract protected T getOrderAttr(Order order);

}
