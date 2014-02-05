package ru.prolib.aquila.core.data.getter;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.G;

/**
 * Конвертер вещественного значения в целое.
 * <p>
 * Конвертирует значения типа {@link java.lang.Double Double}, полученное
 * посредством геттера, в значение типа {@link java.lang.Integer Integer} с
 * потерей дробной части. 
 * <p>
 * 2013-02-25<br>
 * $Id: GDouble2Int.java 543 2013-02-25 06:35:27Z whirlwind $
 */
@Deprecated
public class GDouble2Int extends GDouble2X<Integer> {

	/**
	 * Конструктор.
	 * <p>
	 * @param firePanic генератор события о паническом состоянии
	 * @param gDouble геттер вещественного значения
	 * @param strict характер поведения: true - строгий, false - нестрогий
	 * @param msgPrefix префикс сообщения о паническом состоянии
	 */
	public GDouble2Int(EditableTerminal firePanic, G<Double> gDouble,
			boolean strict, String msgPrefix) {
		super(firePanic, gDouble, strict, msgPrefix);
	}

	@Override
	protected Integer adapt(Double value) {
		return value.intValue();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == GDouble2Int.class ) {
			return fieldsEquals(other);
		} else {
			return false;
		}
	}

}
