package ru.prolib.aquila.core;

/**
 * Интерфейс диспетчера событий.
 * <p>
 * 2012-04-09<br>
 * $Id: EventDispatcher.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public interface EventDispatcher extends EventProducer {
		
	/**
	 * Dispatch event.
	 * <p>
	 * @param type - event type
	 * @param factory - event factory
	 */
	void dispatch(EventType type, EventFactory factory);
	
	/**
	 * Завершить работу
	 */
	public void close();
	
	/**
	 * Получить идентификатор диспетчера.
	 * <p>
	 * Идентификатор позволяет отличать конкретный диспетчер (фактически
	 * его владельца) среди множества других диспетчеров по уникальной строке.
	 * Идентификатор задается явно при создании объекта или назначается
	 * автоматически, если не указан. Предназначен для использования в
	 * отладочных целях.
	 * <p>
	 * @return строковый идентификатор
	 */
	public String getId();
	
	/**
	 * Получить полный идентификатор в виде строки.
	 * <p>
	 * @return полный идентификатор
	 */
	public String asString();
	
	/**
	 * Создать тип события.
	 * <p>
	 * Создает тип события с идентификатором по-умолчанию.
	 * <p>
	 * @return тип события
	 */
	public EventType createType();
	
	/**
	 * Создать тип события.
	 * <p>
	 * Создает тип события с указанным идентификатором.
	 * <p>
	 * @param typeId идентификатор типа события
	 * @return тип события
	 */
	public EventType createType(String typeId);

}
