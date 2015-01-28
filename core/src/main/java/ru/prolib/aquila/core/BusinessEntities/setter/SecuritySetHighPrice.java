package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;

/**
 * Сеттер максимальной цены инструмента за сессию.
 * <p>
 * 2012-12-29<br>
 * $Id: SetSecurityHighPrice.java 388 2012-12-30 12:58:15Z whirlwind $
 */
public class SecuritySetHighPrice extends SecuritySetDouble {
	
	/**
	 * Конструктор.
	 */
	public SecuritySetHighPrice() {
		super();
	}

	@Override
	protected void setSecurityAttr(EditableSecurity security, Double value) {
		security.setHighPrice(value);
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == SecuritySetHighPrice.class;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121229, 154357)
			.append(SecuritySetHighPrice.class)
			.toHashCode();
	}

}
