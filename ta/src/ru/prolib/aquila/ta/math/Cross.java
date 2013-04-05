package ru.prolib.aquila.ta.math;

import ru.prolib.aquila.ta.Value;
import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.ValueImpl;
import ru.prolib.aquila.ta.ValueUpdateException;

/**
 * Определение пересечение кривых, образованных значениями двух источников.
 * Фактически отслеживает момент, когда изменяется знак разности текущих
 * значений двух источников. Разница рассчитывается вычитанием значения второго
 * источника из значения первого источника. Когда разница меняется с
 * отрицательной на положительную, то сигнализируется о пересечении снизу-вверх
 * ({@link #ABOVE} - первая кривая пересекла вторую кривую снизу-вверх). 
 * Изменение разницы с положительной на отрицательную обозначается как
 * {@link #BELOW} - первая кривая пересекла вторую кривую сверху-вниз. 
 * Если разница между значениями осталась со знаком, определенным на предыдущем
 * этапе, то объект остается в несигнальном состоянии {@link #NONE}. 
 */
@Deprecated
public class Cross extends ValueImpl<Integer> {
	private final ru.prolib.aquila.ta.indicator.Cross cross;
	
	public Cross(Value<Double> src1, Value<Double> src2) {
		this(src1, src2, ValueImpl.DEFAULT_ID);
	}
	
	public Cross(Value<Double> src1, Value<Double> src2, String id) {
		super(id);
		cross = new ru.prolib.aquila.ta.indicator.Cross(src1, src2);
	}
	
	public Value<Double> getSource1() {
		return cross.getFirstSource();
	}
	
	public Value<Double> getSource2() {
		return cross.getSecondSource();
	}

	@Override
	public synchronized void update() throws ValueUpdateException {
		try {
			add(cross.calculate());
		} catch ( ValueException e ) {
			throw new ValueUpdateException(e);
		}
	}

}
