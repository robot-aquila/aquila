package ru.prolib.aquila.quik.subsys.row;

import java.util.Hashtable;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.*;

/**
 * Хранилище рядов таблицы заявок.
 */
public class OrderTable {
	private final EventDispatcher dispatcher;
	private final EventType onChanged;
	private final Map<Long, OrderTableRow> rows;
	
	public OrderTable(EventDispatcher dispatcher, EventType onChanged) {
		super();
		this.dispatcher = dispatcher;
		this.onChanged = onChanged;
		rows = new Hashtable<Long, OrderTableRow>();
	}
	
	/**
	 * Получить ряд таблицы, соответствующий заявке.
	 * <p>
	 * @param orderId идентификатор заявки
	 * @return экземпляр ряда или null, если нет ряда для указанной заявки
	 */
	public synchronized OrderTableRow getRow(long orderId) {
		return rows.get(orderId);
	}
	
	/**
	 * Установить ряд таблицы.
	 * <p>   
	 * @param row экземпляр ряда
	 */
	public synchronized void setRow(OrderTableRow row) {
		rows.put(row.getId(), row);
	}
	
	/**
	 * Удалить все строки.
	 */
	public synchronized void clear() {
		rows.clear();
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == OrderTable.class ) {
			OrderTable o = (OrderTable) other;
			return new EqualsBuilder()
				.append(rows, o.rows)
				.append(onChanged, o.onChanged)
				.append(dispatcher, o.dispatcher)
				.isEquals();
		} else {
			return false;
		}
	}
	
	/**
	 * Получить диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Получить тип события: при изменении таблицы.
	 * <p>
	 * @return тип события
	 */
	public EventType OnChanged() {
		return onChanged;
	}
	
	/**
	 * Генерировать событие об изменении таблицы.
	 */
	public void fireChangedEvent() {
		dispatcher.dispatch(new EventImpl(onChanged));
	}

}
