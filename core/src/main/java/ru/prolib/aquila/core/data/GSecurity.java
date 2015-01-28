package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;
import ru.prolib.aquila.core.BusinessEntities.CommonModel.Securities;
import ru.prolib.aquila.core.data.getter.GSecurityDescr;

/**
 * Геттер инструмента.
 * <p>
 * Геттер использует экземпляр {@link GSecurityDescr} для получения
 * дескриптора инструмента и набор инструментов для получения экземпляра
 * инструмента. Если дескриптор инструмента не определен или инструмент
 * в наборе не найден, возвращается null.
 * <p>
 * 2012-09-27<br>
 * $Id: GSecurity.java 543 2013-02-25 06:35:27Z whirlwind $
 */
@Deprecated
public class GSecurity implements G<Security> {
	private final G<SecurityDescriptor> gDescr;
	private final Securities securities;
	
	public GSecurity(G<SecurityDescriptor> gDescr, Securities securities) {
		super();
		this.gDescr = gDescr;
		this.securities = securities;
	}
	
	/**
	 * Получить геттер дескриптора инструмента.
	 * <p>
	 * @return геттер
	 */
	public G<SecurityDescriptor> getDescriptorGetter() {
		return gDescr;
	}
	
	/**
	 * Получить набор инструментов.
	 * <p>
	 * @return набор инструментов
	 */
	public Securities getSecurities() {
		return securities;
	}

	@Override
	public Security get(Object object) throws ValueException {
		SecurityDescriptor descr = (SecurityDescriptor) gDescr.get(object);
		if ( descr != null && securities.isSecurityExists(descr) ) {
			try {
				return securities.getSecurity(descr);
			} catch ( SecurityException e ) {
				throw new RuntimeException(e);
			}
		} else {
			return null;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == GSecurity.class ) {
			GSecurity o = (GSecurity) other;
			return new EqualsBuilder()
				.append(gDescr, o.gDescr)
				.append(securities, o.securities)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121103, /*0*/51621)
			.append(gDescr)
			.append(securities)
			.toHashCode();
	}

}
