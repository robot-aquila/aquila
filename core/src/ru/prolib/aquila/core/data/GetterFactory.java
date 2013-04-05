package ru.prolib.aquila.core.data;

import java.util.Date;
import java.util.Map;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.OrderFactory;
import ru.prolib.aquila.core.utils.Validator;

/**
 * Интерфейс фабрики геттеров.
 * <p>
 * 2012-10-19<br>
 * $Id: GetterFactory.java 527 2013-02-14 15:14:09Z whirlwind $
 */
@Deprecated
public interface GetterFactory {
	
	/**
	 * Создать геттер счета.
	 * <p>
	 * Создает геттер счета на основании значения указанного элемента ряда.
	 * <p>
	 * @param column идентификатор элемента ряда с кодом счета
	 * @return геттер
	 */
	public G<Account> rowAccount(String column);
	
	/**
	 * Создать геттер счета.
	 * <p>
	 * @param code идентификатор кода счета
	 * @param subCode идентификатор суб-кода счета
	 * @return геттер
	 */
	public G<Account> rowAccount(String code, String subCode);

	/**
	 * Создать условный геттер целого.
	 * <p>
	 * Использует валидатор для выбора одного из двух геттеров целочисленного
	 * значения. Делегирует получение значения соответствующему геттеру.
	 * <p>
	 * @param validator валидатор условия
	 * @param onValid геттер в случае исполнения условия
	 * @param onInvalid геттер в случае неисполнения условия
	 * @return геттер
	 */
	public G<Integer> condInteger(Validator validator, G<Integer> onValid,
			G<Integer> onInvalid);
	
	/**
	 * Создать условный геттер целого.
	 * <p>
	 * Использует валидатор для выбора одного из двух целочисленных значений.
	 * <p>
	 * @param validator валидатор условия
	 * @param onValid результат в случае исполнения условия
	 * @param onInvalid результат в случае неисполнения условия
	 * @return геттер
	 */
	public G<Integer> condInteger(Validator validator, int onValid,
			int onInvalid);
	
	/**
	 * Создать условный геттер целого.
	 * <p>
	 * Создает условный геттер целого на основании сравнения значения
	 * элемента ряда с заданным объектом. Выбирает значение для возврата на
	 * основании результата сравнения. Если в геттер передан не ряд, ряд не
	 * содержит элемента с таким именем, то возвращает null. 
	 * <p>
	 * @param column идентификатор элемента ряда
	 * @param expected ожидаемое значение
	 * @param onEquals результат в случае совпадения с ожидаемым значением
	 * @param onNotEquals результат в случае несовпадения
	 * @return геттер
	 */
	public G<Integer> rowCondInteger(String column, Object expected,
			int onEquals, int onNotEquals);

	/**
	 * Создать геттер целого.
	 * <p>
	 * Создает геттер целого типа {@link java.lang.Long Long} на основании
	 * значения элемента ряда. Если передан не ряд, ряд не содержит элемента
	 * с указанным именем или значение элемента не является целым, то
	 * возвращается null. 
	 * <p>
	 * @param column идентификатор элемента ряда
	 * @return геттер
	 */
	public G<Long> rowLong(String column);
	
	
	/**
	 * Создать геттер вещественного.
	 * <p>
	 * @param column идентификатор элемента ряда
	 * @return геттер вещественного
	 */
	public G<Double> rowDouble(String column);

	/**
	 * Создать геттер целого.
	 * <p>
	 * @param column идентификатор элемента ряда
	 * @return геттер целого
	 */
	public G<Integer> rowInteger(String column);
	
	/**
	 * Создать геттер строки.
	 * <p>
	 * @param column идентификатор элемента ряда
	 * @return геттер строки
	 */
	public G<String> rowString(String column);
	
	/**
	 * Создать геттер объекта.
	 * <p>
	 * @param column идентификатор элемента ряда
	 * @return геттер объекта
	 */
	public G<Object> rowObject(String column);
	
	/**
	 * Создать геттер дескриптора инструмента.
	 * <p>
	 * Данный метод предназначен для создания геттера дескриптора инструмента
	 * с переменными значениями кода инструмента и кода класса инструмента,
	 * которые извлекаются из соответствующих колонок ряда, и фиксированными
	 * предустановленными значениями кода валюты и типа инструмента.
	 * <p>
	 * @param colCode идентификатор элемента ряда с кодом инструмента
	 * @param colClassCode идентификатор элемента ряда с кодом класса
	 * @param valCurrCode предустановленное <b>ЗНАЧЕНИЕ</b> кода валюты
	 * @param valSecType предустановленное <b>ЗНАЧЕНИЕ</b> типа инструмента
	 * @return геттер дескриптора инструмента
	 */
	public G<SecurityDescriptor> rowSecurityDescr(String colCode,
			String colClassCode, String valCurrCode, SecurityType valSecType);
	
	/**
	 * Создать геттер даты.
	 * <p>
	 * Создает геттер, типа {@link GDate2E}. Форматы даты и времени
	 * должны соответствовать требованиям класса
	 * {@link java.text.SimpleDateFormat SimpleDateFormat}.
	 * <p>
	 * @param date идентификатор элемента ряда строки даты
	 * @param time идентификатор элемента ряда строки времени
	 * @param dateFormat формат даты
	 * @param timeFormat формат времени
	 * @return геттер даты
	 */
	public G<Date> rowDate(String date, String time, String dateFormat,
			String timeFormat);
	
	/**
	 * Создать геттер даты.
	 * <p>
	 * @param column идентификатор элемента ряда
	 * @return геттер даты
	 */
	public G<Date> rowDate(String column);
	
	/**
	 * Создать геттер направления операции.
	 * <p>
	 * @param column идентификатор элемента ряда
	 * @param buyEquiv значение элемента, соответствующее покупке
	 * @return геттер направления
	 */
	public G<OrderDirection> rowOrderDir(String column, Object buyEquiv);
	
	/**
	 * Создать геттер константной строки.
	 * <p>
	 * @param value возвращаемое значение
	 * @return геттер
	 */
	public G<String> constString(String value);
	
	/**
	 * Создать геттер константного объекта.
	 * <p>
	 * @param value возвращаемое значение
	 * @return геттер
	 */
	public G<Object> constObject(Object value);
	
	/**
	 * Создать геттер портфеля.
	 * <p>
	 * @param portfolios набор портфелей
	 * @param gAccount геттер счета портфеля
	 * @return геттер портфеля
	 */
	public G<Portfolio> portfolio(Portfolios portfolios, G<Account> gAccount);
	
	/**
	 * Создать геттер портфеля.
	 * <p>
	 * Используется для портфелей, счет которых идентифицируется одним кодом.
	 * <p>
	 * @param portfolios набор портфелей
	 * @param code идентификатор элемента с кодом счета портфеля
	 * @return геттер портфеля
	 */
	public G<Portfolio> rowPortfolio(Portfolios portfolios, String code);
	
	/**
	 * Создать геттер портфеля.
	 * <p>
	 * Используется для портфелей, счет которых идентифицируется двумя кодами.
	 * <p>
	 * @param portfolios набор портфелей
	 * @param code идентификатор элемента с кодом счета портфеля
	 * @param subCode идентификатор ряда с суб-кодом счета портфеля
	 * @return геттер портфеля
	 */
	public G<Portfolio>
			rowPortfolio(Portfolios portfolios, String code, String subCode);
	
	/**
	 * Создать геттер инструмента.
	 * <p>
	 * @param securities набор инструментов
	 * @param gDescr геттер дескриптора инструмента
	 * @return геттер инструмента
	 */
	public G<Security>
			security(Securities securities, G<SecurityDescriptor> gDescr);
	
	/**
	 * Создать геттер инструмента.
	 * <p>
	 * Данный метод предназначен для создания геттера инструмента
	 * с переменными значениями кода инструмента и кода класса инструмента,
	 * которые извлекаются из соответствующих колонок ряда, и фиксированными
	 * предустановленными значениями кода валюты и типа инструмента.
	 * <p>
	 * @param securities набор инструментов
	 * @param colCode идентификатор элемента ряда кода инструмента
	 * @param colClassCode идентификатор элемента ряда класса инструмента
	 * @param valCurrCode предустановленное <b>ЗНАЧЕНИЕ</b> кода валюты
	 * @param valSecType предустановленное <b>ЗНАЧЕНИЕ</b> типа инструмента
	 * @return геттер инструмента
	 */
	public G<Security> rowSecurity(Securities securities, String colCode,
			String colClassCode, String valCurrCode, SecurityType valSecType);
	
	/**
	 * Создать геттер цены.
	 * <p>
	 * @param price идентификатор элемента ряда значения цены
	 * @param unit идентификатор элемента ряда единицой измерения цены
	 * @param map карта соответствия значений элемента ряда и единицы
	 * @return геттер цены
	 */
	public G<Price>
			rowPrice(String price, String unit, Map<?, PriceUnit> map);
	
	/**
	 * Создать геттер заявки.
	 * <p>
	 * @param orders хранилище заявок
	 * @param factory фабрика заявок
	 * @param transId идентификатор элемента ряда номера транзакции
	 * @param id идентификатор элемента ряда номера заявки
	 * @return геттер заявки
	 */
	public G<EditableOrder> rowOrder(EditableOrders orders,
			OrderFactory factory, String transId, String id);

}
