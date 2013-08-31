package ru.prolib.aquila.core;

import java.util.List;

/**
 * Интерфейс диспетчера событий.
 * <p>
 * 2012-04-09<br>
 * $Id: EventDispatcher.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public interface EventDispatcher {
	
	/**
	 * Добавить получателя события указанного типа
	 * <p>
	 * @param type тип события
	 * @param listener получатель
	 */
	@Deprecated
	public void addListener(EventType type, EventListener listener);
	
	/**
	 * Удалить получателя события указанного типа
	 * <p>
	 * @param type тип события
	 * @param listener получатель
	 */
	@Deprecated
	public void removeListener(EventType type, EventListener listener);
	
	/**
	 * Удалить всех получателей события указанного типа.
	 * <p>
	 * @param type тип события
	 */
	@Deprecated
	public void removeListeners(EventType type);
	
	/**
	 * Отправить события для текущих наблюдателей.
	 * <p>
	 * В отличии от метода {@link #dispatch(Event)}, данный метод в качестве
	 * получателей данного события указывает копию текущего списка наблюдателей.
	 * И если между завершением данного вызова и моментом непосредственно
	 * отправки события из очереди произойдет изменения списка наблюдателей, то
	 * это не повлияет на получателей данного конкретного события. 
	 * <p>
	 * @param event событие
	 */
	public void dispatchForCurrentList(Event event);
	
	/**
	 * Отправить событие
	 * <p>
	 * @param event событие
	 */
	public void dispatch(Event event);
	
	/**
	 * Завершить работу
	 */
	public void close();
	
	/**
	 * Получить количество наблюдателей события указанного типа
	 * <p>
	 * @param type тип события
	 * @return количество наблюдателей
	 */
	@Deprecated
	public int countListeners(EventType type);
	
	/**
	 * Получить наблюдателей события указанного типа.
	 * <p>
	 * @param type тип события
	 * @return копия текущего списка получателей
	 */
	@Deprecated
	public List<EventListener> getListeners(EventType type);
	
	/**
	 * Проверить получателя.
	 * <p>
	 * Выполняет проверку является ли указанный экземпляр получателя
	 * наблюдателем события указанного типа.
	 * <p> 
	 * @param type тип события
	 * @param listener получатель
	 * @return результат проверки: true - указанный получатель является
	 * наблюдателем события данного типа, false - не является наблюдателем
	 */
	@Deprecated
	public boolean isTypeListener(EventType type, EventListener listener);
	
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
	 * Создать тип события, связанный с диспетчером.
	 * <p>
	 * @return тип события с идентификатором по-умолчанию
	 */
	public EventType createType();
	
	/**
	 * Создать тип события, связанный с диспетчером.
	 * <p>
	 * @param typeId идентификатор типа события
	 * @return тип события
	 */
	public EventType createType(String typeId);

}
