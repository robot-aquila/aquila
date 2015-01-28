package ru.prolib.aquila.core.BusinessEntities.setter;

import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Сеттер баланса портфеля.
 * <p>
 * 2012-10-28<br>
 * $Id: SetPortfolioBalance.java 388 2012-12-30 12:58:15Z whirlwind $
 */
public class PortfolioSetBalance implements S<EditablePortfolio> {

	/**
	 * Создать сеттер.
	 */
	public PortfolioSetBalance() {
		super();
	}
	
	/**
	 * Установить баланс портфеля.
	 * <p>
	 * Допустимый тип значений {@link java.lang.Double}.
	 * Значения иных типов игнорируются.
	 */
	@Override
	public void set(EditablePortfolio portfolio, Object value) throws ValueException {
		if ( value != null ) {
			Class<?> valueClass = value.getClass(); 
			if ( valueClass == Double.class ) {
				portfolio.setBalance((Double) value);
			}
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == PortfolioSetBalance.class;
	}

}
