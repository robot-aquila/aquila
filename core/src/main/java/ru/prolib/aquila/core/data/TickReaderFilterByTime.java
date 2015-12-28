package ru.prolib.aquila.core.data;

import java.time.LocalDateTime;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Фильтр тиков по времени. 
 * <p>
 * Данный класс прозрачно фильтрует более ранние данные. Такая необходимость
 * может возникнуть, когда источник данных обеспечивает доступ с точностью до
 * дат при необходимой точности до миллисекунд. При запросе на перемещение
 * курсора, курсор подконтрольного итератора будет перемещаться до тех пор, пока
 * его текущий элемент не станет соответствовать указанному времени (больше или
 * равно).   
 */
public class TickReaderFilterByTime implements Aqiterator<Tick> {
	private final Aqiterator<Tick> decorated;
	private final LocalDateTime start;
	private boolean filtered = false;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param iterator подконтрольный итератор
	 * @param start время начала периода отбора данных
	 */
	public TickReaderFilterByTime(Aqiterator<Tick> iterator, LocalDateTime start) {
		super();
		this.decorated = iterator;
		this.start = start;
	}

	@Override
	public void close() {
		decorated.close();
	}

	@Override
	public Tick item() throws DataException {
		return decorated.item();
	}

	@Override
	public boolean next() throws DataException {
		if ( ! filtered ) {
			do {
				if ( ! decorated.next() ) {
					return false;
				}
			} while ( decorated.item().getTime().isBefore(start) );
			return filtered = true;
		} else {
			return decorated.next();
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TickReaderFilterByTime.class ) {
			return false;
		}
		TickReaderFilterByTime o = (TickReaderFilterByTime) other;
		return new EqualsBuilder()
			.append(decorated, o.decorated)
			.append(start, o.start)
			.isEquals();
	}

}
