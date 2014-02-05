package ru.prolib.aquila.core.data.getter;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Геттер-конвертер вещественного значения.
 * <p>
 * Позволяет задавать характер поведения геттера: строгое (strict) и нестрогое
 * (nice). Строгое поведение подразумевает, что данные должны быть определены.
 * Ситуация, когда геттер исходного значения возвращает null рассматривается как
 * критическая и приводит к генерации события о паническом состоянии.
 * <p>
 * Нестрогое поведение позволяет игнорировать null-значения, полученные от
 * геттера исходного значения. В этом случае данный класс не выполняет
 * приведение к типу с помощью вызова метода {@link #adapt(Double)} и завершает
 * свою работу возвратом null. Данный подход позволяет использовать данный
 * геттер для полей, представляющих опциональные атрибуты (например, величина
 * спрэда для стоп-заявок, тип которых не предмусматривает указания спрэда). В
 * таком случае результатом работы данного геттера будет значение null, которое
 * (предположительно) будет игнорироваться соответствующим сеттером.
 * <p>
 * @param <R> - тип возвращаемого значения
 * <p>
 * 2013-02-25<br>
 * $Id: GDouble2X.java 543 2013-02-25 06:35:27Z whirlwind $
 */
@Deprecated
abstract public class GDouble2X<R> implements G<R> {
	private final EditableTerminal firePanic;
	private final G<Double> gDouble;
	private final boolean strict;
	private final String msgPrefix;

	/**
	 * Конструктор.
	 * <p>
	 * @param firePanic генератор события о паническом состоянии
	 * @param gDouble геттер вещественного значения
	 * @param strict характер поведения: true - строгий, false - нестрогий
	 * @param msgPrefix префикс сообщения о паническом состоянии
	 */
	public GDouble2X(EditableTerminal firePanic, G<Double> gDouble,
			boolean strict, String msgPrefix)
	{
		super();
		this.firePanic = firePanic;
		this.gDouble = gDouble;
		this.strict = strict;
		this.msgPrefix = msgPrefix;
	}

	/**
	 * Получить генератор событий.
	 * <p>
	 * @return генератор событий
	 */
	public EditableTerminal getFirePanicEvent() {
		return firePanic;
	}

	/**
	 * Проверить строгий характер поведения.
	 * <p>
	 * @return true - строгий характер, false - нестрогий
	 */
	public boolean isStrict() {
		return strict;
	}

	/**
	 * Получить геттер значения.
	 * <p>
	 * @return геттер
	 */
	public G<Double> getValueGetter() {
		return gDouble;
	}

	/**
	 * Получить префикс сообщения об ошибке.
	 * <p>
	 * @return префикс сообщения
	 */
	public String getMessagePrefix() {
		return msgPrefix;
	}

	@Override
	public R get(Object source) throws ValueException {
		Double value = gDouble.get(source);
		if ( value == null ) {
			if ( strict ) {
				String msg = msgPrefix + "NULL values not allowed for: {}";
				firePanic.firePanicEvent(1, msg, new Object[] { gDouble });
			}
			return null;
		} else {
			return adapt(value);
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[value=" + gDouble
			+ ", strict=" + strict + ", msgPfx='" + msgPrefix + "']";
	}
	
	abstract protected R adapt(Double value);
	
	protected boolean fieldsEquals(Object other) {
		GDouble2X<?> o = (GDouble2X<?>) other;
		return new EqualsBuilder()
			.append(firePanic, o.firePanic)
			.append(gDouble, o.gDouble)
			.append(msgPrefix, o.msgPrefix)
			.append(strict, o.strict)
			.isEquals();
	}

}