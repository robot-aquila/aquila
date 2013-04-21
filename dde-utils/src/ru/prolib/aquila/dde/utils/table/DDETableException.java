package ru.prolib.aquila.dde.utils.table;

import ru.prolib.aquila.dde.DDEException;

/**
 * Базовое исключение обработки DDE таблицы.  
 */
public class DDETableException extends DDEException {
	private static final long serialVersionUID = 5340534040930224148L;
	private final String table;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param msg сообщение
	 * @param table имя таблицы, в связи с которой возникло исключение
	 */
	public DDETableException(String msg, String table) {
		super(msg);
		this.table = table;
	}
	
	/**
	 * Получить имя таблицы в связи с исключением.
	 * <p>
	 * @return имя таблицы
	 */
	public String getTableName() {
		return table;
	}

}
