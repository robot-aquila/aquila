package ru.prolib.aquila.dde.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.EventType;

/**
 * Событие DDE поступление новых данных.
 * <p>
 * События данного типа характеризуются строками имени DDE-сервиса, темой,
 * субъектом и байтовым массивом данных. 
 * <p>
 * 2012-07-27<br>
 * $Id: DDEDataEvent.java 308 2012-11-07 14:37:19Z whirlwind $
 */
public class DDEDataEvent extends DDETopicEvent {
	private final String item;
	private final byte[] data;

	/**
	 * Создать экземпляр события.
	 * <p>
	 * @param type тип события
	 * @param service имя DDE-сервиса
	 * @param topic тема
	 * @param item субъект
	 * @param data массив данных
	 */
	public DDEDataEvent(EventType type, String service, String topic,
						String item, byte[] data)
	{
		super(type, service, topic);
		if ( item == null ) {
			throw new NullPointerException("Item cannot be null");
		}
		if ( data == null ) {
			throw new NullPointerException("Data cannot be null");
		}
		this.item = item;
		this.data = data;
	}
	
	/**
	 * Получить субъект.
	 * <p>
	 * @return субъект
	 */
	public String getItem() {
		return item;
	}
	
	/**
	 * Получить массив входных данных.
	 * <p>
	 * @return массив данных
	 */
	public byte[] getData() {
		return data;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof DDEDataEvent ) {
			DDEDataEvent o = (DDEDataEvent) other;
			return new EqualsBuilder()
				.append(getType(), o.getType())
				.append(getService(), o.getService())
				.append(getTopic(), o.getTopic())
				.append(item, o.item)
				.append(data, o.data)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121107, 85051)
			.append(getType())
			.append(getService())
			.append(getTopic())
			.append(item)
			.append(data)
			.toHashCode();
	}

}
