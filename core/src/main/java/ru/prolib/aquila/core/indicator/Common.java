package ru.prolib.aquila.core.indicator;

import ru.prolib.aquila.core.data.EditableSeries;

/**
 * Основа индикатора.
 * <p>
 * @param <T> - тип значения индикатора
 * <p>
 * 2012-05-14<br>
 * $Id: Common.java 565 2013-03-10 19:32:12Z whirlwind $
 */
abstract public class Common<T> {
	protected final EditableSeries<T> target;

	/**
	 * Создать объект
	 * <p>
	 * @param target целевое значение
	 */
	public Common(EditableSeries<T> target) {
		super();
		if ( target == null ) {
			throw new NullPointerException("Target value cannot be null");
		}
		this.target = target;
	}

	/**
	 * Получить целевое значение.
	 * <p>
	 * @return целевое значение
	 */
	public EditableSeries<T> getTarget() {
		return target;
	}

}