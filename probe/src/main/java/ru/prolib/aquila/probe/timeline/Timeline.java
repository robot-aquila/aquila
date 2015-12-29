package ru.prolib.aquila.probe.timeline;

import java.time.LocalDateTime;

import ru.prolib.aquila.probe.internal.SimulationController;

public interface Timeline extends SimulationController,
	EventSourceRepository
{

	/**
	 * Добавить событие в последовательность.
	 * <p>
	 * @param time позиция события на временной шкале
	 * @param procedure процедура события
	 * @throws TLOutOfIntervalException событие за пределами рабочего периода
	 */
	public abstract void schedule(LocalDateTime time, Runnable procedure)
			throws TLOutOfIntervalException;

	/**
	 * Добавить событие в последовательность.
	 * <p>
	 * @param event событие хронологии
	 * @throws TLOutOfIntervalException событие за пределами рабочего периода
	 */
	public abstract void schedule(TLEvent event)
			throws TLOutOfIntervalException;

	/**
	 * Получить значение ТА.
	 * <p>
	 * @return ТА
	 */
	public abstract LocalDateTime getPOA();
	
}