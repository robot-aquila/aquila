package ru.prolib.aquila.probe.timeline;

import java.util.List;

/**
 * Шаг симуляции.
 * <p>
 */
public class TLSimulationStrategy {
	private final TLInterrogationStrategy helper;
	private final TLEventQueue eventQueue;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param helper процедуры нижнего уровня
	 * @param eventQueue очередь событий хронологии
	 */
	public TLSimulationStrategy(TLInterrogationStrategy helper,
			TLEventQueue eventQueue)
	{
		super();
		this.helper = helper;
		this.eventQueue = eventQueue;
	}
	
	/**
	 * Выполнить симуляцию шага.
	 * <p>
	 * @return true - продолжать симуляцию, false - симуляция завершена
	 */
	public boolean execute() {
		List<TLEventSource> list;
		while ( (list = helper.getForInterrogating()).size() > 0 ) {
			for ( TLEventSource src : list ) {
				helper.interrogate(src);
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
