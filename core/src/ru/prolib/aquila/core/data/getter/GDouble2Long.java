package ru.prolib.aquila.core.data.getter;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.G;

/**
 * Конвертер вещественного значения в целое.
 * <p>
 * Конвертирует значения типа {@link java.lang.Double Double}, полученное
 * посредством геттера, в значение типа {@link java.lang.Long Long} с потерей
 * дробной части. 
 * <p>
 * 2013-02-14<br>
 * $Id$
 */
@Deprecated
public class GDouble2Long extends GDouble2X<Long> {
	
	/**
	 * Конструктор.
	 * <p>
	 * @param firePanic генератор события о паническом состоянии
	 * @param gDouble геттер вещественного значения
	 * @param strict характер поведения: true - строгий, false - нестрогий
	 * @param msgPrefix префикс сообщения о паническом состоянии
	 */
	public GDouble2Long(EditableTerminal firePanic, G<Double> gDouble,
			boolean strict, String msgPrefix)
	{
		super(firePanic, gDouble, strict, msgPrefix);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == GDouble2Long.class ) {
			return fieldsEquals(other);
		} else {
			return false;
		}
	}
	
	@Override
	protected Long adapt(Double value) {
		return value.longValue();
	}

}
