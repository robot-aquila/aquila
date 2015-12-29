package ru.prolib.aquila.probe.timeline;

import java.time.LocalDateTime;

import org.threeten.extra.Interval;

/**
 * Значение за границами интервала.
 */
public class TLOutOfIntervalException extends TLException {
	private static final long serialVersionUID = -6061433378432810110L;
	
	public TLOutOfIntervalException() {
		super();
	}
	
	/**
	 * Этот конструктор формирует сообщение на основании указанного временного
	 * интервала и момента времени, который находится за пределами интервала.
	 * <p>
	 * @param interval интервал
	 * @param time момент времени
	 */
	public TLOutOfIntervalException(Interval interval, LocalDateTime time) {
		super("Interval: " + interval + " time: " + time);
	}
	
	/**
	 * Этот конструктор формирует сообщение на основании указанного временного
	 * интервала и некоего объекта, имеющего связь с моментом во времени, при
	 * том, что это время лежит за пределами интервала. Для получения
	 * информативного сообщения, объект должен приводиться к строке, в которой
	 * содержится информация о времени, с которым он связан.
	 * <p>
	 * @param interval интервал
	 * @param subject субъект
	 */
	public TLOutOfIntervalException(Interval interval, Object subject) {
		super("Interval: " + interval + " subject: " + subject);
	}

}
