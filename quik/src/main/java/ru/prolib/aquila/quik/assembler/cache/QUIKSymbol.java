package ru.prolib.aquila.quik.assembler.cache;

import java.util.Currency;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Дескриптор инструмента с расширенным набором атрибутов.
 * <p>
 * Полная идентификация инструмента возможна только на основании данных таблицы
 * текущих параметров. Иные таблицы QUIK содержат или неполные идентифицирующие
 * инструмент данные, или атрибуты, позволяющие ссылаться на инструмент лишь
 * косвенно. Для каждого типа таблицы, транслируемой из QUIK, способ связывания
 * с инструментом различен. Это связано с наборами доступных полей импортируемых
 * таблиц.
 * <p>
 * Кроме того, в некоторых случаях значения атрибутов для косвенных ссылок имеют
 * ограниченный срок действия и совпадают для различных инструментов. Например,
 * для рынка ФОРТС коды фьючерсов будут периодически повторяться, хотя
 * подразумевают под собой разные фьючерсы (например RIZ3 для RTS-12.13,
 * RTS-12.3 и т.п.). Однако, нельзя просто заменить краткий код на полный, ведь
 * для подачи поручений требуется именно краткий код. 
 * <p>
 * Для того, что бы хоть как-то облегчить ситуацию со связыванием данных,
 * был создан данный класс. Он расширяет атрибуты стандартного дескриптора,
 * делая возможным получить соответствующий способу связывания специфический
 * атрибут непосредственно через дескриптор инструмента.
 */
public class QUIKSymbol extends Symbol {
	private final String systemCode, displayName, shortName;

	/**
	 * Конструктор.
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса
	 * @param currency валюта шага цены
	 * @param type тип инструмента
	 * @param systemCode системный код инструмента (используется для выставления
	 * заявок)
	 * @param shortName краткое наименование инструмента (используется для
	 * связывания строки таблицы позиций с инструментом)
	 * @param displayName полное наименование инструмента
	 */
	public QUIKSymbol(String code, String classCode,
			Currency currency, SymbolType type, String systemCode,
			String shortName, String displayName)
	{
		super(code, classCode, currency, type);
		this.systemCode = systemCode;
		this.displayName = displayName;
		this.shortName = shortName;
	}
	
	/**
	 * Получить системный код инструмента.
	 * <p>
	 * Системный код используется для связывания таблиц по паре системный код
	 * инструмента + класс инструмента, а так же при выставлении заявок.
	 * <p>
	 * @return системный код
	 */
	public String getSystemCode() {
		return systemCode;
	}
	
	/**
	 * Получить полное наименование инструмента.
	 * <p>
	 * Полное наименование инструмента используется для отображения в
	 * интерфейсе.
	 * <p>
	 * @return наименование
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * Получить краткое наименование инструмента.
	 * <p>
	 * Используется для связывания в таблицах, в которых фигурирует только
	 * краткое наименование инструмента. 
	 * <p>
	 * @return краткое наименование
	 */
	public String getShortName() {
		return shortName;
	}

}