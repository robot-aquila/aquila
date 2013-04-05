package ru.prolib.aquila.core.BusinessEntities.setter;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;

/**
 * Сеттер цены закрытия последней сессии по инструменту.
 * <p>
 * 2012-12-29<br>
 * $Id: SetSecurityClosePrice.java 388 2012-12-30 12:58:15Z whirlwind $
 */
public class SecuritySetClosePrice extends SecuritySetDouble {
	
	/**
	 * Конструктор.
	 */
	public SecuritySetClosePrice() {
		super();
	}

	@Override
	protected void setSecurityAttr(EditableSecurity security, Double value) {
		security.setClosePrice(value);
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == SecuritySetClosePrice.class;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121229, 120545)
			.append(SecuritySetClosePrice.class)
			.toHashCode();
	}

}
