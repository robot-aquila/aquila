package ru.prolib.aquila.ta.indicator;

import ru.prolib.aquila.ta.*;

/**
 * Сдвиг значений.
 * Сдвигает последовательность вправо на N количество баров. Так как значения
 * для первых N элементов взять негде, то в качестве этих значений
 * устанавливается null.
 */
public class ShiftRight<T> extends BaseIndicator1SP<T> {
	
	/**
	 * Конструктор.
	 * @param src источник значений
	 * @param period период
	 */
	public ShiftRight(Value<T> src, int period) {
		super(src, period);
	}

	@Override
	public synchronized T calculate() throws ValueException {
		int total = src.getLength();
		if ( total <= period ) {
			return null;
		} else {
			return src.get(total - period - 1);
		}
	}

}
