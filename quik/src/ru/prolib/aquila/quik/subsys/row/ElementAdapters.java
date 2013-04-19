package ru.prolib.aquila.quik.subsys.row;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.getter.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.data.getter.*;
import ru.prolib.aquila.core.data.getter.GDate2E;
import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.quik.subsys.*;
import ru.prolib.aquila.quik.subsys.getter.*;

/**
 * Конструктор адаптеров элементов ряда.
 * <p>
 * 2013-02-16<br>
 * $Id$
 */
public class ElementAdapters {
	private final QUIKServiceLocator locator;
	private final String msgPrefix;
	
	public ElementAdapters(QUIKServiceLocator locator, String msgPrefix) {
		super();
		this.locator = locator;
		this.msgPrefix = msgPrefix;
	}

	/**
	 * Получить сервис-локатор.
	 * <p>
	 * @return сервис-локатор
	 */
	public QUIKServiceLocator getServiceLocator() {
		return locator;
	}
	
	/**
	 * Получить префикс сообщения об ошибке.
	 * <p>
	 * @return префикс сообщения
	 */
	public String getMessagePrefix() {
		return msgPrefix;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other != null && other.getClass() == ElementAdapters.class ) {
			ElementAdapters o = (ElementAdapters) other;
			return new EqualsBuilder()
				.append(locator, o.locator)
				.append(msgPrefix, o.msgPrefix)
				.isEquals();
		} else {
			return false;
		}
	}
	
	/**
	 * Создать адаптер направления заявки.
	 * <p>
	 * @param elementId элемент ряда, соответствующий строке направления 
	 * @param buyEquiv строка-эквивалент покупки
	 * @param sellEquiv строка-эквивалент продажи
	 * @return адаптер элемента ряда
	 */
	@SuppressWarnings("unchecked")
	public G<OrderDirection> createOrderDir(String elementId,
			String buyEquiv, String sellEquiv)
	{
		return new GOrderDir(locator.getTerminal(),
				re(elementId, String.class),
				buyEquiv, sellEquiv, msgPrefix);
	}
	
	/**
	 * Создать адаптер вещественного к целому.
	 * <p>
	 * Создает адаптер со строгим характером поведения.
	 * <p>
	 * @param elementId элемент ряда, соответствующий вещественному значению
	 * @return адаптер элемента ряда
	 */
	public G<Long> createLong(String elementId) {
		return createLong(elementId, true);
	}

	/**
	 * Создать адаптер вещественного к целому.
	 * <p>
	 * @param elementId элемент ряда, соответствующий вещественному значению
	 * @param strict характер поведения: true - строгий, false - нестрогий
	 * @return адаптер
	 */
	@SuppressWarnings("unchecked")
	public G<Long> createLong(String elementId, boolean strict) {
		return new GDouble2Long(locator.getTerminal(),
				re(elementId, Double.class),
				strict, msgPrefix);		
	}
	
	/**
	 * Создать адаптер вещественного к целому.
	 * <p>
	 * Создает адаптер со строгим характером поведения.
	 * <p>
	 * @param elementId элемент ряда, соответствующий вещественному значению
	 * @return адаптер элемента ряда
	 */
	public G<Integer> createInteger(String elementId) {
		return createInteger(elementId, true);
	}

	/**
	 * Создать адаптер вещественного к целому.
	 * <p>
	 * @param elementId элемент ряда, соответствующий вещественному значению
	 * @param strict характер поведения: true - строгий, false - нестрогий
	 * @return адаптер
	 */
	@SuppressWarnings("unchecked")
	public G<Integer> createInteger(String elementId, boolean strict) {
		return new GDouble2Int(locator.getTerminal(),
				re(elementId, Double.class),
				strict, msgPrefix);		
	}
	
	/**
	 * Создать адаптер дескриптора инструмента.
	 * <p>
	 * @param codeId элемент ряда, соответствующий коду инструмента
	 * @param classId элемент ряда, соответствующий коду класса инструмента
	 * @param currencyId элемент ряда, соответствующий коду валюты
	 * @param defaultCurrency код валюты по-умолчанию
	 * @param typeId элемент ряда, соответствующий строке типа инструмента
	 * @param map карта соответствия строкового представления типа объекту типа
	 * @return адаптер
	 */
	@SuppressWarnings("unchecked")
	public G<SecurityDescriptor> createSecDescr(String codeId, String classId,
			String currencyId, String defaultCurrency,
			String typeId, Map<String, SecurityType> map)
	{
		return new GSecurityDescr(re(codeId, String.class),
			re(classId, String.class),
			new QUIKGetCurrency(re(currencyId, String.class), defaultCurrency),
			createStringMap(typeId, map, true, SecurityType.STK));
	}
	
	/**
	 * Создать адаптер-валидатор определенного вещественного числа.
	 * <p>
	 * @param elementId элемент ряда, соответствующий вещественному значению
	 * @return адаптер элемента ряда
	 */
	@SuppressWarnings("unchecked")
	public G<Double> createDouble(String elementId) {
		return new GNotNull<Double>(locator.getTerminal(),
				re(elementId, Double.class),
				msgPrefix);
	}
	
	/**
	 * Создать адаптер дескриптора инструмента из наименования.
	 * <p> 
	 * @param nameId элемент ряда, соответствующий наименованию инструмента
	 * @return адаптер
	 */
	@SuppressWarnings("unchecked")
	public G<SecurityDescriptor> createSecDescr(String nameId) {
		return new QUIKGetSecurityDescriptor1(locator,
				re(nameId, String.class));
	}
	
	/**
	 * Создать адаптер дескриптора инструмента из кода и класса инструмента.
	 * <p>
	 * @param codeId элемент ряда, соответствующий коду инструмента
	 * @param classId элемент ряда, соответствующий коду класса инструмента
	 * @return адаптер
	 */
	@SuppressWarnings("unchecked")
	public G<SecurityDescriptor> createSecDescr(String codeId, String classId) {
		return new QUIKGetSecurityDescriptor2(locator,
				re(codeId, String.class),
				re(classId, String.class));
	}
	
	/**
	 * Создать адаптер двух строк даты и времени в объект типа
	 * {@link java.util.Date Date}.
	 * <p>
	 * @param dateId элемент ряда, соответствующий строке даты
	 * @param timeId элемент ряда, соответствующий строке времени
	 * @param dateFormat формат даты или null для дефолтного формата
	 * @param timeFormat формат времени или null для дефолтного формата
	 * @param strict характер поведения: true - строгий, false - нестрогий
	 * @return адаптер
	 */
	@SuppressWarnings("unchecked")
	public G<Date> createDate(String dateId, String timeId,
			String dateFormat, String timeFormat, boolean strict)
	{
		if ( dateFormat == null ) {
			dateFormat = ((SimpleDateFormat) DateFormat.getDateInstance())
				.toPattern();
		}
		if ( timeFormat == null ) {
			timeFormat = ((SimpleDateFormat) DateFormat.getTimeInstance())
				.toPattern();
		}
		return new GDate2E(locator.getTerminal(), strict,
				re(dateId, String.class),
				re(timeId, String.class),
				dateFormat, timeFormat, msgPrefix);
	}
	
	/**
	 * Создать адаптер двух строк в объект типа
	 * {@link ru.prolib.aquila.core.BusinessEntities.Account Account}.
	 * <p>
	 * @param codeId элемент ряда, соответствующий коду счета
	 * @param subCodeId элемент ряда, соответствующий суб-коду счета
	 * @return адаптер
	 */
	@SuppressWarnings("unchecked")
	public G<Account> createAccount(String codeId, String subCodeId) {
		EditableTerminal term = locator.getTerminal();
		return new GAccount(
			new GNotNull<String>(term, re(codeId, String.class), msgPrefix),
			new GNotNull<String>(term, re(subCodeId, String.class), msgPrefix));
	}
	
	/**
	 * Создать адаптер двух строк в объект типа
	 * {@link ru.prolib.aquila.core.BusinessEntities.Account Account} с
	 * проверкой наличия соответствующего портфеля.
	 * <p>
	 * @param codeId элемент ряда, соответствующий коду счета
	 * @param subCodeId элемент ряда, соответствующий суб-коду счета
	 * @return адаптер
	 */
	public G<Account>
		createAccountAndCheckExists(String codeId, String subCodeId)
	{
		return new GAccountExists(locator.getTerminal(),
				createAccount(codeId, subCodeId), msgPrefix);
	}
	
	/**
	 * Создать адаптер трех строк в объект типа
	 * {@link ru.prolib.aquila.core.BusinessEntities.Account Account}.
	 * <p>
	 * @param codeId элемент ряда, соответствующий коду счета
	 * @param subCodeId элемент ряда, соответствующий суб-коду счета
	 * @param subCode2Id элемент ряда, соответствующий вторичному суб-коду
	 * @return адаптер
	 */
	@SuppressWarnings("unchecked")
	public G<Account>
		createAccount(String codeId, String subCodeId, String subCode2Id)
	{
		EditableTerminal term = locator.getTerminal();
		return new GAccount(
			new GNotNull<String>(term, re(codeId, String.class), msgPrefix),
			new GNotNull<String>(term, re(subCodeId, String.class), msgPrefix),
			new GNotNull<String>(term, re(subCode2Id, String.class),msgPrefix));
	}
	
	/**
	 * Создать адаптер трех строк в объект типа
	 * {@link ru.prolib.aquila.core.BusinessEntities.Account Account} с
	 * проверкой наличия соответствующего портфеля.
	 * <p>
	 * @param codeId элемент ряда, соответствующий коду счета
	 * @param subCodeId элемент ряда, соответствующий суб-коду счета
	 * @param subCode2Id элемент ряда, соответствующий вторичному суб-коду
	 * @return адаптер
	 */
	public G<Account> createAccountAndCheckExists(String codeId,
			String subCodeId, String subCode2Id)
	{
		return new GAccountExists(locator.getTerminal(),
				createAccount(codeId, subCodeId, subCode2Id), msgPrefix);
	}
	
	/**
	 * Создать адаптер объекта счета для таблиц заявок и стоп-заявок.
	 * <p>
	 * Восстанавливает объект типа
	 * {@link ru.prolib.aquila.core.BusinessEntities.Account Account} из
	 * доступных в таблицах заявок кода клиента и кода счета.
	 * <p>
	 * @param subCodeId элемент ряда, соответствующий суб-коду счета
	 * @param subCode2Id элемент ряда, соответствующий вторичному суб-коду
	 * @return адаптер
	 */
	@SuppressWarnings("unchecked")
	public G<Account> createOrderAccount(String subCodeId, String subCode2Id) {
		return new QUIKGetOrderAccount(locator,
				re(subCodeId, String.class),
				re(subCode2Id, String.class));
	}
	
	/**
	 * Создать транслятор строки.
	 * <p>
	 * Обеспечивает доступ к строковому значению исходного ряда.
	 * <p>
	 * @param elementId элемент исходного ряда
	 * @return адаптер
	 */
	@SuppressWarnings("unchecked")
	public G<String> createString(String elementId) {
		return new GNotNull<String>(locator.getTerminal(),
				re(elementId, String.class), msgPrefix);
	}
	
	/**
	 * Создать карту соответствий для строкового элемента.
	 * <p>
	 * Создает геттер соответствия по карте исходя из значения строкового
	 * элемента ряда.
	 * <p>
	 * @param elementId идентификатор ключевого элемента ряда 
	 * @param map карта соответствия значений
	 * @param strict характер поведения: true - строгий, false - нестрогий
	 * @param defaultValue значение по-умолчанию
	 * @return адаптер
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public G createStringMap(String elementId, Map<String, ?> map,
			boolean strict, Object defaultValue)
	{
		return new GStringMap(locator.getTerminal(),
				re(elementId, String.class),
				map, defaultValue, strict, msgPrefix);
	}
	
	/**
	 * Создать карту соответствий строкового элемента геттеру.
	 * <p>
	 * @param elementId идентификатор ключевого элемента ряда
	 * @param map карта соответствия значений геттерам
	 * @param defaultValue значение по-умолчанию
	 * @return адаптер
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public G createStringMap2G(String elementId, Map<String, ?> map,
			Object defaultValue)
	{
		return new GStringMap2G(new GNotNull<String>(locator.getTerminal(),
				re(elementId, String.class), msgPrefix),
				map, defaultValue);
	}
	
	@SuppressWarnings("unchecked")
	public G<OrderType>
		createOrderType(String elementId, Map<String, OrderType> map)
	{
		return new GStringMap<OrderType>(locator.getTerminal(),
			new GNotNull<String>(locator.getTerminal(),
				new QUIKGetOrderTypeCode(re(elementId, String.class)),
				msgPrefix),
			map, OrderType.OTHER, true, msgPrefix);
	}
	
	@SuppressWarnings("unchecked")
	public G<Price>
		createPrice(String priceId, String unitId, Map<String, PriceUnit> map)
	{
		return new GPrice(re(priceId, Double.class),
				createStringMap(unitId, map, false, null));
	}
	
	/**
	 * Создать геттер элемента ряда.
	 * <p>
	 * @param elementId идентификатор элемента ряда
	 * @param elementClass ожидаемый класс элемента
	 * @return адаптер
	 */
	public G<?> createElement(String elementId, Class<?> elementClass) {
		return re(elementId, elementClass);
	}
	
	/**
	 * Создать геттер элемента ряда.
	 * <p>
	 * @param elementId идентификатор элемента ряда
	 * @param elementClass ожидаемый класс элемента 
	 * @return геттер элемента
	 */
	private RowElement re(String elementId, Class<?> elementClass) {
		return new RowElement(elementId, elementClass);
	}

}
