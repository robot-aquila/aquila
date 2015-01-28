package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import ru.prolib.aquila.core.utils.Validator;
import ru.prolib.aquila.core.utils.ValidatorException;

/**
 * Валидатор на основе геттера.
 * <p>
 * На вход принимает объект-источник, который передает установленному геттеру.
 * Результат полученный от геттера направляет установленному валидатору и
 * возвращает полученный от валидатора результат.
 * <p>
 * 2012-10-30<br>
 * $Id: GValidator.java 301 2012-11-04 01:37:17Z whirlwind $
 */
public class GValidator implements Validator {
	private final G<?> getter;
	private final Validator validator;
	
	public GValidator(G<?> getter, Validator validator) {
		super();
		this.getter = getter;
		this.validator = validator;
	}
	
	/**
	 * Получить геттер значения.
	 * <p>
	 * @return геттер
	 */
	public G<?> getGetter() {
		return getter;
	}
	
	/**
	 * Получить валидатор значения.
	 * <p>
	 * @return валидатор
	 */
	public Validator getValidator() {
		return validator;
	}

	@Override
	public boolean validate(Object object) throws ValidatorException {
		try {
			return validator.validate(getter.get(object));
		} catch ( ValueException e ) {
			throw new ValidatorException(e);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other != null && other.getClass() == GValidator.class ) {
			GValidator o = (GValidator) other;
			return new EqualsBuilder()
				.append(getter, o.getter)
				.append(validator, o.validator)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121031, /*0*/55551)
			.append(getter)
			.append(validator)
			.hashCode();
	}

}
