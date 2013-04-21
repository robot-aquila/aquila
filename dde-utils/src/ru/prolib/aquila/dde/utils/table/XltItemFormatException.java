package ru.prolib.aquila.dde.utils.table;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Исключение формата строки item XLTable. 
 */
public class XltItemFormatException extends DDETableException {
	private static final long serialVersionUID = -4646164082434729988L;
	private final String item;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param table имя таблицы, к которой относится исключение
	 * @param item строка item таблицы
	 */
	public XltItemFormatException(String table, String item) {
		super("Invalid item format [" + item + "] for table [" + table + "]",
				table);
		this.item = item;
	}
	
	/**
	 * Получить строку item таблицы.
	 * <p>
	 * @return строка item
	 */
	public String getItem() {
		return item;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() == XltItemFormatException.class ) {
			XltItemFormatException o = (XltItemFormatException) other;
			return new EqualsBuilder()
				.append(getTableName(), o.getTableName())
				.append(item, o.item)
				.isEquals();
		}
		return false;
	}

}
