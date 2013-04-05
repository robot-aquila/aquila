package ru.prolib.aquila.ta.SignalSource.PatternMatcher;

import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.ValueException;

/**
 * Acceleration/Deceleration Oscillator.
 * Матчер покупки выше нулевой линии.
 */
public class AC_BuyAboveZero extends PatternMatcher1Value {

	/**
	 * Конструктор.
	 * @param src - значение осциллятора AC.
	 */
	public AC_BuyAboveZero(Value<Double> src) {
		super(src);
	}

	@Override
	public boolean matches() throws ValueException {
		if ( src.getLength() >= 4 ) {
			Double a = src.get(-3),
				   b = src.get(-2),
				   c = src.get(-1),
				   d = src.get();
			if ( a != null && b != null & c != null & d != null
				&& a > 0 && b > 0 && c > 0 && d > 0 
				&& b < a && c > b && d > c )
			{
				return true;
			}
		}
		return false;
	}

}
