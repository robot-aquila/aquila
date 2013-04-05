package ru.prolib.aquila.ta.SignalSource.PatternMatcher;

import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.ValueException;

/**
 * Acceleration/Deceleration Oscillator.
 * Матчер покупки ниже нулевой линии.
 */
public class AC_BuyBelowZero extends PatternMatcher1Value {

	/**
	 * Конструктор.
	 * @param src - значение осциллятора AC.
	 */
	public AC_BuyBelowZero(Value<Double> src) {
		super(src);
	}

	@Override
	public boolean matches() throws ValueException {
		if ( src.getLength() >= 5 ) {
			Double a = src.get(-4),
				   b = src.get(-3),
				   c = src.get(-2),
				   d = src.get(-1),
				   e = src.get();
			if ( a != null && b != null && c != null & d != null && e != null
				&& a < 0 && b < 0 && c < 0 && d < 0 && e < 0
				&& b < a & c > b && d > c && e > d ) {
				return true;
			}
		}
		return false;
	}

}
