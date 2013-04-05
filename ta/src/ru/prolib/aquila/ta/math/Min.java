package ru.prolib.aquila.ta.math;

import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.ValueImpl;
import ru.prolib.aquila.ta.ValueUpdateException;

/**
 * Рассчитывает минимальное значение источника за последние N-периодов.
 */
@Deprecated
public class Min extends ValueImpl<Double> {
	private final ru.prolib.aquila.ta.indicator.Min min;
	
	public Min(Value<Double> src, int periods, String id) {
		super(id);
		min = new ru.prolib.aquila.ta.indicator.Min(src, periods);
	}
	
	public Min(Value<Double> src, int periods) {
		this(src, periods, ValueImpl.DEFAULT_ID);
	}
	
	public Value<Double> getSourceValue() {
		return min.getSource();
	}
	
	public int getPeriods() {
		return min.getPeriod();
	}

	@Override
	public void update() throws ValueUpdateException {
		try {
			add(min.calculate());
		} catch ( ValueException e ) {
			throw new ValueUpdateException(e);
		}
	}

}
