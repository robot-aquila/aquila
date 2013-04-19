package ru.prolib.aquila.core.BusinessEntities.getter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.G;
import ru.prolib.aquila.core.data.ValueException;

/**
 * Геттер торгового счета.
 * <p>
 * 2012-09-27<br>
 * $Id: GAccount.java 332 2012-12-09 12:06:25Z whirlwind $
 */
public class GAccount implements G<Account> {
	private final G<String> gCode,gSubCode,gSubCode2;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param gCode геттер кода счета
	 * @param gSubCode геттер суб-кода счета
	 * @param gSubCode2 геттер вторичного суб-кода счета
	 */
	public GAccount(G<String> gCode, G<String> gSubCode, G<String> gSubCode2) {
		super();
		this.gCode = gCode;
		this.gSubCode = gSubCode;
		this.gSubCode2 = gSubCode2;
	}
	
	/**
	 * Конструктор без геттера вторичного суб-кода счета.
	 * <p>
	 * @param gCode геттер кода счета
	 * @param gSubCode геттер суб-кода счета
	 */
	public GAccount(G<String> gCode, G<String> gSubCode) {
		this(gCode, gSubCode, null);
	}
	
	/**
	 * Конструктор без геттеров суб-кода и вторичного суб-кода счета. 
	 * <p>
	 * @param gCode геттер кода счета
	 */
	public GAccount(G<String> gCode) {
		this(gCode, null, null);
	}
	
	/**
	 * Получить геттер кода счета.
	 * <p>
	 * @return геттер
	 */
	public G<String> getCodeGetter() {
		return gCode;
	}
	
	/**
	 * Получить геттер суб-кода счета.
	 * <p>
	 * @return геттер
	 */
	public G<String> getSubCodeGetter() {
		return gSubCode;
	}
	
	public G<String> getSubCode2Getter() {
		return gSubCode2;
	}

	/**
	 * Создает экземпляр счета.
	 * <p>
	 * @param source источник данных
	 * @return счет
	 */
	@Override
	public Account get(Object source) throws ValueException {
		String code = gCode.get(source);
		String subCode = null, subCode2 = null;
		if ( gSubCode != null ) subCode = gSubCode.get(source);
		if ( gSubCode2 != null ) subCode2 = gSubCode2.get(source);
		return new Account(code, subCode, subCode2);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == GAccount.class ) {
			return fieldsEquals(other);
		} else {
			return false;
		}
	}
	
	protected boolean fieldsEquals(Object other) {
		GAccount o = (GAccount) other;
		return new EqualsBuilder()
			.append(gCode, o.gCode)
			.append(gSubCode, o.gSubCode)
			.append(gSubCode2, o.gSubCode2)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121031, /*0*/45555)
			.append(gCode)
			.append(gSubCode)
			.append(gSubCode2)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[code=" + gCode + ", "
			+ "subCode=" + gSubCode + ", "
			+ "subCode2=" + gSubCode2 + "]";
	}

}
