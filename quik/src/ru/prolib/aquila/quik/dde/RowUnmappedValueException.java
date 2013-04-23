package ru.prolib.aquila.quik.dde;

import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Исключение в связи с отсутствием соответствующего значения в карте
 * трансформации значения ряда.
 */
public class RowUnmappedValueException extends RowDataException {
	private static final long serialVersionUID = -8580072711089837259L;
	private final String elementId, actualValue;
	private final Set<String> expected;

	/**
	 * Конструктор.
	 * <p>
	 * @param elementId идентификатор элемента
	 * @param actualValue фактическое значение, для которого не нашлось пары
	 * @param expected набор значений для которых есть соответствия
	 */
	public RowUnmappedValueException(String elementId, String actualValue,
			Set<String> expected)
	{
		super("Unmapped value [" + actualValue + "] for [" + elementId + "]. "
				+ "Expected one of " + expected);
		this.elementId = elementId;
		this.actualValue = actualValue;
		this.expected = expected;
	}
	
	public String getElementId() {
		return elementId;
	}
	
	public String getActualValue() {
		return actualValue;
	}
	
	public Set<String> getExpectedValues() {
		return expected;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == null ) {
			return false;
		}
		if ( other == this ) {
			return true;
		}
		if ( other.getClass() != RowUnmappedValueException.class ) {
			return false;
		}
		RowUnmappedValueException o = (RowUnmappedValueException) other;
		return new EqualsBuilder()
			.append(elementId, o.elementId)
			.append(actualValue, o.actualValue)
			.append(expected, o.expected)
			.isEquals();
	}

}
