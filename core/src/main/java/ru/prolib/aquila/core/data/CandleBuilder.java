package ru.prolib.aquila.core.data;

import java.time.Instant;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;

public class CandleBuilder {
	private ZTFrame timeFrame;
	private Instant time;
	private CDecimal open, high, low, close, volume;
	
	public CandleBuilder withTimeFrame(ZTFrame timeFrame) {
		this.timeFrame = timeFrame;
		return this;
	}
	
	public CandleBuilder withTime(Instant time) {
		this.time = time;
		return this;
	}
	
	public CandleBuilder withTime(String time) {
		this.time = Instant.parse(time);
		return this;
	}
	
	public CandleBuilder withOpenPrice(CDecimal value) {
		this.open = value;
		return this;
	}
	
	public CandleBuilder withOpenPrice(String value) {
		return withOpenPrice(CDecimalBD.of(value));
	}
	
	public CandleBuilder withOpenPrice(long value) {
		return withOpenPrice(CDecimalBD.of(value));
	}
	
	public CandleBuilder withHighPrice(CDecimal value) {
		this.high = value;
		return this;
	}
	
	public CandleBuilder withHighPrice(String value) {
		return withHighPrice(CDecimalBD.of(value));
	}
	
	public CandleBuilder withHighPrice(long value) {
		return withHighPrice(CDecimalBD.of(value));
	}
	
	public CandleBuilder withLowPrice(CDecimal value) {
		this.low = value;
		return this;
	}
	
	public CandleBuilder withLowPrice(String value) {
		return withLowPrice(CDecimalBD.of(value));
	}
	
	public CandleBuilder withLowPrice(long value) {
		return withLowPrice(CDecimalBD.of(value));
	}
	
	public CandleBuilder withClosePrice(CDecimal value) {
		this.close = value;
		return this;
	}
	
	public CandleBuilder withClosePrice(String value) {
		return withClosePrice(CDecimalBD.of(value));
	}
	
	public CandleBuilder withClosePrice(long value) {
		return withClosePrice(CDecimalBD.of(value));
	}
	
	public CandleBuilder withVolume(CDecimal value) {
		this.volume = value;
		return this;
	}
	
	public CandleBuilder withVolume(long value) {
		return withVolume(CDecimalBD.of(value));
	}
	
	public CandleBuilder withVolume(String value) {
		return withVolume(CDecimalBD.of(value));
	}
	
	public Candle buildCandle() {
		return new Candle(timeFrame.getInterval(time), open, high, low, close, volume);
	}
	
	public Candle buildCandle(String timeString, long open, long high,
			long low, long close, long volume)
	{
		return withTime(timeString)
				.withOpenPrice(open)
				.withHighPrice(high)
				.withLowPrice(low)
				.withClosePrice(close)
				.withVolume(volume)
				.buildCandle();
	}
	
	public Candle buildCandle(String timeString, String open, String high,
			String low, String close, long volume)
	{
		return withTime(timeString)
				.withOpenPrice(open)
				.withHighPrice(high)
				.withLowPrice(low)
				.withClosePrice(close)
				.withVolume(volume)
				.buildCandle();
	}

}
