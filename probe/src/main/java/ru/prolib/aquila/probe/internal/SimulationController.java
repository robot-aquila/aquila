package ru.prolib.aquila.probe.internal;

import java.time.LocalDateTime;

import org.threeten.extra.Interval;

import ru.prolib.aquila.core.EventType;

/**
 * Интерфейс контроллера симуляции (исполнения хронологии событий).
 */
public interface SimulationController {

	/**
	 * Получить рабочий период.
	 * <p>
	 * @return РП
	 */
	public Interval getRunInterval();

	/**
	 * В процессе выполнения?
	 * <p>
	 * @return true - в процессе, false - обработка приостановлена/завершена
	 */
	public boolean running();

	/**
	 * Симуляция приостановлена?
	 * <p>
	 * @return true - приостановлена, false - выполняется или завершена
	 */
	public boolean paused();

	/**
	 * Симуляция завершена?
	 * <p>
	 * @return true - завершена, false - симуляция продолжается или не начата
	 */
	public boolean finished();

	/**
	 * Завершить работу.
	 */
	public void finish();

	/**
	 * Приостановить работу.
	 */
	public void pause();

	/**
	 * Выполнять эмуляцию до отсечки.
	 * <p>
	 * @param cutoff время отсечки
	 */
	public void runTo(LocalDateTime cutoff);

	/**
	 * Выполнять симуляцию до конца РП.
	 * <p>
	 * Запускает процесс симуляции до достижения конца рабочего периода.
	 */
	public void run();

	/**
	 * Получить тип события: симуляция завершена.
	 * <p>
	 * @return тип события
	 */
	public EventType OnFinish();

	/**
	 * Получить тип события: симуляция приостановлена.
	 * <p>
	 * @return тип события
	 */
	public EventType OnPause();

	/**
	 * Получить тип события: симуляция продолжена.
	 * <p>
	 * @return тип события
	 */
	public EventType OnRun();

}