package ru.prolib.aquila.dde.utils.table;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Исключение, свидетельствующее о неполном составе полей импортируемой таблицы.
 */
public class NotAllRequiredFieldsException extends DDETableException {
	private static final long serialVersionUID = 8222083880666738379L;
	private String field;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param table имя таблицы, к которой относится исключение 
	 * @param field идентификатор отсутствующего поля
	 */
	public NotAllRequiredFieldsException(String table, String field) {
		super("Field [" + field + "] is required for table [" + table + "]",
				table);
		this.field = field;
	}
	
	/**
	 * Получить имя необходимого поля.
	 * <p>
	 * @return имя поля
	 */
	public String getFieldName() {
		return field;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != NotAllRequiredFieldsException.class ) {
			return false;
		}
		NotAllRequiredFieldsException o = (NotAllRequiredFieldsException) other;
		return new EqualsBuilder()
			.append(getTableName(), o.getTableName())
			.append(field, o.field)
			.isEquals();
	}

}
