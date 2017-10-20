package ru.prolib.aquila.dde.utils.table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.aquila.dde.DDETable;

/**
 * Сдвиг области таблицы на определенное кол-во рядов и/или колонок.
 * <p> 
 * 2012-08-10<br>
 * $Id: DDETableShift.java 304 2012-11-06 09:17:07Z whirlwind $
 */
public class DDETableShift implements DDETable {
	private final int shiftCols;
	private final int shiftRows;
	private final DDETable source;
	
	/**
	 * Создать объект сдвига таблицы
	 * <p> 
	 * @param source исходная таблица
	 * @param shiftCols горизонтальное смещение (колонки)
	 * @param shiftRows вертикальное смещение (ряды)
	 */
	public DDETableShift(DDETable source, int shiftCols, int shiftRows) {
		super();
		this.shiftCols = shiftCols;
		this.shiftRows = shiftRows;
		this.source = source;
	}
	
	/**
	 * Получить исходную таблицу.
	 * <p>
	 * @return таблица
	 */
	public DDETable getTable() {
		return source;
	}
	
	/**
	 * Получить горизонтальное смещение.
	 * <p>
	 * @return смещение
	 */
	public int getShiftCols() {
		return shiftCols;
	}

	/**
	 * Получить вертикальное смещение.
	 * <p>
	 * @return смещение
	 */
	public int getShiftRows() {
		return shiftRows;
	}

	@Override
	public Object getCell(int row, int col) {
		row -= shiftRows;
		col -= shiftCols;
		if ( row < 0 || col < 0 ) {
			return null;
		}
		return source.getCell(row, col);
	}

	@Override
	public int getCols() {
		return source.getCols() + shiftCols;
	}

	@Override
	public String getItem() {
		return source.getItem();
	}

	@Override
	public int getRows() {
		return source.getRows() + shiftRows;
	}

	@Override
	public String getTopic() {
		return source.getTopic();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other instanceof DDETableShift ) {
			DDETableShift o = (DDETableShift) other;
			return new EqualsBuilder()
				.append(source, o.source)
				.append(shiftCols, o.shiftCols)
				.append(shiftRows, o.shiftRows)
				.isEquals();
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(20121107, 131133)
			.append(source)
			.append(shiftCols)
			.append(shiftRows)
			.toHashCode();
	}

}
