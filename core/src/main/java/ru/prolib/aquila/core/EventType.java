package ru.prolib.aquila.core;

import java.util.List;
import java.util.Set;

/**
 * Интерфейс типа события.
 * <p> 
 * 2012-04-09<br>
 * $Id: EventType.java 513 2013-02-11 01:17:18Z whirlwind $
 */
public interface EventType {
	
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
	 * Подписаться на событие.
	 * <p>
	 * <b>DO NOT USE THIS METHOD IF YOU DON'T UNDERSTAND HOW IT WORKS!</b>
	 * <p>
	 * Подписывает на получение событий в синхронном режиме. События будут
	 * транслироваться том же потоке, в котором были сгенерированы. Иначе
	 * говоря, этот тип подписки обеспечивает максимально быструю доставку
	 * событий. Но такой способ может негативно повлиять на работу поставщика
	 * событий.
	 * <p>
	 * Если указанный получатель ранее был подписан на события в асинхронном
	 * режиме, повторная подписка с помощью этого метода переведет его в режим
	 * синхронного получения событий.
	 * <p>
	 * @param listener - The event listener.
	 */
	public void addSyncListener(EventListener listener);

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
	 * Проверить принадлежность к синхронным получателям.
	 * <p>
	 * @param listener получатель
	 * @return true - если это синхронный получатель событий, false - иначе
	 */
	public boolean isSyncListener(EventListener listener);
	
	/**
	 * Проверить принадлежность к асинхронным получателям.
	 * <p>
	 * @param listener получатель
	 * @return true - если это асинхронный получатель событий, false - иначе 
	 */
	public boolean isAsyncListener(EventListener listener);
	
	/**
	 * Проверить режим только синхронной трансляции.
	 * <p>
	 * @return true - если разрешена только синхронная трансляция событий 
	 */
	public boolean isOnlySyncMode();
	
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
	 * Получить список асинхронных получателей.
	 * <p>
	 * @return дубликат списка асинхронных получателей
	 */
	public List<EventListener> getAsyncListeners();
	
	/**
	 * Получить список синхронных получателей.
	 * <p>
	 * @return дубликат списка синхронных получателей
	 */
	public List<EventListener> getSyncListeners();
	
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