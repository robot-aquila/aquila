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
public class SymbolsCache {
	/**
	 * Разделитель.
	 */
	private static final String SEP = "#";
	
	/**
	 * Список всех зарегистрированных дескрипторов.
	 */
	private final List<QUIKSymbol> symbols;
	
	/**
	 * Карта сопоставления краткого наименования дескриптору инструмента.
	 */
	private final Map<String, QUIKSymbol> short2symbol;
	
	/**
	 * Карта сопоставления комбинации системного кода инструмента и кода класса
	 * дескриптору инструмента.
	 */
	private final Map<String, QUIKSymbol> long2symbol;
	
	private final EventDispatcher dispatcher;
	private final EventTypeSI onUpdate; 
	
	public SymbolsCache(EventDispatcher dispatcher, EventTypeSI onUpdate) {
		super();
		symbols = new Vector<QUIKSymbol>();
		short2symbol = new LinkedHashMap<String, QUIKSymbol>();
		long2symbol = new Hashtable<String, QUIKSymbol>();
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
	 * @param symbol дескриптор
	 * @return возвращает true, если был зарегистрирован новый дескриптор
	 */
	public synchronized boolean put(QUIKSymbol symbol) {
		if ( symbols.contains(symbol) ) {
			return false;
		}
		set(symbol);
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
		QUIKSymbol get(String systemCode, String classCode)
	{
		return long2symbol.get(systemCode + SEP + classCode);
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
	public synchronized QUIKSymbol get(String shortName) {
		return short2symbol.get(shortName);
	}
	
	/**
	 * Получить список всех зарегистрированных дескрипторов.
	 * <p>
	 * @return список дескрипторов инструментов
	 */
	public synchronized List<QUIKSymbol> get() {
		return new Vector<QUIKSymbol>(symbols);
	}
	
	/**
	 * Сохранить дескриптор (без генерации событий).
	 * <p>
	 * Служебный метод.
	 * <p>
	 * @param d дескриптор
	 */
	void set(QUIKSymbol d) {
		symbols.add(d);
		short2symbol.put(d.getShortName(), d);
		long2symbol.put(d.getSystemCode() + SEP + d.getClassCode(), d);
	}
	
	@Override
	public synchronized boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SymbolsCache.class ) {
			return false;
		}
		SymbolsCache o = (SymbolsCache) other;
		return new EqualsBuilder()
			.append(o.symbols, symbols)
			.append(o.dispatcher, dispatcher)
			.append(o.long2symbol, long2symbol)
			.append(o.onUpdate, onUpdate)
			.append(o.short2symbol, short2symbol)
			.isEquals();
	}

}
