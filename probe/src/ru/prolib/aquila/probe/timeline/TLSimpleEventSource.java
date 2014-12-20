package ru.prolib.aquila.probe.timeline;

import java.util.*;

/**
 * Реализация простого источника событий хронологии на основе очереди.
 */
public class TLSimpleEventSource implements TLEventSource {
	private final Deque<TLEvent> queue;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param events список событий, которые будут скопированы в создаваемый
	 * объект
	 */
	public TLSimpleEventSource(List<TLEvent> events) {
		super();
		this.queue = new ArrayDeque<TLEvent>(events);
	}
	
	/**
	 * Конструктор.
	 * <p>
	 * Создает объект с пустой очередью событий.
	 */
	public TLSimpleEventSource() {
		super();
		this.queue = new ArrayDeque<TLEvent>();
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

}
