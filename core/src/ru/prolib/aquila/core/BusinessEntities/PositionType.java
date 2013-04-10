package ru.prolib.aquila.core.BusinessEntities;

/**
 * Тип позиции.
 * <p>
 * 2012-12-26<br>
 * $Id: PositionType.java 383 2012-12-26 12:21:37Z whirlwind $
 */
public class PositionType {
	/**
	 * Длинная позиция.
	 */
	public static final PositionType LONG = new PositionType("Long");
	/**
	 * Нейтральная позиция (вне рынка).
	 */
	public static final PositionType CLOSE = new PositionType("Close");
	/**
	 * Короткая позиция.
	 */
	public static final PositionType SHORT = new PositionType("Short");
	/**
	 * Конфигурационный тип, определяющий возможность открытия как длиных,
	 * так и коротких позиций. Используется для настройки стратегий работы.
	 */
	public static final PositionType BOTH = new PositionType("Long & Short");
	
	private final String code;
	
	private PositionType(String code) {
		super();
		this.code = code;
	}
	
	/**
	 * Получить код типа.
	 * <p>
	 * @return код типа
	 */
	public String getCode() {
		return code;
	}
	
	@Override
	public String toString() {
		return code;
	}

}
