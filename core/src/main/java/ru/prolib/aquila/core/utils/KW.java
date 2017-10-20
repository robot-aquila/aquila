package ru.prolib.aquila.core.utils;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * KeyWrapper - обертка ключа.
 * <p> 
 * Данный класс предназначен для использования в тех случаях, когда наличие
 * элементов коллекции определяется не по {@link Object#equals(Object)}, а по
 * уникальности экземпляров объектов. Например, если ключами хэш-массива должны
 * выступать экземпляры объектов, класс которых подразумевает совпадение по
 * значениям атрибутов объектов класса. В этом случае, нельзя использовать
 * хэш-массив для связывания экземпляра с каким либо-значением, так как при
 * совпадении по атрибутам соответствующая запись коллекции для второго объекта
 * создана не будет. Использование данного класса гарантирует, что два субъекта
 * сравнения равны только в том случае, если указывают на один и тот же
 * экземпляр. Естественно, что для получения нужного результата оба экземпляра
 * должны быть завернуты в обертку из данного класса. Это могут быть два разных
 * объекта обертки, но с одним и тем же завернутым экземпляром. 
 * <p>
 * @param <T> фактический тип ключа
 */
public class KW<T> {
	private final T instance;
	
	public KW(T instance) {
		super();
		this.instance = instance;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != KW.class ) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		KW o = (KW) other;
		return o.instance == instance;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(instance).toHashCode();
	}
	
	/**
	 * Получить обернутый экземпляр.
	 * <p>
	 * @return экземпляр объекта
	 */
	public T instance() {
		return instance;
	}

}
