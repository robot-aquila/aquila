package ru.prolib.aquila.core.data;

import java.time.Instant;

public class CandleBuilder {
	private TimeFrame timeFrame;
	private Instant time;
	private Double open, high, low, close;
	private Long volume;
	
	public CandleBuilder withTimeFrame(TimeFrame timeFrame) {
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
	
	public CandleBuilder withOpenPrice(Double value) {
		this.open = value;
		return this;
	}
	
	public CandleBuilder withHighPrice(Double value) {
		this.high = value;
		return this;
	}
	
	public CandleBuilder withLowPrice(Double value) {
		this.low = value;
		return this;
	}
	
	public CandleBuilder withClosePrice(Double value) {
		this.close = value;
		return this;
	}
	
	public CandleBuilder withVolume(Long value) {
		this.volume = value;
		return this;
	}
	
	public Candle buildCandle() {
		return new Candle(timeFrame.getInterval(time), open, high, low, close, volume);
	}

}
