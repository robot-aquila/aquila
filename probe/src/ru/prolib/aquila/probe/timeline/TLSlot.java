package ru.prolib.aquila.probe.timeline;

import java.util.List;
import java.util.Vector;
import org.joda.time.DateTime;

/**
 * Временной слот.
 * <p>
 * Временной слот на этапе формирования последовательности событий накапливает
 * события, соответствующие общей временной метке. После того, как формирование
 * последовательности событий завершено, эти события могут быть исполнены одним
 * вызовом.
 */
public class TLSlot implements Comparable<TLSlot> {
	private final DateTime time;
	private final List<TLEvent> events;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param event слотобразующее событие
	 */
	public TLSlot(TLEvent event) {
		super();
		events = new Vector<TLEvent>();
		events.add(event);
		time = event.getTime();
	}
	
	/**
	 * Получить последовательность событий слота.
	 * <p>
	 * @return последовательность событий
	 */
	public List<TLEvent> getEvents() {
		return new Vector<TLEvent>(events);
	}
	
	/**
	 * Получить временную метку слота.
	 * <p>
	 * @return время
	 */
	public DateTime getTime() {
		return time;
	}

	/**
	 * Добавить событие.
	 * <p>
	 * Добавляет событие в конец последовательности исполнения.
	 * <p>
	 * @param event событие
	 * @throws IllegalArgumentException событие соотв. иной временной метке
	 * @throws NullPointerException не указано событие
	 */
	public void addEvent(TLEvent event) {
		if ( event == null ) {
			throw new NullPointerException();
		}
		if ( ! time.equals(event.getTime()) ) {
			throw new IllegalArgumentException();
		}
		events.add(event);
	}
	
	/**
	 * Исполнить последовательность событий.
	 */
	public void executeEvents() {
		for ( TLEvent e : events ) {
			e.run();
		}
	}

	@Override
	public int compareTo(TLSlot other) {
		if ( other == null ) {
			return 1;
		}
		return time.compareTo(other.time);
	}

}
