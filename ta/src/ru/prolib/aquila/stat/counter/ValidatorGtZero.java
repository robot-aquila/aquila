package ru.prolib.aquila.stat.counter;

/**
 * Валидатор условия значение типа {@link Double} больше нуля.
 * 
 * 2012-02-06
 * $Id: ValidatorGtZero.java 198 2012-02-06 13:04:25Z whirlwind $
 */
public class ValidatorGtZero implements Validator {
	
	public ValidatorGtZero() {
		super();
	}

	@Override
	public boolean shouldCounted(Double value) {
		return value > 0;
	}

}
