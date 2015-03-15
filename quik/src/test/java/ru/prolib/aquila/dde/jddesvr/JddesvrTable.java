package ru.prolib.aquila.dde.jddesvr;

import ru.prolib.aquila.dde.DDEBlank;
import ru.prolib.aquila.dde.DDEError;
import ru.prolib.aquila.dde.DDESkip;
import ru.prolib.aquila.dde.DDETable;
import ru.prolib.aquila.jddesvr.Cell;
import ru.prolib.aquila.jddesvr.Table;

/**
 * Обертка-адаптер DDE-таблицы из пакета jddesvr.
 * <p>
 * 2012-07-20<br>
 * $Id$
 */
class JddesvrTable implements DDETable {
	private final Table table;

	/**
	 * Обернуть таблицу.
	 * <p>
	 * @param table таблица в формате jddesvr
	 */
	public JddesvrTable(Table table) {
		super();
		this.table = table;
	}

	@Override
	public Object getCell(int row, int col) {
		Cell cell = null;
		try {
			cell = table.getCell(row, col);
		} catch ( Exception e ) {
			throw new IndexOutOfBoundsException("R" + row + ":C" + col);
		}
		if ( cell.isBlank() ) {
			return new DDEBlank();
		} else if ( cell.isBoolean() ) {
			return cell.asBoolean();
		} else if ( cell.isError() ) {
			return new DDEError();
		} else if ( cell.isFloat() ) {
			return cell.asFloat();
		} else if ( cell.isInteger() ) {
			return cell.asInteger();
		} else if ( cell.isSkip() ) {
			return new DDESkip();
		} else if ( cell.isString() ) {
			return cell.asString();
		} else {
			return null;
		}
	}

	@Override
	public int getCols() {
		return table.getCols();
	}

	@Override
	public String getItem() {
		return table.getItem();
	}

	@Override
	public int getRows() {
		return table.getRows();
	}

	@Override
	public String getTopic() {
		return table.getTopic();
	}
	
	protected void finalize() throws Throwable {
		table.delete();
		super.finalize();
	}

}
