package ru.prolib.aquila.dde;

/**
 * Интерфейс таблицы XLTable.
 * <p>
 * 2012-07-15<br>
 * $Id: DDETable.java 241 2012-07-25 13:52:22Z whirlwind $
 */
public interface DDETable {
	
	/**
	 * Получить количество строк таблицы.
	 * <p>
	 * @return количество строк
	 */
	public int getRows();
	
	/**
	 * Получить количество колонок таблицы.
	 * <p>
	 * @return количество колонок
	 */
	public int getCols();
	
	/**
	 * Получить строку темы.
	 * <p>
	 * @return тема
	 */
	public String getTopic();
	
	/**
	 * Получить строку субъекта.
	 * <p>
	 * @return субъект
	 */
	public String getItem();
	
	/**
	 * Получить значение ячейки.
	 * <p>
	 * @param row индекс строки
	 * @param col индекс колонки
	 * @return значение соответствующей ячейки таблицы. Может быть объектом
	 * одного из следующих классов:
	 * {@link java.lang.Integer} - для целого<br>
	 * {@link java.lang.Double} - для вещественного<br>
	 * {@link java.lang.Boolean} - для булева<br>
	 * {@link java.lang.String} - для строк<br>
	 * {@link DDEBlank} - пустая ячейка<br>
	 * {@link DDEError} - ячейка с ошибкой<br>
	 * {@link DDESkip} - пропуск ячейки<br>
	 * @throws IndexOutOfBoundsException указаны координаты за пределами таблицы
	 */
	public Object getCell(int row, int col);

}
