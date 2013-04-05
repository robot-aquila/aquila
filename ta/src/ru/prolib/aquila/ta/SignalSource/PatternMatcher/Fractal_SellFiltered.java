package ru.prolib.aquila.ta.SignalSource.PatternMatcher;

import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.ValueException;

/**
 * Матчер фракталов вниз с фильтром по значению.
 * Помимо самого фрактала, матчер так же проверяет условие что минимум
 * на дне фрактала ниже, чем фильтр на том же самом баре.
 */
public class Fractal_SellFiltered extends PatternMatcher2Value {
	private final int periods;
	private final int center;

	public Fractal_SellFiltered(Value<Double> low, Value<Double> filter) {
		super(low, filter);
		this.periods = 5;
		center = periods / 2 + 1;
	}

	@Override
	public boolean matches() throws ValueException {
		int total = src1.getLength();
		if ( total >= periods ) {
			int bar = total - center;
			Double bottom = src1.get(bar);
			Double filter = src2.get(bar);
			// дно должно быть ниже фильтра
			if ( filter != null && bottom < src2.get(bar) ) {
				for ( int i = periods; i > 0; i -- ) {
					 // дно пропускаем
					if ( i != center ) {
						// если хотя бы одно из соседних ниже или
						// равно пику, значит фрактала нет 
						if ( src1.get(total - i) <= bottom ) {
							return false;
						}
					}
				}
				return true;
			}
		}
		return false;
	}

}
