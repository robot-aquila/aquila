package ru.prolib.aquila.core.BusinessEntities;

/**
 * Статус инструмента.
 * <p>
 * 2012-12-28<br>
 * $Id: SecurityStatus.java 388 2012-12-30 12:58:15Z whirlwind $
 */
public class SecurityStatus {
	/**
	 * Инструмент торгуется.
	 */
	public static final SecurityStatus TRADING = new SecurityStatus("Trading");
	/**
	 * Торги приостановлены.
	 */
	public static final SecurityStatus STOPPED = new SecurityStatus("Stopped");
	
	private final String code;
	
	/**
	 * Конструктор.
	 * <p>
	 * @param code код статуса
	 */
	private SecurityStatus(String code) {
		super();
		this.code = code;
	}
	
	@Override
	public String toString() {
		return code;
	}

	/**
	 * Получить код типа.
	 * <p>
	 * @return код типа
	 */
	public String getCode() {
		return code;
	}

}
