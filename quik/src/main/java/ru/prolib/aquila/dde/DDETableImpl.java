package ru.prolib.aquila.dde;

/**
 * Реализация таблицы на основе массива ячеек. Данная реализация может быть
 * использована как для целей тестирования, так и в специфических реализациях
 * сервера DDE. 
 * <p>
 * 2012-07-16<br>
 * $Id: DDETableImpl.java 255 2012-08-15 17:42:52Z whirlwind $
 */
public class DDETableImpl implements DDETable {
	private final Object[] cells;
	private final String topic;
	private final String item;
	private final int cols;
	private final int rows;
	
	/**
	 * Создать таблицу.
	 * <p>
	 * @param cells массив ячеек
	 * @param topic строка темы
	 * @param item строка субъекта
	 * @param cols количество колонок
	 */
	public DDETableImpl(Object[] cells, String topic, String item, int cols) {
		super();
		this.cells = cells;
		this.topic = topic;
		this.item = item;
		this.cols = cols;
		this.rows = cells.length / cols;
	}
	
	/**
	 * Создать пустую таблицу.
	 */
	public DDETableImpl() {
		super();
		cells = null;
		topic = item = "";
		cols = rows = 0;
	}

	@Override
	public int getRows() {
		return rows;
	}

	@Override
	public int getCols() {
		return cols;
	}

	@Override
	public String getTopic() {
		return topic;
	}

	@Override
	public String getItem() {
		return item;
	}

	@Override
	public Object getCell(int row, int col) {
		int offset = row * cols + col;
		if ( offset >= cells.length ) {
			throw new IndexOutOfBoundsException("Cell index out of range: " +
												offset);
		}
		return cells[offset];
	}

}
