package ru.prolib.aquila.core.data.filler;

import java.util.TimerTask;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.*;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;

/**
 * Служебный класс: сервис закрытия свечи по времени.
 */
class CandleFlusher implements Starter {
	private final EditableCandleSeries candles;
	private final java.util.Timer scheduler;
	private final Scheduler source;
	private TimerTask task;
	
	public CandleFlusher(EditableCandleSeries candles, Scheduler source) {
		this(candles, source, new java.util.Timer(true));
	}
	
	public CandleFlusher(EditableCandleSeries candles,
			Scheduler source, java.util.Timer scheduler)
	{
		super();
		this.candles = candles;
		this.scheduler = scheduler;
		this.source = source;
	}
	
	EditableCandleSeries getCandles() {
		return candles;
	}
	
	java.util.Timer getScheduler() {
		return scheduler;
	}
	
	Scheduler getTimeSource() {
		return source;
	}
	
	/**
	 * Установить экземпляр текущей задачи.
	 * <p>
	 * <b>Прим.</b> служебный метод для тестирования запуска/останова сервиса.
	 * <p>
	 * @param task задача
	 */
	void setTask(TimerTask task) {
		this.task = task;
	}
	
	/**
	 * Получить экземпляр текущей задачи.
	 * <p>
	 * <b>Прим.</b> служебный метод для тестирования запуска/останова сервиса.
	 * <p>
	 * @return текущая задача
	 */
	TimerTask getTask() {
		return task;
	}

	@Override
	public synchronized void start() throws StarterException {
		if ( task == null ) {
			task = new CandleFlusherTask(candles, source);
			Interval interval = candles.getTimeframe()
				.getInterval(source.getCurrentTime());
			scheduler.scheduleAtFixedRate(task, interval.getEnd().toDate(),
					interval.toDurationMillis());
		}
	}

	@Override
	public synchronized void stop() throws StarterException {
		if ( task != null ) {
			task.cancel();
			task = null;
		}
	}
	/*
	private Date getFirstTime() {
		Calendar c = Calendar.getInstance();
		c.setTime(aligner().align(source.getCurrentTime()));
		c.add(Calendar.MINUTE, aligner().getPeriod());
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	
	private long getPeriod() {
		return aligner().getPeriod() * 60000;
	}

	private AlignTime aligner() {
		return candles.getTimeAligner();
	}
	*/
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CandleFlusher.class ) {
			return false;
		}
		CandleFlusher o = (CandleFlusher) other;
		return new EqualsBuilder()
			.appendSuper(o.source == source)
			.append(o.candles, candles)
			.isEquals();
	}

}
