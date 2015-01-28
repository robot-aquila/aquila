package ru.prolib.aquila.core.text;

/**
 * Фасад к подсистеме текстовых сообщений и меток.
 */
public interface IMessagesRegistry {
	
	/**
	 * Получить набор сообщений.
	 * <p>
	 * @param id идентификатор набора
	 * @return набор сообщений
	 */
	public IMessages getMessages(String id);

}
