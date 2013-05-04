package ru.prolib.aquila.quik.subsys.getter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.quik.subsys.QUIKServiceLocator;

/**
 * Геттер дескриптора инструмента на основе кода и класса инструмента.
 * <p>
 * 2013-01-23<br>
 * $Id: QUIKGetSecurityDescriptor2.java 520 2013-02-12 10:12:53Z whirlwind $
 */
public class QUIKGetSecurityDescriptor2 implements G<SecurityDescriptor> {
	private final QUIKServiceLocator locator;
	private final G<String> gCode, gClass;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param locator сервис-локатор
	 * @param gCode геттер кода инструмента
	 * @param gClass геттер класса инструмента
	 */
	public QUIKGetSecurityDescriptor2(QUIKServiceLocator locator,
			G<String> gCode, G<String> gClass)
	{
		super();
		this.locator = locator;
		this.gCode = gCode;
		this.gClass = gClass;
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
	 * Получить геттер кода инструмента.
	 * <p>
	 * @return геттер
	 */
	public G<String> getCodeGetter() {
		return gCode;
	}
	
	/**
	 * Получить геттер класса инструмента.
	 * <p>
	 * @return геттер
	 */
	public G<String> getClassGetter() {
		return gClass;
	}
	
	@Override
	public SecurityDescriptor get(Object arg0) throws ValueException {
		String secCode = gCode.get(arg0);
		String secClass = gClass.get(arg0);
		return secCode == null || secClass == null ? null
			: locator.getPartiallyKnownObjects()
				.getSecurityDescriptorByCodeAndClass(secCode, secClass);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		return other != null
			&& other.getClass() == QUIKGetSecurityDescriptor2.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		QUIKGetSecurityDescriptor2 o = (QUIKGetSecurityDescriptor2) other;
		return new EqualsBuilder()
			.append(locator, o.locator)
			.append(gCode, o.gCode)
			.append(gClass, o.gClass)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20130123, 102129)
			.append(locator)
			.append(gCode)
			.append(gClass)
			.toHashCode();
	}

}
