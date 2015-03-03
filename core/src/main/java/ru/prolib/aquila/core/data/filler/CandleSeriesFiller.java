package ru.prolib.aquila.core.data.filler;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.*;

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
	public CandleSeriesFiller(CandleSeries series, Starter updater,
			Starter flusher)
	{
		super();
		this.series = series;
		this.updater = updater;
		this.flusher = flusher;
	}
	
	/**
	 * Создать генератор свечей по сделкам инструмента.
	 * <p>
	 * @param es фасад системы событий
	 * @param security инструмент-источник сделок
	 * @param timeframe таймфрейм
	 * @param candleAutoFlush если true, то свечи будут закрываться по
	 * границе свечи (по локальному времени). Иначе закрываются только при
	 * поступлении сделки из следующего периода.
	 */
	public CandleSeriesFiller(EventSystem es, Security security,
			Timeframe timeframe, boolean candleAutoFlush)
	{
		this(security, new CandleSeriesImpl(es, timeframe), candleAutoFlush);
	}
	
	/**
	 * Создать генератор свечей по сделкам инструмента.
	 * <p>
	 * @param security инструмент-источник сделок
	 * @param candles последовательность свечей
	 * @param candleAutoFlush если true, то свечи будут закрываться по
	 * границе свечи (по локальному времени). Иначе закрываются только при
	 * поступлении сделки из следующего периода.
	 */
	public CandleSeriesFiller(Security security, EditableCandleSeries candles,
			boolean candleAutoFlush)
	{
		this(candles,
			new CandleByTrades(security, candles), candleAutoFlush ?
				new CandleFlusher(candles, security.getTerminal()) :
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
