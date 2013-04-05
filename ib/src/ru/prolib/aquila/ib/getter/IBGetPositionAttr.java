package ru.prolib.aquila.ib.getter;

import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.ib.event.IBEventUpdatePortfolio;

/**
 * Геттер на основе события типа {@link IBEventUpdatePortfolio}.
 * <p>
 * @param <R> - тип возвращаемого события
 * <p>
 * 2012-12-30<br>
 * $Id: IBGetPositionAttr.java 433 2013-01-14 22:37:52Z whirlwind $
 */
abstract public class IBGetPositionAttr<R> implements G<R> {
	
	/**
	 * Конструктор.
	 */
	public IBGetPositionAttr() {
		super();
	}

	@Override
	public R get(Object source) {
		 return (source instanceof IBEventUpdatePortfolio) ?
			getEventAttr((IBEventUpdatePortfolio) source) : null;
	}
	
	@Override
	abstract public int hashCode();
	
	@Override
	abstract public boolean equals(Object other);
	
	/**
	 * Получить атрибут события.
	 * <p>
	 * @param event событие
	 * @return значение атрибута
	 */
	abstract protected R getEventAttr(IBEventUpdatePortfolio event);

}
