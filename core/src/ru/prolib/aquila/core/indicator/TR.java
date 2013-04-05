package ru.prolib.aquila.core.indicator;

import ru.prolib.aquila.core.data.*;

/**
 * True Range (TR).
 * <p>
 * 2013-03-12<br>
 * $Id: TR.java 571 2013-03-12 00:53:34Z whirlwind $
 */
public class TR extends _FLY<Candle> {
	private static final FMath math = new FMathImpl(); 

	public TR(CandleSeries source) {
		this(Series.DEFAULT_ID, source);
	}
	
	public TR(String id, CandleSeries source) {
		super(id, source);
	}

	@Override
	protected String makeId(String id) {
		return id == null ? "TR" : id;
	}

	@Override
	protected Double calculate(int index) throws ValueException {
		return math.tr(source, index);
	}

}
