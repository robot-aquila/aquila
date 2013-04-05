package ru.prolib.aquila.core.data.row;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.data.G;

/**
 * Геттер элемента ряда.
 * <p>
 * 2012-08-22<br>
 * $Id$
 */
@SuppressWarnings("rawtypes")
public class RowElement implements G {
	private final String elementId;
	private final Class<?> elementClass;

	/**
	 * Конструктор.
	 * <p>
	 * @param elementId идентификатор элемента
	 * @param elementClass ожидаемый класс элемента
	 */
	public RowElement(String elementId, Class<?> elementClass) {
		super();
		this.elementId = elementId;
		this.elementClass = elementClass;
	}

	/**
	 * Получить идентификатор элемента.
	 * <p>
	 * @return идентификатор элемента
	 */
	public String getElementId() {
		return elementId;
	}
	
	/**
	 * Получить ожидаемый класс элемента.
	 * <p>
	 * @return класс элемента
	 */
	public Class getElementClass() {
		return elementClass;
	}

	/**
	 * Получить элемент ряда.
	 * <p>
	 * Данный метод в качестве источника ожидает объект, реализующий интерфейс
	 * {@link Row}. Если в качестве источника данных указан объект, который
	 * не реализует этот интерфейс, результатом вызов метода будет null.
	 * <p>
	 * При наличии элемента ряда, идентификатор которого был указан при
	 * создании данного объекта, выполняется проверка соответствия класса
	 * элемента классу, указанному при создании данного геттера. Если класс
	 * элемента ряда соответствует указанному, то результатом работы метода
	 * будет элемент ряда. В противном случае, метод вернет null. 
	 * <p>
	 * @param source источник данных
	 * @return элемент ряда или null
	 */
	@Override
	public Object get(Object source) {
		if ( source instanceof Row ) {
			Object object = ((Row) source).get(elementId);
			return object != null && object.getClass() == elementClass
				? object : null;
		}
		return null;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == RowElement.class ) {
			RowElement o = (RowElement) other;
			return new EqualsBuilder()
				.append(elementId, o.elementId)
				.append(elementClass, o.elementClass)
				.isEquals();
			
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName()
			+ "[" + elementId + ", " + elementClass.getSimpleName() + "]";
	}

}
