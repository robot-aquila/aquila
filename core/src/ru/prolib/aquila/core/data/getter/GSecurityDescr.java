package ru.prolib.aquila.core.data.getter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Геттер дескриптора инструмента.
 * <p>
 * Использует два геттера для доступа к коду инструмента и коду класса. В
 * случае отсутствия возможности создать дескриптор, возвращает null.  
 * <p>
 * 2012-08-28<br>
 * $Id: GSecurityDescr.java 543 2013-02-25 06:35:27Z whirlwind $
 */
public class GSecurityDescr implements G<SecurityDescriptor> {
	private final G<String> gCode;
	private final G<String> gClass;
	private final G<String> gCurr;
	private final G<SecurityType> gType;
	
	/**
	 * Создать геттер.
	 * <p>
	 * @param gCode геттер кода инструмента
	 * @param gClass геттер класса инструмента
	 * @param gCurr геттер кода валюты
	 * @param gType геттер типа инструмента
	 */
	public GSecurityDescr(G<String> gCode, G<String> gClass,
			G<String> gCurr, G<SecurityType> gType)
	{
		super();
		this.gCode = gCode;
		this.gClass = gClass;
		this.gCurr = gCurr;
		this.gType = gType;
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
	 * Получить геттер кода класса.
	 * <p>
	 * @return геттер
	 */
	public G<String> getClassGetter() {
		return gClass;
	}
	
	/**
	 * Получить геттер кода валюты.
	 * <p>
	 * @return геттер
	 */
	public G<String> getCurrencyGetter() {
		return gCurr;
	}
	
	/**
	 * Получить геттер типа инструмента.
	 * <p>
	 * @return геттер
	 */
	public G<SecurityType> getTypeGetter() {
		return gType;
	}

	@Override
	public SecurityDescriptor get(Object source) throws ValueException {
		SecurityDescriptor descr = new SecurityDescriptor(gCode.get(source),
				gClass.get(source), gCurr.get(source), gType.get(source));
		return descr.isValid() ? descr : null;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other instanceof GSecurityDescr ) {
			GSecurityDescr o = (GSecurityDescr) other;
			return new EqualsBuilder()
				.append(gCode, o.gCode)
				.append(gClass, o.gClass)
				.append(gCurr, o.gCurr)
				.append(gType, o.gType)
				.isEquals();			
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121031, 135927)
			.append(gCode)
			.append(gClass)
			.append(gCurr)
			.append(gType)
			.toHashCode();
	}

}
