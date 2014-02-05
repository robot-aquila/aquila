package ru.prolib.aquila.core.data.getter;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Ограничение возвращаемого результата ненулевым значением. 
 * <p>
 * Фактически позволяет добавить любому геттеру ограничение на нулевые значения.
 * Это необходимо в тех случаях, когда нулевое значение свидетельствует о
 * критическом недостатке данных. Данный геттер использует генератор события,
 * для уведомления наблюдателей о критической ситуации. 
 * <p>
 * @param <R> - тип возвращаемого значения
 * <p>
 * 2013-02-16<br>
 * $Id$
 */
@Deprecated
public class GNotNull<R> implements G<R> {
	private final EditableTerminal firePanic;
	private final G<R> gValue;
	private final String msgPrefix;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param firePanic генератор события о критической ситуации
	 * @param gValue источник значений
	 * @param msgPrefix msgPrefix префикс сообщения о паническом состоянии
	 */
	public GNotNull(EditableTerminal firePanic, G<R> gValue, String msgPrefix) {
		super();
		this.firePanic = firePanic;
		this.gValue = gValue;
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
	 * Получить префикс сообщения об ошибке.
	 * <p>
	 * @return префикс сообщения
	 */
	public String getMessagePrefix() {
		return msgPrefix;
	}
	
	/**
	 * Получить геттер исходного значения.
	 * <p>
	 * @return геттер
	 */
	public G<R> getValueGetter() {
		return gValue;
	}

	@Override
	public R get(Object source) throws ValueException {
		R value = gValue.get(source);
		if ( value == null ) {
			String msg = msgPrefix + "NULL values not allowed for: {}";
			firePanic.firePanicEvent(1, msg, new Object[] { gValue });
		}
		return value;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == GNotNull.class ) {
			GNotNull<?> o = (GNotNull<?>) other;
			return new EqualsBuilder()
				.append(firePanic, o.firePanic)
				.append(gValue, o.gValue)
				.append(msgPrefix, o.msgPrefix)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[value=" + gValue
			+ ", msgPfx='" + msgPrefix + "']";
	}

}
