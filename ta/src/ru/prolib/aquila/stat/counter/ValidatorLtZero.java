package ru.prolib.aquila.stat.counter;

/**
 * Валидатор условия значение типа {@link Double} меньше нуля.
 * 
 * 2012-02-06
 * $Id: ValidatorLtZero.java 198 2012-02-06 13:04:25Z whirlwind $
 */
public class ValidatorLtZero implements Validator {
	
	public ValidatorLtZero() {
		super();
	}

	@Override
	public boolean shouldCounted(Double value) {
		return value < 0;
	}

}
