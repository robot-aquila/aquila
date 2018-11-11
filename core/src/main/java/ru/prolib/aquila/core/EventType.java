package ru.prolib.aquila.core;

import java.util.Set;

import ru.prolib.aquila.core.eque.HierarchyOfAlternatesObservable;

/**
 * Интерфейс типа события.
 * <p> 
 * 2012-04-09<br>
 * $Id: EventType.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public interface EventType extends HierarchyOfAlternatesObservable {
	
	/**
	 * Получить идентификатор типа события.
	 * <p>
	 * Идентификатор позволяет отличать конкретный тип события среди множества
	 * других типов по уникальной строке. Идентификатор задается явно при
	 * создании объекта или назначается автоматически, если не указан.
	 * Предназначен для использования в отладочных целях.
	 * <p>
	 * @return строковый идентификатор
	 */
	public String getId();

	/**
	 * Подписаться на событие.
	 * <p>
	 * Подписывает на получение событий в асинхронном режиме. События будут
	 * транслироваться в синхронном режиме, если тип события ограничен только
	 * синхронным способом передачи.
	 * <p>
	 * Если указанный получатель ранее был подписан на события в синхронном
	 * режиме, повторная подписка с помощью этого метода переведет его в режим
	 * асинхронного получения событий.
	 * <p>
	 * @param listener получатель
	 */
	public void addListener(EventListener listener);

	/**
	 * Отписаться от события.
	 * <p>
	 * @param listener - The event listener.
	 */
	public void removeListener(EventListener listener);
	
	/**
	 * Проверить наличие указанного получателя.
	 * <p>
	 * @param listener получатель
	 * @return результат проверки: true - если указанный получатель в списке
	 * наблюдателей данного типа, false - получатель не является наблюдателем
	 * события данного типа.
	 */
	public boolean isListener(EventListener listener);

	/**
	 * Подписаться на единичное событие.
	 * <p>
	 * Данный метод позволяет подписаться на первое поступившее событие данного
	 * типа. Перед получением события получатель автоматически отписывается от
	 * последующего получения событий данного типа.
	 * <p>
	 * @param listener получатель
	 * @return промежуточный обозреватель
	 */
	public EventListener listenOnce(EventListener listener);
	
	/**
	 * Очистить списки получателей.
	 */
	public void removeListeners();

	/**
	 * Получить общее количество получателей.
	 * <p>
	 * @return общее количество получателей
	 */
	public int countListeners();
	
	/**
	 * Get listeners.
	 * <p>
	 * @return set of listeners
	 */
	public Set<EventListener> getListeners();
	
	/**
	 * Add an alternate type.
	 * <p>
	 * Alternate type is an event type which should repeat events of this type.
	 * This may be accomplished with using of event factory which will build an
	 * event with same meaning but for different event type instances. Such
	 * behavior can be initiated by using
	 * {@link EventQueue#enqueue(EventType, EventFactory)} enqueue method.
	 * <p>
	 * @param type - event type to add
	 */
	public void addAlternateType(EventType type);
	
	/**
	 * Remove an alternate type.
	 * <p>
	 * For more details see description of the
	 * {@link #addAlternateType(EventType)} method.
	 * <p>
	 * @param type - event type to remove
	 */
	public void removeAlternateType(EventType type);
	
	/**
	 * Test that type is alternate for this type.
	 * <p>
	 * For more details see description of the
	 * {@link #addAlternateType(EventType)} method.
	 * <p>
	 * @param type - event type to check
	 * @return true if argument is an alternate type for this event type
	 */
	public boolean isAlternateType(EventType type);
	
	/**
	 * Get set of alternate types.
	 * <p>
	 * For more details see description of the
	 * {@link #addAlternateType(EventType)} method.
	 * <p>
	 * @return set of alternate types
	 */
	public Set<EventType> getAlternateTypes();
	
	/**
	 * Test does it have alternates or not.
	 * <p>
	 * For more details see description of the
	 * {@link #addAlternateType(EventType)} method.
	 * <p>
	 * @return returns true if this type does have at least one alternate type 
	 */
	public boolean hasAlternates();
	
	public void removeAlternates();
	
	public boolean hasListeners();
	
	public int countAlternates();
	
	public void removeAlternatesAndListeners();
	
}