package ru.prolib.aquila.stat.counter;

/**
 * Пропускает все значения.
 * 
 * 2012-02-06
 * $Id: ValidatorAll.java 198 2012-02-06 13:04:25Z whirlwind $
 */
public class ValidatorAll implements Validator {
	
	public ValidatorAll() {
		super();
	}

	@Override
	public boolean shouldCounted(Double value) {
		return true;
	}

}
