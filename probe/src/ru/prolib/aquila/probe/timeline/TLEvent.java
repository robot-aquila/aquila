package ru.prolib.aquila.probe.timeline;

import org.joda.time.DateTime;

/**
 * Базовое событие хронологии.
 */
public class TLEvent implements Runnable {
	private final DateTime time;
	private final Runnable procedure; 

	/**
	 * Конструктор.
	 * <p>
	 * @param time время наступления события
	 * @param procedure процедура исполнения события
	 */
	public TLEvent(DateTime time, Runnable procedure) {
		super();
		this.time = time;
		this.procedure = procedure;
	}
	
	/**
	 * Получить время наступления события.
	 * <p>
	 * @return время события
	 */
	public DateTime getTime() {
		return time;
	}
	
	/**
	 * Получить процедуру исполнения события.
	 * <p>
	 * @return процедура
	 */
	public Runnable getProcedurte() {
		return procedure;
	}

	/**
	 * Выполнить процедуру исполнения события.
	 */
	@Override
	public void run() {
		procedure.run();
	}

}
