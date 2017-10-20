package ru.prolib.aquila.dde.utils.table;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Дескриптор региона таблицы.
 * <p>
 * Дескриптор региона описывается координатами двух ячеек: верхней левой ячейки,
 * представляющей начало региона, и нижней правой, представляющей последнюю
 * ячейку региона.
 */
public class DDETableRange {
	private final int firstRow, firstCol, lastRow, lastCol;
	
	public DDETableRange(int firstRow, int firstCol, int lastRow, int lastCol) {
		super();
		this.firstRow = firstRow;
		this.firstCol = firstCol;
		this.lastRow = lastRow;
		this.lastCol = lastCol;
	}
	
	public int getFirstRow() {
		return firstRow;
	}
	
	public int getFirstCol() {
		return firstCol;
	}
	
	public int getLastRow() {
		return lastRow;
	}
	
	public int getLastCol() {
		return lastCol;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == DDETableRange.class ) {
			DDETableRange o = (DDETableRange) other;
			return new EqualsBuilder()
				.append(firstRow, o.firstRow)
				.append(firstCol, o.firstCol)
				.append(lastRow, o.lastRow)
				.append(lastCol, o.lastCol)
				.isEquals();
		} else {
			return false;
		}
	}

}
