package ru.prolib.aquila.core.utils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Валидатор-заглушка.
 * <p>
 * Возвращает предустановленный результат валидации.
 * <p>
 * 2012-12-03<br>
 * $Id: ValidatorStub.java 327 2012-12-05 19:58:26Z whirlwind $
 */
public class ValidatorStub implements Validator {
	private final boolean result;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param result предустановленный результат валидации
	 */
	public ValidatorStub(boolean result) {
		super();
		this.result = result;
	}
	
	/**
	 * Получить предустановленный результат валидации.
	 * <p>
	 * @return результат валидации
	 */
	public boolean getResult() {
		return result;
	}

	@Override
	public boolean validate(Object object) throws ValidatorException {
		return result;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == getClass() ) {
			return fieldsEquals(other);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121203, 4033)
			.append(result)
			.toHashCode();
	}
	
	protected boolean fieldsEquals(Object other) {
		ValidatorStub o = (ValidatorStub) other;
		return new EqualsBuilder()
			.append(result, o.result)
			.isEquals();
	}

}
