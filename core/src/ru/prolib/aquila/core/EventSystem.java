package ru.prolib.aquila.core;

/**
 * Интерфейс фабрики системы событий. 
 * <p>
 * 2012-04-21<br>
 * $Id: EventSystem.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public interface EventSystem {
	
	/**
	 * Получить используемую очередь событий.
	 * <p>
	 * @return очередь событий
	 */
	public EventQueue getEventQueue();
	
	/**
	 * Создать диспетчер событий.
	 * <p>
	 * @return диспетчер событий
	 */
	public EventDispatcher createEventDispatcher();
	
	/**
	 * Создать диспетчер событий.
	 * <p>
	 * @param id идентификатор диспетчера
	 * @return диспетчер событий
	 */
	public EventDispatcher createEventDispatcher(String id);
	
	/**
	 * Создать событие общего типа.
	 * <p>
	 * Данный метод следует использовать при необходимости использования
	 * существующего диспетчера событий. Например, в случае разделения
	 * диспетчера между разными типами в рамках одного объекта-источника
	 * событий.
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @return тип события
	 */
	public EventType createGenericType(EventDispatcher dispatcher);
	
	/**
	 * Создать событие общего типа.
	 * <p>
	 * Позволяет назначать типу события конкретный идентификатор.
	 * См. {@link #createGenericType(EventDispatcher)}.
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param id идентификатор типа
	 * @return тип события
	 */
	public EventType createGenericType(EventDispatcher dispatcher, String id);
	
	/**
	 * Создать событие композитного типа.
	 * <p> 
	 * Данный метод создает композитное событие с правилом: генерировать
	 * событие, когда единожды получено событие каждого из указанных типов.
	 * <p>
	 * Данный метод следует использовать при необходимости использования
	 * существующего диспетчера событий. Например, в случае разделения
	 * диспетчера между разными типами в рамках одного объекта-источника
	 * событий.
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param types типы событий, составляющие композицию
	 * @return тип события
	 */
	@Deprecated
	public CompositeEventType
		createTypeEachEventOneTime(EventDispatcher dispatcher,
								   EventType[] types);
	
	/**
	 * Создать событие композитного типа.
	 * <p>
	 * Данный метод создает композитное событие с правилом: генерировать
	 * событие, когда единожды получено событие каждого из указанных типов.
	 * <p>
	 * Данный метод автоматически создает диспетчер событий по умелчанию.
	 * <p>
	 * @param types типы событий, составляющие композицию
	 * @return тип события
	 */
	@Deprecated
	public CompositeEventType
		createTypeEachEventOneTime(EventType[] types);
	
	/**
	 * Создать событие композитного типа.
	 * <p> 
	 * Данный метод создает композитное событие с правилом: генерировать
	 * событие, когда единожды получено событие каждого из указанных типов.
	 * <p>
	 * Данный метод следует использовать при необходимости использования
	 * существующего диспетчера событий и генератора специфического события.
	 * <p>
	 * @param dispatcher диспетчер событий
	 * @param types типы событий, составляющие композицию
	 * @param eventGenerator генератор события
	 * @return тип события
	 */
	@Deprecated
	public CompositeEventType
		createTypeEachEventOneTime(EventDispatcher dispatcher,
								   EventType[] types,
								   CompositeEventGenerator eventGenerator);

}
