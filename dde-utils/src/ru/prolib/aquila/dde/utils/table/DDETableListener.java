package ru.prolib.aquila.dde.utils.table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.dde.DDEException;
import ru.prolib.aquila.dde.DDETable;
import ru.prolib.aquila.dde.utils.DDETableEvent;

/**
 * Наблюдатель DDE-таблицы.
 * <p>
 * 2012-08-12<br>
 * $Id: DDETableListener.java 304 2012-11-06 09:17:07Z whirlwind $
 */
public class DDETableListener implements EventListener {
	private static final Logger logger;
	private final String topic;
	private final DDETableHandler handler;
	
	static {
		logger = LoggerFactory.getLogger(DDETableListener.class);
	}
	
	/**
	 * Создать наблюдателя.
	 * <p>
	 * @param topic тема DDE-таблицы
	 * @param handler обработчик таблицы
	 */
	public DDETableListener(String topic, DDETableHandler handler) {
		super();
		if ( topic == null ) {
			throw new NullPointerException("Table topic cannot be null");
		}
		if ( handler == null ) {
			throw new NullPointerException("Table handler cannot be null");
		}
		this.topic = topic;
		this.handler = handler;
	}
	
	/**
	 * Получить обработчик таблицы.
	 * <p>
	 * @return обработчик таблицы
	 */
	public DDETableHandler getTableHandler() {
		return handler;
	}
	
	/**
	 * Получить тему соответствующую таблице.
	 * <p>
	 * @return тема
	 */
	public String getTopic() {
		return topic;
	}

	@Override
	public void onEvent(Event event) {
		if ( event != null && event.getClass() == DDETableEvent.class ) {
			DDETable table = ((DDETableEvent) event).getTable();
			if ( table.getTopic().equals(topic) ) {
				try {
					handler.handle(table);
				} catch ( DDEException e ) {
					logger.error("Unexpected exception for " + topic, e);
				}
			}
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof DDETableListener ) {
			DDETableListener o = (DDETableListener) other;
			return new EqualsBuilder()
				.append(topic, o.topic)
				.append(handler, o.handler)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121107, /*0*/51819)
			.append(topic)
			.append(handler)
			.toHashCode();
	}

}
