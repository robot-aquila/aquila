package ru.prolib.aquila.quik.subsys.getter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;

/**
 * Геттер дескриптора инструмента на основе наименования.
 * <p>
 * 2013-01-23<br>
 * $Id: QUIKGetSecurityDescriptor1.java 520 2013-02-12 10:12:53Z whirlwind $
 */
public class QUIKGetSecurityDescriptor1 implements G<SecurityDescriptor> {
	private final QUIKServiceLocator locator;
	private final G<String> gName;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param locator сервис-локатор
	 * @param gName геттер наименования
	 */
	public QUIKGetSecurityDescriptor1(QUIKServiceLocator locator,
			G<String> gName)
	{
		super();
		this.locator = locator;
		this.gName = gName;
	}
	
	/**
	 * Получить сервис-локатор.
	 * <p>
	 * @return сервис-локатор
	 */
	public QUIKServiceLocator getServiceLocator() {
		return locator;
	}
	
	/**
	 * Получить геттер наименования.
	 * <p>
	 * @return геттер
	 */
	public G<String> getNameGetter() {
		return gName;
	}

	@Override
	public SecurityDescriptor get(Object source) throws ValueException {
		String name = gName.get(source);
		return name == null ? null : locator.getDescriptors().getByName(name);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null
			&& other.getClass() == QUIKGetSecurityDescriptor1.class ?
					fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		QUIKGetSecurityDescriptor1 o = (QUIKGetSecurityDescriptor1) other;
		return new EqualsBuilder()
			.append(locator, o.locator)
			.append(gName, o.gName)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20130123, 81557)
			.append(locator)
			.append(gName)
			.toHashCode();
	}

}
