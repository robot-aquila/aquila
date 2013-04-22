package ru.prolib.aquila.quik.dde;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Исключение в связи с неопределенным значением элемента ряда.
 */
public class RowNullValueException extends RowDataException {
	private static final long serialVersionUID = -5041196623423173564L;
	private final String elementId;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param elementId элемент, которому соответствует null-значение
	 */
	public RowNullValueException(String elementId) {
		super("Unexpected null value: " + elementId);
		this.elementId = elementId;
	}
	
	/**
	 * Получить идентификатор соответствующего элемента ряда.
	 * <p>
	 * @return идентификатор элемента, которому соответствует null-значение
	 */
	public String getElementId() {
		return elementId;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() == RowNullValueException.class ) {
			RowNullValueException o = (RowNullValueException) other;
			return new EqualsBuilder()
				.append(elementId, o.getElementId())
				.isEquals();
		}
		return false;
	}

}
