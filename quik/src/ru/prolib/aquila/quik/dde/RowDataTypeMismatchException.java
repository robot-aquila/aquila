package ru.prolib.aquila.quik.dde;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Исключение несовпадения с ожидаемым типом значения ряда.
 */
public class RowDataTypeMismatchException extends RowDataException {
	private static final long serialVersionUID = 7612473191806886414L;
	private final String elementId, expectType;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param elementId идентификатор элемента ряда с некорректым типом значения
	 * @param expectType строковое представление ожидаемого типа данных
	 */
	public RowDataTypeMismatchException(String elementId, String expectType) {
		super("Data type mismatch. Expected [" + expectType
				+ "] for [" + elementId + "]");
		this.elementId = elementId;
		this.expectType = expectType;
	}
	
	/**
	 * Получить идентификатор соответствующего элемента.
	 * <p>
	 * @return идентификатор элемента
	 */
	public String getElementId() {
		return elementId;
	}
	
	/**
	 * Получить строковое представление ожидаемого типа данных.
	 * <p>
	 * @return ожидаемый тип данных 
	 */
	public String getExpectedType() {
		return expectType;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != RowDataTypeMismatchException.class ) {
			return false;
		}
		RowDataTypeMismatchException o = (RowDataTypeMismatchException) other;
		return new EqualsBuilder()
			.append(elementId, o.elementId)
			.append(expectType, o.expectType)
			.isEquals();
	}

}
