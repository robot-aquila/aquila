package ru.prolib.aquila.probe.timeline;

import java.util.*;

/**
 * Реализация простого источника событий хронологии на основе очереди.
 */
public class TLSimpleEventSource implements TLEventSource {
	private final String id;
	private final Deque<TLEvent> queue;

	/**
	 * Конструктор.
	 * <p>
	 * @param id идентификатор объекта 
	 * @param events список событий, которые будут скопированы в создаваемый
	 * объект
	 */
	public TLSimpleEventSource(String id, List<TLEvent> events) {
		super();
		this.id = id;
		this.queue = new ArrayDeque<TLEvent>(events);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Создает объект с указанным идентификаторов и пустой очередью событий.
	 * <p>
	 * @param id идентификатор объекта
	 */
	public TLSimpleEventSource(String id) {
		this(id, new Vector<TLEvent>());
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * @param events список событий, которые будут скопированы в создаваемый
	 * объект
	 */
	public TLSimpleEventSource(List<TLEvent> events) {
		this(null, events);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Создает объект с пустой очередью событий.
	 */
	public TLSimpleEventSource() {
		this((String) null);
	}

	/**
	 * Добавить событие в конец очереди.
	 * <p>
	 * @param event событие для добавления
	 * @return источник событий (этот объект)
	 */
	public TLSimpleEventSource add(TLEvent event) {
		queue.add(event);
		return this;
	}

	@Override
	public TLEvent pullEvent() throws TLException {
		return queue.poll();
	}

	@Override
	public void close() {
		queue.clear();
	}

	@Override
	public boolean closed() {
		return queue.size() == 0;
	}
	
	@Override
	public String toString() {
		return id == null ? super.toString() : id;
	}

}
