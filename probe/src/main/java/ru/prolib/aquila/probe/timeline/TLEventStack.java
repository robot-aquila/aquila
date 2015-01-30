package ru.prolib.aquila.probe.timeline;

import java.util.List;
import java.util.Vector;
import org.joda.time.DateTime;

/**
 * Стек событий.
 * <p>
 * Стек событий аккумулирует события, соответствующие общей временной метке.
 * Все события стека могут быть исполнены одним вызовом.
 */
public class TLEventStack implements Comparable<TLEventStack> {
	private final DateTime time;
	private final List<TLEvent> events;
	private boolean executed = false;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param event базовое событие используется для определения временной метки
	 */
	public TLEventStack(TLEvent event) {
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
	public synchronized List<TLEvent> getEvents() {
		return new Vector<TLEvent>(events);
	}
	
	/**
	 * Получить временную метку слота.
	 * <p>
	 * @return  время
	 */
	public DateTime getTime() {
		return time;
	}

	/**
	 * Поместить событие в стек.
	 * <p>
	 * Добавляет событие в конец последовательности исполнения.
	 * <p>
	 * @param event событие
	 * @throws IllegalArgumentException событие соотв. иной временной метке
	 * @throws NullPointerException не указано событие
	 * @throws IllegalStateException попытка добавить событие в исполненный стек
	 */
	public synchronized void pushEvent(TLEvent event) {
		if ( event == null ) {
			throw new NullPointerException();
		}
		if ( executed ) {
			throw new IllegalStateException();
		}
		if ( ! time.equals(event.getTime()) ) {
			throw new IllegalArgumentException();
		}
		events.add(event);
	}
	
	/**
	 * Исполнить последовательность событий.
	 * <p>
	 * Если стек уже исполнен, то ничего не происходит.
	 */
	public synchronized void execute() {
		if ( ! executed ) {
			for ( TLEvent e : events ) {
				e.execute();
			}
			executed = true;
		}
	}
	
	/**
	 * Проверить факт исполнения стека событий.
	 * <p>
	 * @return true - события стека исполнены, false - не исполнены
	 */
	public synchronized boolean executed() {
		return executed;
	}

	@Override
	public int compareTo(TLEventStack other) {
		if ( other == null ) {
			return 1;
		}
		return time.compareTo(other.time);
	}

}
