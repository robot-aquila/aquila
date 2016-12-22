package ru.prolib.aquila.core.data;

/**
 * True Range (TR).
 * <p>
 * 2013-03-12<br>
 * $Id: TR.java 571 2013-03-12 00:53:34Z whirlwind $
 */
public class TrueRange implements Series<Double> {
	private static final TAMath indicators = new TAMath();
	
	private final Series<Candle> candles;
	private String id;

	public TrueRange(Series<Candle> candles) {
		this(Series.DEFAULT_ID, candles);
	}
	
	public TrueRange(String id, Series<Candle> candles) {
		super();
		this.id = id;
		this.candles = candles;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Double get() throws ValueException {
		return indicators.tr(candles);
	}

	@Override
	public Double get(int index) throws ValueException {
		return indicators.tr(candles, index);
	}

	@Override
	public int getLength() {
		return candles.getLength();
	}

}
