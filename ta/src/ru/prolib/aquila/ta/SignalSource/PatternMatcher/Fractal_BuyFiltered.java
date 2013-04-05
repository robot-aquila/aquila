package ru.prolib.aquila.ta.SignalSource.PatternMatcher;

import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.ValueException;

/**
 * Матчер фракталов вверх с фильтром по значению.
 * Помимо самого фрактала, матчер так же проверяет условие что максимум
 * на пике фрактала больше чем фильтр на том же самом баре.
 */
public class Fractal_BuyFiltered extends PatternMatcher2Value {
	private final int periods;
	private final int center;

	public Fractal_BuyFiltered(Value<Double> high, Value<Double> filter) {
		super(high, filter);
		this.periods = 5;
		center = periods / 2 + 1;
	}
	
	@Override
	public boolean matches() throws ValueException {
		int total = src1.getLength();
		if ( total >= periods ) {
			int bar = total - center;
			Double peak = src1.get(bar);
			Double filter = src2.get(bar);
			// пик должен быть выше фильтра
			if ( filter != null && peak > filter ) {
				for ( int i = periods; i > 0; i -- ) {
					 // пик пропускаем
					if ( i != center ) {
						// если хотя бы одно из соседних выше или
						// равно пику, значит фрактала нет 
						if ( src1.get(total - i) >= peak ) {
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
