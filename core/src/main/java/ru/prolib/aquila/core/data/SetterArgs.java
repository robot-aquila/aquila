package ru.prolib.aquila.core.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Аргументы сеттера.
 * <p>
 * 2013-01-07<br>
 * $Id: SetterArgs.java 399 2013-01-06 23:29:15Z whirlwind $
 */
public class SetterArgs {
	private final Object object,value;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param object первый аргумент метода {@link S#set(Object, Object)})
	 * @param value второй аргумент метода {@link S#set(Object, Object)})
	 */
	public SetterArgs(Object object, Object value) {
		super();
		this.object = object;
		this.value = value;
	}
	
	/**
	 * Получить объект модификации.
	 * <p>
	 * @return первый аргумент метода {@link S#set(Object, Object)})
	 */
	public Object getObject() {
		return object;
	}
	
	/**
	 * Получить значение элемента.
	 * <p>
	 * @return второй аргумент метода {@link S#set(Object, Object)})
	 */
	public Object getValue() {
		return value;
	}
	
	@Override
	public boolean equals(Object other) {
		return other != null && other.getClass() == SetterArgs.class
			? fieldsEquals(other) : false;
	}
	
	protected boolean fieldsEquals(Object other) {
		SetterArgs o = (SetterArgs) other;
		return new EqualsBuilder()
			.append(object, o.object)
			.append(value, o.value)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20130107, 30019)
			.append(object)
			.append(value)
			.toHashCode();
	}
	
}