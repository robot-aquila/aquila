package ru.prolib.aquila.core.timetable;

/**
 * Дни недели.
 */
public enum DOW {
	/**
	 * Понедельник.
	 */
	MONDAY(1),
	/**
	 * Вторник.
	 */
	TUESDAY(2),
	/**
	 * Среда.
	 */
	WEDNESDAY(3),
	/**
	 * Четверг.
	 */
	THURSDAY(4),
	/**
	 * Пятница.
	 */
	FRIDAY(5),
	/**
	 * Суббота.
	 */
	SATURDAY(6),
	/**
	 * Воскресенье.
	 */
	SUNDAY(7);

	private final int dow;
	
	private DOW(int dayOfWeek) {
		this.dow = dayOfWeek;
	}
	
	/**
	 * Получить номер дня недели.
	 * <p>
	 * @return номер дня недели
	 */
	public int getNumber() {
		return dow;
	}
	
}
