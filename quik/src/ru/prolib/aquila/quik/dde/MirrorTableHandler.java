package ru.prolib.aquila.quik.dde;

import java.util.*;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.data.row.*;
import ru.prolib.aquila.dde.*;
import ru.prolib.aquila.dde.utils.table.*;

/**
 * Базовый обработчик кэширования таблицы типа зеркало.
 */
public class MirrorTableHandler implements DDETableHandler {
	public static final Map<String, Integer> EMPTY_MAP;
	private Map<String, Integer> headers;
	private final CacheGateway gateway;
	private final DDEUtils ddeUtils;
	
	static {
		EMPTY_MAP = new Hashtable<String, Integer>();
	}
	
	public MirrorTableHandler(CacheGateway gateway) {
		super();
		ddeUtils = new DDEUtils();
		headers = EMPTY_MAP;
		this.gateway = gateway;
	}
	
	/**
	 * Получить текущую карту заголовков.
	 * <p>
	 * Только для тестов.
	 * <p>
	 * @return карта заголовков
	 */
	protected Map<String, Integer> getCurrentHeadersMap() {
		return headers; 
	}
	
	/**
	 * Получить шлюз кэша.
	 * <p>
	 * @return шлюз
	 */
	public CacheGateway getCacheGateway() {
		return gateway;
	}

	@Override
	public synchronized void handle(DDETable ddeTable) throws DDEException {
		try {
			handleTable(ddeTable);
		} catch ( RowException e ) {
			throw new DDEException(e);
		}
	}
	
	/**
	 * Обработать данные таблицы.
	 * <p>
	 * @param ddeTable таблица
	 * @throws DDEException ошибка уровня конвертации данных
	 * @throws RowSetException ошибка позицирования ряда
	 */
	private void handleTable(DDETable ddeTable)
			throws DDEException, RowSetException
	{
		DDETableRange range = ddeUtils.parseXltRange(ddeTable);
		int firstRow = 0;
		if ( range.getFirstRow() == 1 ) {
			firstRow = 1;
			gateway.clearCache();
			headers.clear();
			headers = ddeUtils.makeHeadersMap(ddeTable,
					gateway.getRequiredHeaders());
		}
		RowSet rs = new DDETableRowSet(ddeTable, headers, firstRow);
		while ( rs.next() ) {
			gateway.toCache(rs);
		}
		gateway.fireUpdateCache();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() == MirrorTableHandler.class ) {
			MirrorTableHandler o = (MirrorTableHandler) other;
			return new EqualsBuilder()
				.append(gateway, o.gateway)
				.append(headers, o.headers)
				.isEquals();
		} else {
			return false;
		}
	}

}
