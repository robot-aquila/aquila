package ru.prolib.aquila.stat.counter;

/**
 * Валидатор значения агрегатных счетчиков типа {@link Count} используется для
 * определения условия вхождения в выборку. Если валидатор возвращает true, то
 * значение будет учтено в результате.
 * 
 * 2012-02-06
 * $Id: Validator.java 198 2012-02-06 13:04:25Z whirlwind $
 */
public interface Validator {
	
	public boolean shouldCounted(Double value);

}
