package ru.prolib.aquila.t2q;

/**
 * Статус подключения.
 * <p>
 * 2013-01-29<br>
 * $Id: T2QConnStatus.java 457 2013-01-29 11:32:17Z whirlwind $
 */
public class T2QConnStatus {
	/**
	 * Соединение между терминалом QUIK и сервером установлено.
	 */
	public static final T2QConnStatus QUIK_CONN;
	/**
	 * Соединение между терминалом QUIK и сервером разорвано.
	 */
	public static final T2QConnStatus QUIK_DISC;
	/**
	 * Соединение между DLL и используемым терминалом QUIK установлено.
	 */
	public static final T2QConnStatus DLL_CONN;
	/**
	 * Соединение между DLL и используемым терминалом QUIK разорвано.
	 */
	public static final T2QConnStatus DLL_DISC;
	
	static {
		QUIK_CONN = new T2QConnStatus("QUIK_CONN");
		QUIK_DISC = new T2QConnStatus("QUIK_DISC");
		DLL_CONN = new T2QConnStatus("DLL_CONN");
		DLL_DISC = new T2QConnStatus("DLL_DISC");
	}
	
	private final String code;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param code код статуса
	 */
	private T2QConnStatus(String code) {
		super();
		this.code = code;
	}
	
	/**
	 * Получить код статуса.
	 * <p>
	 * @return код статуса
	 */
	public String getCode() {
		return code;
	}
	
	@Override
	public String toString() {
		return getCode();
	}

}
