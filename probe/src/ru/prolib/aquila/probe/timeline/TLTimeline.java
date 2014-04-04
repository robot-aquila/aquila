package ru.prolib.aquila.probe.timeline;

import org.joda.time.DateTime;
import org.joda.time.Interval;


/** 
 * Хронология событий.
 */
public class TLTimeline {
	private final TLEventSources sources;
	private final TLEventQueue queue;
	private final TLSimulationStrategy simulation;
	
	/**
	 * Конструктор (для тестов).
	 * <p>
	 * @param sources источник событий
	 * @param queue последовательность событий
	 * @param simulation процедуры симуляции
	 */
	public TLTimeline(TLEventSources sources, TLEventQueue queue,
			TLSimulationStrategy simulation)
	{
		super();
		this.sources = sources;
		this.queue = queue;
		this.simulation = simulation;
	}
	
	/**
	 * Получить ТА.
	 * <p>
	 * @return точка актуальности
	 */
	public DateTime getPOA() {
		return queue.getPOA();
	}
	
	/**
	 * Получить рабочий период.
	 * <p>
	 * @return РП
	 */
	public Interval getInterval() {
		return queue.getInterval();
	}
	
	/**
	 * Добавить событие в последовательность.
	 * <p>
	 * @param time позиция события на временной шкале
	 * @param procedure процедура события
	 */
	public void pushEvent(DateTime time, Runnable procedure) {
		queue.pushEvent(new TLEvent(time, procedure));
	}
	
	/**
	 * Добавить событие в последовательность.
	 * <p>
	 * @param event событие
	 */
	public void pushEvent(TLEvent event) {
		queue.pushEvent(event);
	}
	
	/**
	 * Зарегистрировать источник событий.
	 * <p>
	 * @param source источник событий
	 */
	public void registerSource(TLEventSource source) {
		sources.registerSource(source);
	}
	
	/**
	 * Прекратить работу с источником событий.
	 * <p>
	 * @param source источник событий
	 */
	public void removeSource(TLEventSource source) {
		sources.removeSource(source);
	}
	
	/**
	 * Симуляция выполняется?
	 * <p>
	 * @return true - на момент вызова выполнялась симуляция событий, false -
	 * обработка приостановлена или завершена
	 */
	public boolean running() {
		// TODO: not yet implemented
		return false;
	}
	
	/**
	 * Симуляция приостановлена?
	 * <p>
	 * @return true - симуляция приостановлена, false - симуляция выполняется
	 * или завершена
	 */
	public boolean paused() {
		// TODO: not yet implemented
		return false;
	}
	
	/**
	 * Симуляция завершена?
	 * <p>
	 * @return true - симуляция завершена, false - не завершена 
	 */
	public boolean finished() {
		// TODO: not yet implemented
		return false;
	}
	
	/**
	 * Завершить работу.
	 */
	public void finish() {
		sources.close();
	}
	
	public void pause() {
		// TODO: not yet implemented
	}
	
	public void runTo(DateTime stopTime) {
		// TODO: not yet implemented
	}
	
	public void run() {
		// TODO: not yet implemented
	}
	
}
