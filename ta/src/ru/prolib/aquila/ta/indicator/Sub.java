package ru.prolib.aquila.ta.indicator;

import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.ValueException;

/**
 * Расчитывает разницу двух значений.
 */
public class Sub extends BaseIndicator2S<Double,Double> {

	/**
	 * Конструктор.
	 * @param src1 уменьшаемое
	 * @param src2 вычитаемое
	 */
	public Sub(Value<Double> src1, Value<Double> src2) {
		super(src1, src2);
	}

	@Override
	public Double calculate() throws ValueException {
		Double v1 = src1.get();
		Double v2 = src2.get();
		if ( v1 == null || v2 == null ) return null;
		return v1 - v2;
	}

}
