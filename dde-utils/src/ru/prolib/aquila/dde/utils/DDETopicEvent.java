package ru.prolib.aquila.dde.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.EventType;

/**
 * Cобытие DDE-сервиса в связи с темой.
 * <p>
 * События данного типа характеризуются именем DDE-сервиса и темой. Оба
 * атрибута являются строковыми значениями.
 * <p>
 * 2012-07-27<br>
 * $id$
 */
public class DDETopicEvent extends DDEEvent {
	private final String topic;

	/**
	 * Создать экземпляр события.
	 * <p>
	 * @param type тип события
	 * @param service имя DDE-сервиса
	 * @param topic тема события
	 */
	public DDETopicEvent(EventType type, String service, String topic) {
		super(type, service);
		if ( topic == null ) {
			throw new NullPointerException("Topic cannot be null");
		}
		this.topic = topic;
	}

	/**
	 * Получить тему события.
	 * <p>
	 * @return тема события
	 */
	public String getTopic() {
		return topic;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof DDETopicEvent ) {
			DDETopicEvent o = (DDETopicEvent)other;
			return new EqualsBuilder()
				.append(getType(), o.getType())
				.append(getService(), o.getService())
				.append(getTopic(), o.getTopic())
				.isEquals();			
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121107, 90901)
			.append(getType())
			.append(getService())
			.append(topic)
			.toHashCode();
	}

}
