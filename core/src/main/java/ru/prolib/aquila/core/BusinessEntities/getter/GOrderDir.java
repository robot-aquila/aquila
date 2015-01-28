package ru.prolib.aquila.core.BusinessEntities.getter;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;

/**
 * Конвертер направления заявки или сделки.
 * <p>
 * Конвертирует значение типа {@link java.lang.String String}, полученное
 * с помощтю геттера, в значение типа {@link
 * ru.prolib.aquila.core.BusinessEntities.Direction OrderDirection}. При
 * невозможности осуществить конвертацию генерирует событие о паническом
 * состоянии.
 * <p>
 * 2013-02-14<br>
 * $Id$
 */
@Deprecated
public class GOrderDir implements G<Direction> {
	private final EditableTerminal firePanic;
	private final G<String> gString;
	private final String msgPrefix, buyEquiv, sellEquiv;

	/**
	 * Конструктор.
	 * <p>
	 * @param firePanic генератор события о паническом состоянии
	 * @param gString геттер строкового представления направления
	 * @param buyEquiv строковый эквивалент покупки 
	 * @param sellEquiv строковый эквивалент продажи
	 * @param msgPrefix префикс сообщения о паническом состоянии
	 */
	public GOrderDir(EditableTerminal firePanic, G<String> gString,
			String buyEquiv, String sellEquiv, String msgPrefix)
	{
		super();
		this.firePanic = firePanic;
		this.gString = gString;
		this.msgPrefix = msgPrefix;
		this.buyEquiv = buyEquiv;
		this.sellEquiv = sellEquiv;
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
	 * Получить геттер значения.
	 * <p>
	 * @return геттер
	 */
	public G<String> getValueGetter() {
		return gString;
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
	 * Получить строковый эквивалент покупки.
	 * <p>
	 * @return эквивалент покупки
	 */
	public String getBuyEquiv() {
		return buyEquiv;
	}
	
	/**
	 * Получить строковый эквивалент продажи.
	 * <p>
	 * @return эквивалент продажи
	 */
	public String getSellEquiv() {
		return sellEquiv;
	}

	@Override
	public Direction get(Object source) throws ValueException {
		String value = gString.get(source);
		if ( buyEquiv.equals(value) ) {
			return Direction.BUY;
		} else if ( sellEquiv.equals(value) ) {
			return Direction.SELL;
		} else if ( value == null ) {
			String msg = msgPrefix + "NULL values not allowed for: {}";
			firePanic.firePanicEvent(1, msg, new Object[] { gString });
		} else {
			String msg = msgPrefix + "Unexpected value '{}' for: {}";
			firePanic.firePanicEvent(1, msg, new Object[] { value, this });
		}
		return null;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[value=" + gString
			+ ", buy='" + buyEquiv + "', sell='" + sellEquiv
			+ "', msgPfx='" + msgPrefix + "']";
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == GOrderDir.class ) {
			GOrderDir o = (GOrderDir) other;
			return new EqualsBuilder()
				.append(firePanic, o.firePanic)
				.append(gString, o.gString)
				.append(buyEquiv, o.buyEquiv)
				.append(sellEquiv, o.sellEquiv)
				.append(msgPrefix, o.msgPrefix)
				.isEquals();
		} else {
			return false;
		}
	}

}
