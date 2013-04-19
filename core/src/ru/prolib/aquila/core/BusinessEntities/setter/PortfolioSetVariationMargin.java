package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер вариационной маржи портфеля.
 * <p>
 * 2012-09-10<br>
 * $Id$
 */
public class PortfolioSetVariationMargin implements S<EditablePortfolio> {

	/**
	 * Установить вариационную маржу.
	 * <p>
	 * Допустимый тип значений {@link java.lang.Double}.
	 * Значения иных типов игнорируются.
	 */
	@Override
	public void set(EditablePortfolio portfolio, Object value) throws ValueException {
		if ( value != null ) {
			Class<?> valueClass = value.getClass(); 
			if ( valueClass == Double.class ) {
				portfolio.setVariationMargin((Double) value);
			}
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null
			&& other.getClass() == PortfolioSetVariationMargin.class;
	}

}
