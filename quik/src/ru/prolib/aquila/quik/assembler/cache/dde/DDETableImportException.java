package ru.prolib.aquila.quik.assembler.cache.dde;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.dde.utils.table.DDETableException;

/**
 * Общее исключение импорта таблицы.
 */
public class DDETableImportException extends DDETableException {
	private static final long serialVersionUID = -218048007059050443L;

	public DDETableImportException(String table, Throwable e) {
		super("Table [" + table + "]: " + e.getMessage(), table, e);
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == null ) {
			return false;
		}
		if ( other == this ) {
			return true;
		}
		if ( other.getClass() != DDETableImportException.class ) {
			return false;
		}
		DDETableImportException o = (DDETableImportException) other;
		return new EqualsBuilder()
			.append(getTableName(), o.getTableName())
			.append(getCause(), o.getCause())
			.isEquals();
	}

}
