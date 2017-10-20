package ru.prolib.aquila.core.BusinessEntities;

/**
 * Тип позиции.
 * <p>
 * 2012-12-26<br>
 * $Id: PositionType.java 383 2012-12-26 12:21:37Z whirlwind $
 */
public enum PositionType {
	/**
	 * Длинная позиция.
	 */
	LONG ("Long", true, false),
	/**
	 * Нейтральная позиция (вне рынка).
	 */
	CLOSE ("Close", false, false),
	/**
	 * Короткая позиция.
	 */
	SHORT ("Short", false, true),
	/**
	 * Конфигурационный тип, определяющий возможность открытия как длиных,
	 * так и коротких позиций. Используется для указания цели.
	 */
	BOTH ("Long & Short", true, true);
	
	private final String code;
	private final boolean isLong, isShort;
	
	PositionType(String code, boolean isLong, boolean isShort) {
		this.code = code;
		this.isLong = isLong;
		this.isShort = isShort;
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
	
	/**
	 * Это длинная позиция?
	 * <p>
	 * Данный метод позволяет определить, указывает-ли данный тип на длинную
	 * позицию. В случае, когда тип используется в контексте указания цели,
	 * данный признак рассматривается как разрешение на открытие длинной
	 * позиции.
	 * <p>
	 * @return true - длинная позиция или открытие длинной позиции разрешено
	 */
	public boolean isLong() {
		return isLong;
	}
	
	/**
	 * Это короткая позиция?
	 * <p>
	 * Данный метод позволяет определить, указывает-ли данный тип на короткую
	 * позицию. В случае, когда тип используется в контексте указания цели,
	 * данный признак рассматривается как разрешение на открытие короткой
	 * позиции.
	 * <p> 
	 * @return true - короткая позиция или открытие короткой позиции разрешено
	 */
	public boolean isShort() {
		return isShort;
	}

}
