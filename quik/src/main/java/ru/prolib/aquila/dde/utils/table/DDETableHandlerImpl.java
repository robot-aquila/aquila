package ru.prolib.aquila.dde.utils.table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.dde.DDEException;
import ru.prolib.aquila.dde.DDETable;

/**
 * Стандартный обработчик таблицы.
 * <p>
 * 2012-08-15<br>
 * $Id: DDETableHandlerImpl.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class DDETableHandlerImpl implements DDETableHandler {
	private final DDETableRowSetBuilder sb;
	private final RowHandler rh;
	
	/**
	 * Создать обработчик таблицы.
	 * <p>
	 * @param setBuilder конструктор набора записей
	 * @param rowHandler обработчик строки таблицы
	 */
	public DDETableHandlerImpl(DDETableRowSetBuilder setBuilder,
							   RowHandler rowHandler)
	{
		super();
		if ( setBuilder == null ) {
			throw new NullPointerException("Row set builder cannot be null");
		}
		sb = setBuilder;
		if ( rowHandler == null ) {
			throw new NullPointerException("Row handler cannot be null");
		}
		rh = rowHandler;
	}
	
	/**
	 * Получить обработчик ряда.
	 * <p>
	 * @return обработчик ряда
	 */
	public RowHandler getRowHandler() {
		return rh;
	}
	
	/**
	 * Получить конструктор набора
	 * <p>
	 * @return конструктор набора записей
	 */
	public DDETableRowSetBuilder getRowSetBuilder() {
		return sb;
	}

	@Override
	public void handle(DDETable table) throws DDEException {
		RowSet rs = sb.createRowSet(table);
		try {
			while ( rs.next() ) {
				rh.handle(rs);
			}
		} catch ( RowException e ) {
			throw new DDEException(e);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof DDETableHandlerImpl ) {
			DDETableHandlerImpl o = (DDETableHandlerImpl) other;
			return new EqualsBuilder()
				.append(rh, o.rh)
				.append(sb, o.sb)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121105, 201147)
			.append(sb)
			.append(rh)
			.toHashCode();
	}

}
