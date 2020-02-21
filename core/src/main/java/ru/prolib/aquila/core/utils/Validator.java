package ru.prolib.aquila.core.utils;

/**
 * Интерфейс валидатора.
 * <p>
 * 2012-09-23<br>
 * $Id: Validator.java 287 2012-10-15 03:30:51Z whirlwind $
 */
public interface Validator<ValueType> {
	
	/**
	 * Выполнить валидацию условий.
	 * <p>
	 * @param object объект проверки
	 * @return true - удовлетворяет условиям, false - условия не выполнены 
	 * @throws ValidatorException - If error occurred.
	 */
	public boolean validate(ValueType object) throws ValidatorException;

}
