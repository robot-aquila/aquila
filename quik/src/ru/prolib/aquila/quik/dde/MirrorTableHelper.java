package ru.prolib.aquila.quik.dde;

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
 * Помошник обработчика зеркалирования таблицы.
 */
public class MirrorTableHelper {
	private final CacheGateway gateway;
	private final DDEUtils utils;
	private Map<String, Integer> headers;
	private Map<Integer, Object> keyValues;
	
	public MirrorTableHelper(CacheGateway gateway, DDEUtils utils) {
		super();
		this.gateway = gateway;
		this.utils = utils;
		headers = new Hashtable<String, Integer>();
		keyValues = new Hashtable<Integer, Object>();
	}
	
	public MirrorTableHelper(CacheGateway gateway) {
		this(gateway, new DDEUtils());
	}
	
	/**
	 * Получить шлюз кэша.
	 * <p>
	 * @return шлюз кэша
	 */
	public CacheGateway getCacheGateway() {
		return gateway;
	}
	
	/**
	 * Получить DDE-утилиты.
	 * <p>
	 * @return утилиты
	 */
	public DDEUtils getDdeUtils() {
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
	public synchronized Map<String, Integer> getHeaders() {
		return headers;
	}
	
	/**
	 * Установить карту текущих заголовков.
	 * <p>
	 * @param headers новая карта заголовков
	 */
	public synchronized void setHeaders(Map<String, Integer> headers) {
		this.headers = headers;
	}
	
	/**
	 * Получить карту ключевых значений рядов.
	 * <p>
	 * Карта ключевых значений отражает идентификаторы ряда на соответствующие
	 * номеря рядов оригинальной таблицы. Если в момент обновления ключевое
	 * значение ряда с определенным номером ряда перестает соответствовать
	 * ранее зафиксированному значению, значит ряды таблицы теперь представляют
	 * иную информацию, нежели это было раньше. Это означает, что кэш становится
	 * недействительным и его нужно полностью очистить.
	 * <p>
	 * @return карта ключевого значения номеру ряда исходной таблицы
	 */
	public synchronized Map<Integer, Object> getKeyValues() {
		return keyValues;
	}
	
	/**
	 * Установить карту ключевых значений рядов.
	 * <p>
	 * @param keyValues карта ключевых значений
	 */
	public synchronized void setKeyValues(Map<Integer, Object> keyValues) {
		this.keyValues = keyValues;
	}
	
	/**
	 * Создать дескриптор области таблицы.
	 * <p>
	 * @param table область таблицы
	 * @return дескриптор
	 * @throws XltItemFormatException ошибка разбора строки item
	 */
	public TableMeta createTableMeta(DDETable table)
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
	public synchronized void updateHeaders(DDETable table)
			throws NotAllRequiredFieldsException
	{
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
	public RowSet createRowSet(TableMeta meta, DDETable table) {
		return new DDETableRowSet(table, headers, meta.hasHeaderRow() ? 1 : 0);
	}
	
	/**
	 * Проверить набор рядов на соответствие закешированным данным.
	 * <p>
	 * В данном методе выполняется проверка на соответствие ряда данных из
	 * набора соответствующему ряду в кэше. Соответствие определяется по номеру
	 * ряда в соответствие с дескриптором области. Если ключевой признак
	 * закешированной записи не соответствует ключевому признаку
	 * соответствующего ряда данных, то весь кэш объявляется недействительным.
	 * Отсутствие ключевого значения в карте ключевых значений свидетельствует
	 * о новом ряде и не влияет на принятие решения.
	 * <p>
	 * @param meta дескриптор области
	 * @param rs набор рядов
	 * @throws RowSetException ошибка позицирования/доступа к элементу ряда
	 * @throws DDEException ошибка получения ключевого значения
	 */
	public synchronized void checkRowSetChanged(TableMeta meta, RowSet rs)
			throws RowSetException, DDEException
	{
		if ( ! meta.hasDataRows() ) {
			return;
		}
		int rowNumber = meta.getDataFirstRowNumber();
		while ( rs.next() ) {
			if ( keyValues.containsKey(rowNumber) ) {
				Object keyValue = gateway.getKeyValue(rs);
				if ( ! keyValue.equals(keyValues.get(rowNumber)) ) {
					gateway.clearCache();
					keyValues.clear();
					break;
				}
			}
			rowNumber ++;
		}
		rs.reset();
	}
	
	/**
	 * Закешировать ряды таблицы.
	 * <p>
	 * @param meta дескриптор области таблицы
	 * @param rs набор рядов
	 * @throws RowSetException ошибка позицирования/доступа к элементу ряда 
	 * @throws DDEException  ошибка конвертирования/получения ключевого значения
	 */
	public synchronized void cacheRowSet(TableMeta meta, RowSet rs)
			throws RowSetException, DDEException
	{
		if ( ! meta.hasDataRows() ) {
			return;
		}
		int rowNumber = meta.getDataFirstRowNumber();
		while ( rs.next() ) {
			gateway.toCache(rs);
			keyValues.put(rowNumber, gateway.getKeyValue(rs));
			rowNumber ++;
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
		if ( other.getClass() != MirrorTableHelper.class ) {
			return false;
		}
		MirrorTableHelper o = (MirrorTableHelper) other;
		return new EqualsBuilder()
			.append(gateway, o.gateway)
			.append(utils, o.utils)
			.append(headers, o.headers)
			.append(keyValues, o.keyValues)
			.isEquals();
	}

}
