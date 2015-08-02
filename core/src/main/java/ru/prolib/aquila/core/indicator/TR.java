package ru.prolib.aquila.core.indicator;

import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.data.*;

/**
 * True Range (TR).
 * <p>
 * 2013-03-12<br>
 * $Id: TR.java 571 2013-03-12 00:53:34Z whirlwind $
 */
public class TR extends _FLY<Candle> {
	private static final CommonIndicators math = new CommonIndicators(); 

	public TR(EventSystem es, CandleSeries source) {
		this(es, Series.DEFAULT_ID, source);
	}
	
	public TR(EventSystem es, String id, CandleSeries source) {
		super(es, id, source);
	}

	@Override
	protected String makeId(String id) {
		return id == null ? "TR" : id;
	}

	@Override
	protected Double calculate(int index) throws ValueException {
		return math.tr(source, index);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != TR.class ) {
			return false;
		}
		return fieldsEquals((_FLY<?>) other);
	}

}
