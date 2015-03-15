package ru.prolib.aquila.dde.utils;

import org.apache.commons.lang3.builder.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.dde.DDETable;

/**
 * Событие DDE поступление табличных данных.
 * <p>
 * События данного типа характеризуются строкой имени DDE-сервиса и
 * объектом-таблицы.
 * <p>
 * 2012-07-27<br>
 * $Id: DDETableEvent.java 308 2012-11-07 14:37:19Z whirlwind $
 */
public class DDETableEvent extends DDEEvent {
	private final DDETable table; 

	/**
	 * Создать экземпляр события.
	 * <p>
	 * @param type тип события
	 * @param service наименование сервиса
	 * @param table экземпляр входящей таблицы
	 */
	public DDETableEvent(EventTypeSI type, String service, DDETable table) {
		super(type, service);
		if ( table == null ) {
			throw new NullPointerException("Table cannot be null");
		}
		this.table = table;
	}
	
	/**
	 * Получить экземпляр таблицы.
	 * <p>
	 * @return входящая таблица
	 */
	public DDETable getTable() {
		return table;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof DDETableEvent ) {
			DDETableEvent o = (DDETableEvent)other;
			return new EqualsBuilder()
				.append(getType(), o.getType())
				.append(getService(), o.getService())
				.append(getTable(), o.getTable())
				.isEquals();			
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121107, 72129)
			.append(getType())
			.append(getService())
			.append(table)
			.toHashCode();
	}

}
