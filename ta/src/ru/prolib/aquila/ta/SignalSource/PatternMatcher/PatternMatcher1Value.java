package ru.prolib.aquila.ta.SignalSource.PatternMatcher;

import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.SignalSource.IPatternMatcher;

/**
 * Типовой матчер на базе одного значения.
 */
abstract public class PatternMatcher1Value implements IPatternMatcher {
	protected final Value<Double> src;

	public PatternMatcher1Value(Value<Double> src) {
		super();
		this.src = src;
	}

	public Value<Double> getSourceValue() {
		return src;
	}

}