package ru.prolib.aquila.core.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


/**
 * Валидатор эквивалентности двух объектов.
 * <p>
 * 2012-10-15<br>
 * $Id: ValidatorEq.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class ValidatorEq implements Validator {
	private final Object etalon;

	/**
	 * Конструктор.
	 * <p>
	 * @param etalon объект для сравнения
	 */
	public ValidatorEq(Object etalon) {
		super();
		this.etalon = etalon;
	}
	
	/**
	 * Получить объект для сравнения.
	 * <p>
	 * @return объект для сравнения
	 */
	public Object getEtalon() {
		return etalon;
	}

	@Override
	public boolean validate(Object object) throws ValidatorException {
		if ( etalon == null ) {
			return object == null ? true : false;
		} else {
			return etalon.equals(object);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other instanceof ValidatorEq ) {
			return new EqualsBuilder()
				.append(etalon, ((ValidatorEq) other).etalon)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(etalon).toHashCode();
	}

}
