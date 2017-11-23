package ru.prolib.aquila.core.data;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.concurrency.LID;

/**
 * True Range (TR).
 * <p>
 * 2013-03-12<br>
 * $Id: TR.java 571 2013-03-12 00:53:34Z whirlwind $
 */
public class TrueRange implements Series<CDecimal> {
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
	public CDecimal get() throws ValueException {
		return indicators.tr(candles);
	}

	@Override
	public CDecimal get(int index) throws ValueException {
		return indicators.tr(candles, index);
	}

	@Override
	public int getLength() {
		return candles.getLength();
	}

	@Override
	public LID getLID() {
		return candles.getLID();
	}

	@Override
	public void lock() {
		candles.lock();
	}

	@Override
	public void unlock() {
		candles.unlock();
	}

}
