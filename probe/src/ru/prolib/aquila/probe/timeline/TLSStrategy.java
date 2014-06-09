package ru.prolib.aquila.probe.timeline;

import java.util.List;

/**
 * Шага эмуляции терминала.
 * <p>
 * Данный класс реализует механизм отработки шага симуляции.
 */
public class TLSStrategy {
	private final TLSIntrgStrategy strategy;
	private final TLEventQueue eventQueue;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param strategy стратегия опроса источников событий
	 * @param eventQueue очередь событий хронологии
	 */
	public TLSStrategy(TLSIntrgStrategy strategy,
			TLEventQueue eventQueue)
	{
		super();
		this.strategy = strategy;
		this.eventQueue = eventQueue;
	}
	
	/**
	 * Выполнить симуляцию шага.
	 * <p>
	 * @return true - продолжать симуляцию, false - симуляция завершена
	 */
	public boolean execute() {
		List<TLEventSource> list;
		while ( (list = strategy.getForInterrogating()).size() > 0 ) {
			for ( TLEventSource src : list ) {
				strategy.interrogate(src);
			}
		}
		TLEventStack stack = eventQueue.pullStack();
		if ( stack == null ) {
			return false;
		}
		stack.execute();
		return true;
	}

}
