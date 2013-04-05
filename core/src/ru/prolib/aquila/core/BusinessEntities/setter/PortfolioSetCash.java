package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.data.S;

/**
 * Сеттер размера доступных денежных средств портфеля.
 * <p>
 * 2012-09-10<br>
 * $Id$
 */
public class PortfolioSetCash implements S<EditablePortfolio> {

	/**
	 * Создать сеттер.
	 */
	public PortfolioSetCash() {
		super();
	}
	
	/**
	 * Установить размер доступных денежных средств.
	 * <p>
	 * Допустимый тип значений {@link java.lang.Double}.
	 * Значения иных типов игнорируются.
	 */
	@Override
	public void set(EditablePortfolio portfolio, Object value) {
		if ( value != null ) {
			Class<?> valueClass = value.getClass(); 
			if ( valueClass == Double.class ) {
				portfolio.setCash((Double) value);
			}
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == PortfolioSetCash.class;
	}

}
