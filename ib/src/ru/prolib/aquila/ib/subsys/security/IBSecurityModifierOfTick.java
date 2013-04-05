package ru.prolib.aquila.ib.subsys.security;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.ib.client.TickType;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.ib.event.IBEventTick;

/**
 * Модификатор инструмента на основании события типа {@link IBEventTick}.
 * <p>
 * 2012-12-23<br>
 * $Id: IBSecurityModifierOfTick.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBSecurityModifierOfTick implements S<EditableSecurity> {
	
	public IBSecurityModifierOfTick() {
		super();
	}

	/**
	 * Установить значение атрибута инструмента.
	 * <p>
	 * Обрабатываются только следующие типы тиковых данных: TickType.ASK,
	 * TickType.ASK_SIZE, TickType.BID, TickType.BID_SIZE
	 * <p>
	 * @param object экземпляр инструмента
	 * @param value экземпляр события типа {@link IBEventTick}
	 */
	@Override
	public void set(EditableSecurity object, Object value) {
		if ( value instanceof IBEventTick ) {
			IBEventTick event = (IBEventTick) value;
			if ( event.getValue() <= 0 ) {
				return;
			}
			switch (event.getTickType() ) {
			case TickType.ASK:
				object.setAskPrice(event.getValue());
				break;
			case TickType.ASK_SIZE:
				object.setAskSize((long) event.getValue());
				break;
			case TickType.BID:
				object.setBidPrice(event.getValue());
				break;
			case TickType.BID_SIZE:
				object.setBidSize((long) event.getValue());
				break;
			case TickType.LAST:
				object.setLastPrice(event.getValue());
				break;
			case TickType.OPEN:
				object.setOpenPrice(event.getValue());
				break;
			case TickType.HIGH:
				object.setHighPrice(event.getValue());
				break;
			case TickType.LOW:
				object.setLowPrice(event.getValue());
				break;
			case TickType.CLOSE:
				object.setClosePrice(event.getValue());
				break;
			}
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121223, 95157).toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null
			&& other.getClass() == IBSecurityModifierOfTick.class;
	}

}
