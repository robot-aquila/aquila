package ru.prolib.aquila.ta.SignalSource.PatternMatcher;

import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.SignalSource.IPatternMatcher;

/**
 * Типовой матчер на базе двух значений.
 */
abstract public class PatternMatcher2Value implements IPatternMatcher {
	protected final Value<Double> src1,src2;

	public PatternMatcher2Value(Value<Double> src1, Value<Double> src2) {
		super();
		this.src1 = src1;
		this.src2 = src2;
	}

	public Value<Double> getSourceValue1() {
		return src1;
	}
	
	public Value<Double> getSourceValue2() {
		return src2;
	}
	
}
