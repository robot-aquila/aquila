package ru.prolib.aquila.core.text;

/**
 * Интерфейс набора текстовых сообщений и меток.
 */
public interface IMessages {
	
	/**
	 * Получить идентификатор набора.
	 * <p>
	 * @return идентификатор набора сообщений
	 */
	public String getId();
	
	/**
	 * Получить текстовую метку.
	 * <p>
	 * @param label идентификатор метки
	 * @return текст
	 */
	public String get(String label);
	
	/**
	 * Форматировать сообщение.
	 * <p>
	 * @param label идентификатор сообщения
	 * @param args аргументы
	 * @return текст
	 */
	public String format(String label, Object... args);

}
