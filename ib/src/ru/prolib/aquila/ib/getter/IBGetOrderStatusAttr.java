package ru.prolib.aquila.ib.getter;

import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.ib.event.IBEventOrderStatus;

/**
 * Заготовка геттера на основе события типа {@link IBEventOrderStatus}.
 * <p>
 * @param <T> тип возвращаемого значения
 * <p>
 * 2012-12-18<br>
 * $Id: IBGetOrderStatusAttr.java 433 2013-01-14 22:37:52Z whirlwind $
 */
abstract public class IBGetOrderStatusAttr<T> implements G<T> {

	public IBGetOrderStatusAttr() {
		super();
	}
	
	@Override
	public T get(Object source) {
		return source instanceof IBEventOrderStatus
			? getEventAttr((IBEventOrderStatus) source) : null;
	}
	
	/**
	 * Получить атрибут события.
	 * <p>
	 * @param event экземпляр события
	 * @return значение атрибута
	 */
	abstract protected T getEventAttr(IBEventOrderStatus event);

}
