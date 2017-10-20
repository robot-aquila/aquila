package ru.prolib.aquila.quik.assembler.cache.dde;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.data.row.RowSet;
import ru.prolib.aquila.core.data.row.RowSetException;
import ru.prolib.aquila.dde.DDEException;
import ru.prolib.aquila.dde.DDETable;
import ru.prolib.aquila.dde.utils.table.DDETableRowSet;
import ru.prolib.aquila.dde.utils.table.DDEUtils;
import ru.prolib.aquila.dde.utils.table.NotAllRequiredFieldsException;
import ru.prolib.aquila.dde.utils.table.XltItemFormatException;

/**
 * Помощник обработчика таблицы.
 * <p>
 * Служебный класс, содержащий реализацию процедур обработки таблицы.
 * Универсальная реализация, позволяющая организовать любую схему обработки
 * набора рядов чере реализацию специфического шлюза. Может быть применен
 * как для тех таблиц, построчная обработка которых выполняется
 * непосредственно в момент получения данных (инструменты, портфели, позиции
 * и т.п), так и для объемных таблиц (все сделки), которые доолжны быть
 * помещены в очередь на обработку. В случае необходимости позволяет
 * использовать смешанный подход.
 */
class TableHelper {
	private final TableGateway gateway;
	private final DDEUtils utils;
	private Map<String, Integer> headers;
	
	/**
	 * Конструктор.
	 * <p>
	 * Только для тестирования.
	 * <p>
	 * @param gateway шлюк таблицы
	 * @param utils утилиты DDE
	 */
	TableHelper(TableGateway gateway, DDEUtils utils) {
		super();
		this.gateway = gateway;
		this.utils = utils;
		headers = new Hashtable<String, Integer>();
	}
	
	/**
	 * Конструктор (короткий вызов).
	 * <p>
	 * @param gateway шлюз таблицы
	 */
	TableHelper(TableGateway gateway) {
		this(gateway, new DDEUtils());
	}
	
	/**
	 * Получить шлюз таблицы.
	 * <p>
	 * Только для тестирования.
	 * <p>
	 * @return шлюз таблицы
	 */
	TableGateway getTableGateway() {
		return gateway;
	}
	
	/**
	 * Получить функции-утилиты.
	 * <p>
	 * Только для тестирования.
	 * <p>
	 * @return утилиты
	 */
	DDEUtils getDdeUtils() {
		return utils;
	}
	
	/**
	 * Получить карту текущих заголовков.
	 * <p>
	 * Карта заголовков отражает идентификаторы колонок на соответствующие им
	 * индексы колонок.
	 * <p>
	 * @return карта заголовков
	 */
	Map<String, Integer> getHeaders() {
		return headers;
	}
	
	/**
	 * Установить карту текущих заголовков.
	 * <p>
	 * Служебный метод. Только для тестирования.
	 * <p>
	 * @param headers новая карта заголовков
	 */
	void setHeaders(Map<String, Integer> headers) {
		this.headers = headers;
	}
	
	/**
	 * Создать дескриптор области таблицы.
	 * <p>
	 * @param table область таблицы
	 * @return дескриптор
	 * @throws XltItemFormatException ошибка разбора строки item
	 */
	TableMeta createTableMeta(DDETable table)
			throws XltItemFormatException
	{
		return new TableMeta(utils.parseXltRange(table));
	}
	
	/**
	 * Обновить заголовки.
	 * <p>
	 * Обновляет заголовки на основе первой строки области таблицы.
	 * <p>
	 * @param table область таблицы
	 * @throws NotAllRequiredFieldsException не все необходимые поля доступны
	 */
	void updateHeaders(DDETable table) throws NotAllRequiredFieldsException {
		headers.clear();
		headers = utils.makeHeadersMap(table, gateway.getRequiredHeaders());
	}
	
	/**
	 * Создать набор рядов данных на основе области таблицы.
	 * <p> 
	 * @param meta дескриптор области
	 * @param table область таблицы
	 * @return набор рядов
	 */
	RowSet createRowSet(TableMeta meta, DDETable table) {
		return new DDETableRowSet(table, headers, meta.hasHeaderRow() ? 1 : 0);
	}
	
	/**
	 * Обработать ряды таблицы.
	 * <p>
	 * @param meta дескриптор области таблицы
	 * @param rs набор рядов
	 * @throws RowSetException ошибка позицирования/доступа к элементу ряда 
	 * @throws DDEException  ошибка конвертирования/получения ключевого значения
	 */
	void process(TableMeta meta, RowSet rs)
			throws RowSetException, DDEException
	{
		if ( ! meta.hasDataRows() ) {
			return;
		}
		if ( gateway.shouldProcessRowByRow(meta, rs) ) {
			while ( rs.next() ) {
				if ( gateway.shouldProcess(rs) ) {
					gateway.process(rs);
				}
			}			
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != TableHelper.class ) {
			return false;
		}
		TableHelper o = (TableHelper) other;
		return new EqualsBuilder()
			.append(gateway, o.gateway)
			.append(utils, o.utils)
			.append(headers, o.headers)
			.isEquals();
	}

}
