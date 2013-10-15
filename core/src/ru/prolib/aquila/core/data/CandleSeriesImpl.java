package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import ru.prolib.aquila.core.BusinessEntities.Trade;


/**
 * Ряд свечей.
 * <p>
 * Класс позволяет формировать серию свечей, используя группировку разнотипных
 * данных по временным интервалам, соответствующим выбранному таймфрейму.  
 * В формировании свечей могут использоваться сделки, тики, другие свечи или
 * временные метки.
 * <p>
 * Точка актуальности (ТА, Point of actuality, POA) - это временная метка,
 * указывающая на самые поздние данные отражаемые в рамках последовательности.
 * Изначально ТА не определена и выставляется после поступления первой единицы
 * данных или временной метки. Точка актуальности сдвигается в будущее каждый
 * раз при получении более поздних данных. Агрегировать данные, которые
 * соответствуют более раннему времени невозможно - это приведет к возбуждению
 * соответствующего исключения. При этом, допускается агрегация данных,
 * датированных текущим значением ТА. Это связано с тем, что временные метки
 * имеют точность до миллисекунд, но реальный тайминг может выполняться с
 * точностью до микросекунд. Новая свеча создается каждый раз в момент, когда
 * ТА становится больше или равна окончанию интервала текущей свечи.
 * <p>
 * При поступлении новых данных выполняется попытка агрегировать их в рамках
 * текущей свечи. Если свеча не открыта, то на основе поступивших данных
 * формируется новая свеча. Данные агрегируются в текущую свечу до тех пор, пока
 * поступающие данные датируются временем внутри интервала текущей свечи. Как
 * только поступают данные более позднего временного интервала (или временная
 * метка такого интервала), текущая свеча фиксируется как завершенная, а ТА
 * устанавливается на временную метку поступивших данных. Если запрос связан с
 * данными, то формируется новая свеча, которая добавляется в конец
 * последовательности. 
 * <p>
 * 2013-03-11<br>
 * $Id: CandleSeriesImpl.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class CandleSeriesImpl extends SeriesImpl<Candle>
		implements EditableCandleSeries
{
	private final Timeframe timeframe;
	private final DataSeries open, high, low, close, vol;
	private final IntervalSeries interval;
	private DateTime poa;
	
	public CandleSeriesImpl(Timeframe timeframe) {
		this(timeframe, Series.DEFAULT_ID);
	}
	
	public CandleSeriesImpl(Timeframe timeframe, String valueId) {
		this(timeframe, valueId, SeriesImpl.STORAGE_NOT_LIMITED);
	}
	
	public CandleSeriesImpl(Timeframe timeframe, String id, int storageLimit) {
		super(id, storageLimit);
		this.timeframe = timeframe;
		open = new CandleDataSeries(id + ".open", this, new GCandleOpen());
		close = new CandleDataSeries(id + ".close", this, new GCandleClose());
		high = new CandleDataSeries(id + ".high", this, new GCandleHigh());
		low = new CandleDataSeries(id + ".low", this, new GCandleLow());
		vol = new CandleDataSeries(id + ".volume", this, new GCandleVolume());
		interval = new CandleIntervalSeries(id + ".interval", this);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == CandleSeriesImpl.class
			? fieldsEquals(other) : false;
	}
	
	@Override
	protected boolean fieldsEquals(Object other) {
		CandleSeriesImpl o = (CandleSeriesImpl) other;
		return new EqualsBuilder()
			.appendSuper(super.fieldsEquals(other))
			.append(o.timeframe, timeframe)
			.isEquals();
	}

	@Override
	public DataSeries getOpenSeries() {
		return open;
	}

	@Override
	public DataSeries getHighSeries() {
		return high;
	}

	@Override
	public DataSeries getLowSeries() {
		return low;
	}

	@Override
	public DataSeries getCloseSeries() {
		return close;
	}

	@Override
	public DataSeries getVolumeSeries() {
		return vol;
	}

	@Override
	public IntervalSeries getIntervalSeries() {
		return interval;
	}

	/**
	 * Добавить свечу в конец последовательности.
	 * <p>
	 * Данный метод позволяет добавить готовую свечу в конец последовательности.
	 * Интервал свечи должен соответствовать установленному таймфрейму. Начало
	 * интервала должно быть младше или равно текущей ТА последовательности.
	 * Добавление свечи сдвигает ТА на время окончания интервала свечи. Это
	 * означает запрет на последующее агрегирование данных в рамках добавленной
	 * таким образом свечи.
	 * <p>
	 * <i>В этой версии?</i> Свечи добавленные таким образом рассматриваются как
	 * исторические. Поскольку ТА устанавливается на конец свечи, любой
	 * последующий вызов приведет к формированию новой свечи без генерации
	 * события {@link SeriesImpl#OnUpdated()} потому, что текущая свеча не
	 * изменяется.
	 * <p>
	 * @param candle экземпляр добавляемой свечи
	 * @throws OutOfDateException интервал указывает на время старше ТА
	 * @throws OutOfIntervalException интервал свечи не совпадает с
	 * установленным таймфреймом последовательности
	 */
	@Override
	public synchronized void add(Candle candle) throws ValueException {
		if ( poa != null && candle.getStartTime().isBefore(poa) ) {
			throw new OutOfDateException(poa, candle);
		}
		Interval expected = timeframe.getInterval(candle.getStartTime());
		if ( ! candle.getInterval().equals(expected) ) {
			throw new OutOfIntervalException(expected, candle);
		}
		DateTime prevPOA = poa;
		try {
			poa = candle.getEndTime();
			super.add(candle);
		} catch ( ValueException e ) {
			poa = prevPOA;
			throw e;
		}
		
	}
	
	/**
	 * Установить значение текущей свечи.
	 * <p>
	 * Метод позволяет подменить значение текущей свечи. При этом ТА сдвигается
	 * на конец интервала свечи. Это значит, что если текущая свеча была
	 * сформирована путем агрегирования данных, то после установки агрегирование
	 * в текущую свечу становится недоступным.
	 * <p>
	 * @param candle новый экземпляр свечи
	 * @throws OutOfIntervalException интервал свечи-аргумента не совпадает с
	 * интервалом текущей свечи
	 * @throws ValueNotExistsException последовательность пуста
	 */
	@Override
	public synchronized void set(Candle candle) throws ValueException {
		Interval current = get().getInterval();
		if ( ! candle.getInterval().equals(current) ) {
			throw new OutOfIntervalException(current, candle);
		}
		DateTime prevPOA = poa;
		try {
			poa = candle.getEndTime();
			super.set(candle);
		} catch ( ValueException e ) {
			poa = prevPOA;
			throw e;
		}
	}

	@Override
	public synchronized void aggregate(Tick tick) throws OutOfDateException {
		aggregate(tick, false);
	}

	@Override
	public synchronized void aggregate(Trade trade) throws OutOfDateException {
		aggregate(trade, false);
	}

	@Override
	public synchronized void aggregate(Candle candle) throws ValueException {
		aggregate(candle, false);
	}

	@Override
	public synchronized void aggregate(DateTime time)
		throws OutOfDateException
	{
		aggregate(time, false);
	}

	@Override
	public synchronized DateTime getPOA() {
		return poa;
	}

	@Override
	public Timeframe getTimeframe() {
		return timeframe;
	}

	@Override
	public synchronized void aggregate(Tick tick, boolean silent)
		throws OutOfDateException
	{
		DateTime tickTime = tick.getTime();
		if ( poa != null && tickTime.isBefore(poa) ) {
			if ( silent ) {
				return;
			}
			throw new OutOfDateException(poa, tick);
		}
		Interval intr = timeframe.getInterval(tickTime);
		Candle newCandle = new Candle(intr, tick.getValue(),
				tick.getVolume() == null ? 0L : tick.getVolume().longValue());
		DateTime prevPOA = poa;
		try {
			poa = tickTime;
			if ( getLength() == 0 ) {
				super.add(newCandle);
			} else {
				Candle current = get();
				if ( current.getInterval().equals(intr) ) {
					super.set(current.addTick(tick));
				} else {
					super.add(newCandle);
				}
			}
		} catch ( ValueException e ) {
			poa = prevPOA;
			throw new RuntimeException("Unexpected exception", e);
		}
	}

	@Override
	public synchronized void aggregate(Trade trade, boolean silent)
			throws OutOfDateException
	{
		DateTime time = trade.getTime();
		if ( poa != null && time.isBefore(poa) ) {
			if ( silent ) {
				return;
			}
			throw new OutOfDateException(poa, trade);
		}
		Interval intr = timeframe.getInterval(time);
		Candle newCandle = new Candle(intr, trade.getPrice(), trade.getQty());
		DateTime prevPOA = poa;
		try {
			poa = time;
			if ( getLength() == 0 ) {
				super.add(newCandle);
			} else {
				Candle current = get();
				if ( current.getInterval().equals(intr) ) {
					super.set(current.addTrade(trade));
				} else {
					super.add(newCandle);
				}
			}
		} catch ( ValueException e ) {
			poa = prevPOA;
			throw new RuntimeException("Unexpected exception", e);
		}
	}

	@Override
	public synchronized void aggregate(Candle candle, boolean silent)
		throws ValueException
	{
		// Интервал агрегируемой свечи (далее АС) не должен быть раньше ТА
		DateTime startTime = candle.getStartTime();
		if ( poa != null && startTime.isBefore(poa) ) {
			if ( silent ) {
				return;
			}
			throw new OutOfDateException(poa, candle);
		}
		// Нужно определить целевой интервал в рамках последовательности.
		Interval intr;
		Candle current, newCandle;
		DateTime prevPOA = poa, newPOA = candle.getEndTime();
		if ( getLength() > 0 ) {
			try {
				current = get();
			} catch ( ValueException e ) {
				throw new RuntimeException("Unexpected exception", e);
			}
			if ( current.getInterval().contains(startTime) ) {
				// Если есть текущая свеча и начало АС в интервале этой свечи,
				// то будет выполнена попытка агрегировать АС внутри текущей.
				// Это значит, что доступен интервал в пределах от ТА до конца
				// текущей свечи. И АС должна принадлежать этому интервалу.
				intr = new Interval(poa, current.getEndTime());
				if ( ! intr.contains(candle.getInterval()) ) {
					// В текущей свече нет места для АС
					throw new OutOfIntervalException(intr, candle);
				}
				try {
					poa = newPOA;
					super.set(current.addCandle(candle));
				} catch ( ValueException e ) {
					poa = prevPOA;
					throw new RuntimeException("Unexpected exception", e);
				}
				
			} else {
				// Интервал АС более поздний чем интервал текущей свечи.
				// Интервал АС должен быть меньше или равным таймфрейму.
				intr = timeframe.getInterval(startTime);
				if ( ! intr.contains(candle.getInterval()) ) {
					// Интервал АС превышает таймфрейм
					throw new OutOfIntervalException(intr, candle);
				}
				// На базе АС формируется новая свеча
				newCandle = new Candle(intr, candle.getOpen(), candle.getHigh(),
						candle.getLow(), candle.getClose(), candle.getVolume());
				try {
					poa = newPOA;
					super.add(newCandle);
				} catch ( ValueException e ) {
					poa = prevPOA;
					throw new RuntimeException("Unexpected exception", e);
				}
				
			}
			
		} else {
			// Нет свечей. Целевым интервалом является расчетный интервал
			// последовательности для времени начала АС.
			intr = timeframe.getInterval(candle.getStartTime());
			if ( ! intr.contains(candle.getInterval()) ) {
				// Интервал АС больше заданного таймфрейма последовательности
				throw new OutOfIntervalException(intr, candle);
			}
			newCandle = new Candle(intr, candle.getOpen(), candle.getHigh(),
					candle.getLow(), candle.getClose(), candle.getVolume());
			try {
				poa = newPOA;
				super.add(newCandle);
			} catch ( ValueException e ) {
				poa = prevPOA;
				throw new RuntimeException("Unexpected exception", e);
			}
			
		}
	}

	@Override
	public synchronized void aggregate(DateTime time, boolean silent)
			throws OutOfDateException
	{
		if ( poa != null && time.isBefore(poa) ) {
			if ( silent ) {
				return;
			}
			throw new OutOfDateException(poa, time);
		}
		poa = time;
	}

	@Override
	public synchronized Candle findFirstIntradayCandle() {
		int count = getLength();
		if ( count == 0 ) {
			return null;
		}
		try {
			if ( count == 1 ) {
				return get();
			}
		
			LocalDate date = get().getStartTime().toLocalDate();
			for ( int i = count - 2; i >= 0; i -- ) {
				if ( get(i).getStartTime().toLocalDate().isBefore(date) ) {
					return get(i + 1);
				}
			}
			return null;
		} catch ( ValueException e ) {
			throw new RuntimeException("Unexpected exception", e);
		}
	}

}
