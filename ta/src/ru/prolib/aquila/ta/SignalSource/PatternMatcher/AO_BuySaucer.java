package ru.prolib.aquila.ta.SignalSource.PatternMatcher;

import ru.prolib.aquila.ta.*;

/**
 * Awesome Oscillator.
 * Матчер блюдца на покупку.
 */
public class AO_BuySaucer extends PatternMatcher1Value {
	
	public AO_BuySaucer(Value<Double> src) {
		super(src);
	}
	
	@Override
	public boolean matches() throws ValueException {
		if ( src.getLength() >= 3 ) {
			double a = src.get(-2),
				   b = src.get(-1),
				   c = src.get();
			if ( a > 0 && b > 0 && c > 0 && a > b && b < c ) {
				return true;
			}
		}
		return false;
	}

}
