package ru.prolib.aquila.quik.assembler.cache;

import java.util.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.*;

/**
 * Кэш идентификации инструментов.
 * <p>
 * Обеспечивает доступ к дескрипторам инструментов по неполным
 * идентификационным данным, которые транслируются терминалом QUIK при передаче
 * связанных таблиц. Позволяет отслеживать изменения содержимого кэша через
 * прослушивание события. 
 */
public class DescriptorsCache {
	/**
	 * Разделитель.
	 */
	private static final String SEP = "#";
	
	/**
	 * Список всех зарегистрированных дескрипторов.
	 */
	private final List<QUIKSecurityDescriptor> descrList;
	
	/**
	 * Карта сопоставления краткого наименования дескриптору инструмента.
	 */
	private final Map<String, QUIKSecurityDescriptor> short2descr;
	
	/**
	 * Карта сопоставления комбинации системного кода инструмента и кода класса
	 * дескриптору инструмента.
	 */
	private final Map<String, QUIKSecurityDescriptor> long2descr;
	
	private final EventDispatcher dispatcher;
	private final EventTypeSI onUpdate; 
	
	public DescriptorsCache(EventDispatcher dispatcher, EventTypeSI onUpdate) {
		super();
		descrList = new Vector<QUIKSecurityDescriptor>();
		short2descr = new LinkedHashMap<String, QUIKSecurityDescriptor>();
		long2descr = new Hashtable<String, QUIKSecurityDescriptor>();
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
	 * Зарегистрировать дескриптор.
	 * <p>
	 * Регистрирует дескриптор для последующего доступа по неполной
	 * идентификации. Уникальность дескриптора определяется комбинацией всех
	 * его атрибутов, включая расширенные. Это означает, что например фьючерсы 
	 * разных годов с совпадающими системными кодами будут признаны в рамках 
	 * кэша различными.
	 * <p>
	 * Если дескриптор уже зарегистрирован, то никаких изменений не выполняется. 
	 * Если передается новый дескриптор, то выполняется обновление информации
	 * о связях по неполным идентификационным данным с последующей генерацией
	 * о регистрации нового дескриптора.
	 * <p>
	 * @param descr дескриптор
	 * @return возвращает true, если был зарегистрирован новый дескриптор
	 */
	public synchronized boolean put(QUIKSecurityDescriptor descr) {
		if ( descrList.contains(descr) ) {
			return false;
		}
		set(descr);
		dispatcher.dispatch(new EventImpl(onUpdate));
		return true;
	}
	
	/**
	 * Получить дескриптор инструмента.
	 * <p>
	 * Позволяет получить дескриптор инструмента по неполным идентификационным
	 * данным: комбинации системного кода инструмента и кода класса инструмента.
	 * <p>
	 * @param systemCode системный код инструмента
	 * @param classCode код класса инструмента
	 * @return дескриптор инструмента или null, если нет такого инструмента
	 */
	public synchronized
		QUIKSecurityDescriptor get(String systemCode, String classCode)
	{
		return long2descr.get(systemCode + SEP + classCode);
	}
	
	/**
	 * Получить дескриптор инструмента.
	 * <p>
	 * Позволяет получить дескриптор инструмента по неполным идентификационным
	 * данным: краткому наименованию инструмента.
	 * <p>
	 * @param shortName краткое наименование инструмента
	 * @return дескриптор инструмента или null, если нет такого инструмента
	 */
	public synchronized QUIKSecurityDescriptor get(String shortName) {
		return short2descr.get(shortName);
	}
	
	/**
	 * Получить список всех зарегистрированных дескрипторов.
	 * <p>
	 * @return список дескрипторов инструментов
	 */
	public synchronized List<QUIKSecurityDescriptor> get() {
		return new Vector<QUIKSecurityDescriptor>(descrList);
	}
	
	/**
	 * Сохранить дескриптор (без генерации событий).
	 * <p>
	 * Служебный метод.
	 * <p>
	 * @param d дескриптор
	 */
	void set(QUIKSecurityDescriptor d) {
		descrList.add(d);
		short2descr.put(d.getShortName(), d);
		long2descr.put(d.getSystemCode() + SEP + d.getClassCode(), d);
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
			.append(o.descrList, descrList)
			.append(o.dispatcher, dispatcher)
			.append(o.long2descr, long2descr)
			.append(o.onUpdate, onUpdate)
			.append(o.short2descr, short2descr)
			.isEquals();
	}

}
