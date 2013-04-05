package ru.prolib.aquila.core.BusinessEntities.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Генератор события: доступен новый инструмент.
 * <p>
 * 2013-02-06<br>
 * $Id$
 */
public class FireSecurityAvailable implements FireEditableEvent {
	private final EditableSecurities securities;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param securities набор инструментов
	 */
	public FireSecurityAvailable(EditableSecurities securities) {
		super();
		this.securities = securities;
	}
	
	/**
	 * Получить набор инструментов.
	 * <p>
	 * @return набор инструментов
	 */
	public EditableSecurities getSecurities() {
		return securities;
	}

	@Override
	public void fireEvent(Editable object) {
		securities.fireSecurityAvailableEvent((Security) object);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null && other.getClass()
			== FireSecurityAvailable.class ? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		FireSecurityAvailable o = (FireSecurityAvailable) other;
		return new EqualsBuilder()
			.append(securities, o.securities)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20130207, 214223)
			.append(securities)
			.toHashCode();
	}

}
