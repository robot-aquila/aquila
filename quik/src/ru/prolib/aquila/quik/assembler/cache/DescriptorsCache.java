package ru.prolib.aquila.quik.assembler.cache;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Кэш дескрипторов инструментов.
 * <p>
 * Обеспечивает доступ к дескрипторам инструментов по частичной идентификации.
 * Позволяет отслеживать изменения содержимого кэша.
 */
public class DescriptorsCache {
	/**
	 * Разделитель.
	 */
	private static final String SEP = "#";
	
	/**
	 * Карта сопоставления краткого наименования дескриптору инструмента.
	 */
	private final Map<String, SecurityDescriptor> short2descr;
	
	/**
	 * Карта сопоставления комбинации кода инструмента и кода класса
	 * дескриптору инструмента.
	 */
	private final Map<String, SecurityDescriptor> long2descr;
	
	private final EventDispatcher dispatcher;
	private final EventType onUpdate; 
	
	public DescriptorsCache(EventDispatcher dispatcher, EventType onUpdate) {
		super();
		short2descr = new LinkedHashMap<String, SecurityDescriptor>();
		long2descr = new Hashtable<String, SecurityDescriptor>();
		this.dispatcher = dispatcher;
		this.onUpdate = onUpdate;
	}
	
	public EventType OnUpdate() {
		return onUpdate;
	}
	
	EventDispatcher getEventDispatcher() {
		return dispatcher;
	}
	
	/**
	 * Зарегистрировать инструмент.
	 * <p>
	 * Регистрирует инструмент для последующего доступа к дескриптору с неполной
	 * идентификацией. Если дескриптор, соответствующий записи, уже
	 * зарегистрирован, то никаких изменений не выполняется. Если запись
	 * ссылается на незарегистрированный ранее дескриптор, то выполняется
	 * регистрация с последующей генерацией события о доступности нового
	 * дескриптора. Сама запись инструмента не кэшируется и впоследствии
	 * недоступна через кэш.
	 * <p>
	 * @param entry запись инструмента
	 * @return возвращает true, если был зарегистрирован новый дескриптор
	 */
	public synchronized boolean put(SecurityEntry entry) {
		String shortName = entry.getShortName();
		if ( ! short2descr.containsKey(shortName) ) {
			set(entry);
			dispatcher.dispatch(new EventImpl(onUpdate));
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Получить дескриптор инструмента.
	 * <p>
	 * Позволяет получить дескриптор инструмента по неполным идентификационным
	 * данным: комбинации кода инструмента и кода класса инструмента.
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса инструмента
	 * @return дескриптор инструмента или null, если нет такого инструмента
	 */
	public synchronized SecurityDescriptor get(String code, String classCode) {
		return long2descr.get(code + SEP + classCode);
	}
	
	/**
	 * Получить дескриптор инструмента.
	 * <p>
	 * Позволяет получить дескриптор инструмента по неполным идентификационным
	 * данным: краткому наименованию инструмента.
	 * <p>
	 * @param name краткое наименование инструмента
	 * @return дескриптор инструмента или null, если нет такого инструмента
	 */
	public synchronized SecurityDescriptor get(String shortName) {
		return short2descr.get(shortName);
	}
	
	/**
	 * Получить список всех зарегистрированных дескрипторов.
	 * <p>
	 * @return список дескрипторов инструментов
	 */
	public synchronized List<SecurityDescriptor> get() {
		return new Vector<SecurityDescriptor>(short2descr.values());
	}
	
	/**
	 * Сохранить кэш-запись (без генерации событий).
	 * <p>
	 * Служебный метод.
	 * <p>
	 * @param entry кэш-запись
	 */
	void set(SecurityEntry entry) {
		SecurityDescriptor descr = entry.getDescriptor();
		short2descr.put(entry.getShortName(), descr);
		long2descr.put(descr.getCode() + SEP + descr.getClassCode(), descr);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != DescriptorsCache.class ) {
			return false;
		}
		DescriptorsCache o = (DescriptorsCache) other;
		return new EqualsBuilder()
			.append(o.dispatcher, dispatcher)
			.append(o.long2descr, long2descr)
			.append(o.onUpdate, onUpdate)
			.append(o.short2descr, short2descr)
			.isEquals();
	}

}
