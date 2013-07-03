package ru.prolib.aquila.core.data.filler;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.StarterStub;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.data.CandleSeries;

/**
 * Генератор серии свечей.
 */
public class CandleSeriesFiller implements Starter {
	private final CandleSeries series;
	private final Starter updater;
	private final Starter flusher;
	
	/**
	 * Базовый конструктор.
	 * <p>
	 * @param series последовательность
	 * @param updater субсервис обновления свечей
	 * @param flusher субсервис закрытия свечи (по времени)
	 */
	public CandleSeriesFiller(CandleSeries series,
			Starter updater, Starter flusher)
	{
		super();
		this.series = series;
		this.updater = updater;
		this.flusher = flusher;
	}
	
	/**
	 * Создать генератор свечей по сделкам инструмента.
	 * <p>
	 * @param security инструмент-источник сделок
	 * @param periodMinutes таймфрейм свечи в минутах
	 * @param candleAutoFlush если true, то свечи будут закрываться по
	 * границе свечи (по локальному времени). Иначе закрываются только при
	 * поступлении сделки из следующего периода.
	 */
	public CandleSeriesFiller(Security security, int periodMinutes,
			boolean candleAutoFlush)
	{
		this(security, new CandleAggregator(periodMinutes), true);
	}
	
	/**
	 * Создать генератор свечей по сделкам инструмента.
	 * <p>
	 * @param security инструмент-источник сделок
	 * @param aggregator агрегатор свечей
	 * @param candleAutoFlush если true, то свечи будут закрываться по
	 * границе свечи (по локальному времени). Иначе закрываются только при
	 * поступлении сделки из следующего периода.
	 */
	public CandleSeriesFiller(Security security, CandleAggregator aggregator,
			boolean candleAutoFlush)
	{
		this(aggregator.getCandles(),
			new CandleByTrades(security, aggregator),
			candleAutoFlush ?
				new CandleFlusher(aggregator, security.getTerminal()) :
				new StarterStub());
	}
	
	Starter getUpdater() {
		return updater;
	}
	
	Starter getFlusher() {
		return flusher;
	}
	
	public CandleSeries getCandles() {
		return series;
	}

	@Override
	public void start() throws StarterException {
		flusher.start();
		updater.start();
	}

	@Override
	public void stop() throws StarterException {
		updater.stop();
		flusher.stop();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != CandleSeriesFiller.class ) {
			return false;
		}
		CandleSeriesFiller o = (CandleSeriesFiller) other;
		return new EqualsBuilder()
			.append(o.flusher, flusher)
			.append(o.series, series)
			.append(o.updater, updater)
			.isEquals();
	}

}
