package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.utils.Validator;

/**
 * Сеттер-свитч.
 * <p>
 * Позволяет выбрать между двумя другими сеттерами одного типа на основании
 * результата валидации. Так как неизвестно какой из аргументов вызова
 * понадобится валидатору (возможно оба), для передачи аргументов используется
 * дополнительный объект типа {@link SetterArgs}.
 * <p>
 * 2013-01-07<br>
 * $Id: SSwitch.java 399 2013-01-06 23:29:15Z whirlwind $
 */
public class SSwitch<T> implements S<T> {
	private final Validator validator;
	private final S<T> ifTrue;
	private final S<T> ifFalse;
	
	/**
	 * Создать свитч.
	 * <p>
	 * @param validator валидатор (получает экземпляр {@link SetterArgs})
	 * @param ifTrue сеттер в случае истины в результате валидации
	 * @param ifFalse сеттер в случае ложного результата 
	 */
	public SSwitch(Validator validator, S<T> ifTrue, S<T> ifFalse) {
		super();
		this.validator = validator;
		this.ifTrue = ifTrue;
		this.ifFalse = ifFalse;
	}
	
	/**
	 * Получить сеттер в случае истины на валидаторе.
	 * <p>
	 * @return сеттер
	 */
	public S<T> getSetterIfTrue() {
		return ifTrue;
	}
	
	/**
	 * Получить сеттер в случае лжи на валидаторе.
	 * <p>
	 * @return сеттер
	 */
	public S<T> getSetterIfFalse() {
		return ifFalse;
	}
	
	/**
	 * Получить валидатор условия.
	 * <p>
	 * @return валидатор
	 */
	public Validator getValidator() {
		return validator;
	}

	@Override
	public void set(T object, Object value) {
		(validator.validate(new SetterArgs(object, value)) ? ifTrue : ifFalse)
			.set(object, value);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20130107, 032345)
			.append(validator)
			.append(ifTrue)
			.append(ifFalse)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == SSwitch.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		SSwitch<?> o = (SSwitch<?>) other;
		return new EqualsBuilder()
			.append(validator, o.validator)
			.append(ifTrue, o.ifTrue)
			.append(ifFalse, o.ifFalse)
			.isEquals();
	}

}
