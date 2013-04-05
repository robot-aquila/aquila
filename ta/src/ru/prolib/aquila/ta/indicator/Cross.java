package ru.prolib.aquila.ta.indicator;

import ru.prolib.aquila.ta.*;

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
public class Cross extends BaseIndicator2S<Integer,Double> {
	/**
	 * Нет пересечения.
	 */
	public static final Integer NONE  =  0;
	
	/**
	 * Первая кривая пересекла вторую кривую снизу-вверх.
	 */
	public static final Integer ABOVE =  1;
	
	/**
	 * Первая кривая пересекла вторую кривую сверху-вниз.
	 */
	public static final Integer BELOW = -1;
	
	private Integer previous = NONE;
	
	/**
	 * Конструктор.
	 * 
	 * @param src1 первая кривая
	 * @param src2 вторая кривая
	 */
	public Cross(Value<Double> src1, Value<Double> src2) {
		super(src1, src2);
	}

	@Override
	public synchronized Integer calculate() throws ValueException {
		Double v1 = src1.get();
		Double v2 = src2.get();
		if ( v1 == null || v2 == null ) {
			return NONE;
		}
		int current = v1 - v2 < 0 ? BELOW : ABOVE;
		if ( previous == 0 || previous == current ) {
			previous = current;
			return NONE;
		}
		previous = current;
		return current;
	}

}
