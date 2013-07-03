package ru.prolib.aquila.core.data;


/**
 * Реализация ряда свечей.
 * <p>
 * 2013-03-11<br>
 * $Id: CandleSeriesImpl.java 566 2013-03-11 01:52:40Z whirlwind $
 */
public class CandleSeriesImpl extends SeriesImpl<Candle>
		implements EditableCandleSeries
{
	private final DataSeries open, high, low, close, vol;
	private final TimeSeries time;
	
	public CandleSeriesImpl() {
		this(Series.DEFAULT_ID);
	}
	
	public CandleSeriesImpl(String valueId) {
		this(valueId, SeriesImpl.STORAGE_NOT_LIMITED);
	}
	
	public CandleSeriesImpl(String id, int storageLimit) {
		super(id, storageLimit);
		open =  new CandleDataSeries(id + ".open", this, new GCandleOpen());
		close = new CandleDataSeries(id + ".close", this, new GCandleClose());
		high =  new CandleDataSeries(id + ".high", this, new GCandleHigh());
		low =   new CandleDataSeries(id + ".low", this, new GCandleLow());
		vol =   new CandleDataSeries(id + ".volume", this, new GCandleVolume());
		time =  new CandleTimeSeries(id + ".time", this);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass() == CandleSeriesImpl.class
			? fieldsEquals(other) : false;
	}

	@Override
	public DataSeries getOpen() {
		return open;
	}

	@Override
	public DataSeries getHigh() {
		return high;
	}

	@Override
	public DataSeries getLow() {
		return low;
	}

	@Override
	public DataSeries getClose() {
		return close;
	}

	@Override
	public DataSeries getVolume() {
		return vol;
	}

	@Override
	public TimeSeries getTime() {
		return time;
	}

}
