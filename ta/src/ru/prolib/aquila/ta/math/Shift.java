package ru.prolib.aquila.ta.math;

import ru.prolib.aquila.ta.*;
import ru.prolib.aquila.ta.indicator.ShiftRight;

/**
 * Сдвиг значений.
 * Сдвигает последовательность вправо на N количество баров. Так как значения
 * для первых N элементов взять негде, то в качестве этих значений
 * устанавливается null.
 */
@Deprecated
public class Shift<T> extends ValueImpl<T> {
	private final ShiftRight<T> shift;
	
	public Shift(Value<T> source, int periods) {
		this(source, periods, ValueImpl.DEFAULT_ID);
	}
	
	public Shift(Value<T> source, int periods, String id) {
		super(id);
		shift = new ShiftRight<T>(source, periods);
	}
	
	public Value<T> getSourceValue() {
		return shift.getSource();
	}
	
	public int getPeriods() {
		return shift.getPeriod();
	}

	@Override
	public synchronized void update() throws ValueUpdateException {
		try {
			add(shift.calculate());
		} catch ( ValueException e ) {
			throw new ValueUpdateException(e);
		}
	}

}
