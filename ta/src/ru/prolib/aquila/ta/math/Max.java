package ru.prolib.aquila.ta.math;

import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.ValueImpl;
import ru.prolib.aquila.ta.ValueUpdateException;

/**
 * Расчитывает максимальное значение источника за последние N-периодов.
 */
@Deprecated
public class Max extends ValueImpl<Double> {
	private final ru.prolib.aquila.ta.indicator.Max max;
	
	public Max(Value<Double> src, int periods) {
		this(src, periods, ValueImpl.DEFAULT_ID);
	}
	
	public Max(Value<Double> src, int periods, String id) {
		super(id);
		max = new ru.prolib.aquila.ta.indicator.Max(src, periods);
	}
	
	public Value<Double> getSourceValue() {
		return max.getSource();
	}
	
	public int getPeriods() {
		return max.getPeriod();
	}

	@Override
	public void update() throws ValueUpdateException {
		try {
			add(max.calculate());
		} catch ( ValueException e ) {
			throw new ValueUpdateException(e);
		}
	}

}
