package ru.prolib.aquila.ta.SignalSource.PatternMatcher;

import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.ValueException;

/**
 * Awesome Oscillator.
 * Матчер на продажу пересечения нулевой линии сверху.
 */
public class AO_SellZeroCross extends PatternMatcher1Value {

	public AO_SellZeroCross(Value<Double> src) {
		super(src);
	}

	@Override
	public boolean matches() throws ValueException {
		if ( src.getLength() >= 3 ) {
			double a = src.get(-2),
				   b = src.get(-1),
				   c = src.get();
			if ( a > 0 && b > 0 && c < 0 && a > b ) {
				return true;
			}
		}
		return false;
	}

}
