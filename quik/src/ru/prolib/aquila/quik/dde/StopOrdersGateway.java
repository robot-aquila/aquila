package ru.prolib.aquila.quik.dde;

import java.util.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.dde.DDEException;

/**
 * Шлюз к кэшу таблицы стоп-заявок.
 */
public class StopOrdersGateway implements CacheGateway {
	private static final String ID = "STOP_ORDERNUM";
	private static final String TRANS_ID = "TRANSID";
	private static final String STATUS = "STATUS";
	private static final String SEC_CODE = "SECCODE";
	private static final String SEC_CLASS = "CLASSCODE";
	private static final String ACCOUNT_CODE = "ACCOUNT";
	private static final String CLIENT_CODE = "CLIENTCODE";
	private static final String DIR = "BUYSELL";
	private static final String QTY = "QTY";
	private static final String PRICE = "PRICE";
	private static final String TYPE = "STOP_ORDERKIND";
	private static final String LINKED_ID = "LINKED_ORDER";
	private static final String PRICE1 = "CONDITION_PRICE";
	private static final String PRICE2 = "CONDITION_PRICE2";
	private static final String OFFSET = "OFFSET";
	private static final String OFFSET_UNITS = "OFFSET_UNITS";
	private static final String SPREAD = "SPREAD";
	private static final String SPREAD_UNITS = "SPREAD_UNITS";
	private static final String DATE = "STOP_ORDERDATE";
	private static final String TIME = "STOP_ORDERTIME";
	private static final String WITHDRAW_TIME = "STOP_ORDERWITHDRAWTIME";
	private static final String[] REQUIRED_HEADERS = {
		ID,
		TRANS_ID,
		STATUS,
		SEC_CODE,
		SEC_CLASS,
		ACCOUNT_CODE,
		CLIENT_CODE,
		DIR,
		QTY,
		PRICE,
		TYPE,
		LINKED_ID,
		PRICE1,
		PRICE2,
		OFFSET,
		OFFSET_UNITS,
		SPREAD,
		SPREAD_UNITS,
		DATE,
		TIME,
		WITHDRAW_TIME,
	};
	
	private static final Map<String, OrderType> TYPE_MAP;
	private static final Map<String, PriceUnit> UNIT_MAP;
	private static final Map<String, OrderDirection> DIR_MAP;
	private static final Map<String, OrderStatus> STATUS_MAP;
	
	static {
		TYPE_MAP = new Hashtable<String, OrderType>();
		TYPE_MAP.put("Стоп-лимит", OrderType.STOP_LIMIT);
		TYPE_MAP.put("Тэйк-профит", OrderType.TAKE_PROFIT);
		TYPE_MAP.put("Тэйк-профит и стоп-лимит", OrderType.TPSL);
		
		UNIT_MAP = new Hashtable<String, PriceUnit>();
		UNIT_MAP.put("Д", PriceUnit.MONEY);
		UNIT_MAP.put("%", PriceUnit.PERCENT);
		
		DIR_MAP = new Hashtable<String, OrderDirection>();
		DIR_MAP.put("B", OrderDirection.BUY);
		DIR_MAP.put("S", OrderDirection.SELL);

		STATUS_MAP = new Hashtable<String, OrderStatus>();
		STATUS_MAP.put("KILLED", OrderStatus.CANCELLED);
		STATUS_MAP.put("ACTIVE", OrderStatus.ACTIVE);
		STATUS_MAP.put("FILLED", OrderStatus.FILLED);
	}
	
	private final StopOrdersCache cache;
	private final RowDataConverter converter;
	
	public StopOrdersGateway(StopOrdersCache cache,RowDataConverter converter) {
		super();
		this.cache = cache;
		this.converter = converter;
	}
	
	/**
	 * Получить кэш таблицы стоп-заявок.
	 * <p>
	 * @return кэш
	 */
	public StopOrdersCache getStopOrdersCache() {
		return cache;
	}
	
	/**
	 * Получить конвертер данных ряда.
	 * <p>
	 * @return конвертер данных
	 */
	public RowDataConverter getRowDataConverter() {
		return converter;
	}

	@Override
	public String[] getRequiredHeaders() {
		return REQUIRED_HEADERS;
	}

	@Override
	public void toCache(Row row) throws DDEException {
		try {
			Long transId = converter.getLong(row, TRANS_ID);
			Long linkedOrderId = converter.getLong(row, LINKED_ID);
			cache.put(new StopOrderCache(converter.getLong(row, ID),
					transId != 0 ? transId : null,
					getStatus(row),
					converter.getString(row, SEC_CODE),
					converter.getString(row, SEC_CLASS),
					converter.getString(row, ACCOUNT_CODE),
					converter.getString(row, CLIENT_CODE),
					getDirection(row),
					converter.getLong(row, QTY),
					converter.getDoubleOrNull(row, PRICE),
					getStopLimitPrice(row),
					getTakeProfitPrice(row),
					getOffset(row),
					getSpread(row),
					(linkedOrderId == 0 ? null : linkedOrderId),
					converter.getTime(row, DATE, TIME, false),
					converter.getTime(row, DATE, WITHDRAW_TIME, true),
					getType(row)));
		} catch ( ValueException e ) {
			throw new DDEException(e.getMessage());
		}
	}
	
	/**
	 * Получить стоп-лимит цену.
	 * <p>
	 * @param row ряд
	 * @return спрэд
	 * @throws ValueException ошибка доступа к элементу ряда
	 */
	private Double getStopLimitPrice(Row row) throws ValueException {
		OrderType type = getType(row);
		if ( type == OrderType.STOP_LIMIT ) {
			return converter.getDouble(row, PRICE1);
		} else if ( type == OrderType.TPSL ) {
			return converter.getDouble(row, PRICE2);
		} else {
			return null;
		}
	}
	
	/**
	 * Получить тэйк-профит цену.
	 * <p>
	 * @param row ряд
	 * @return спрэд
	 * @throws ValueException ошибка доступа к элементу ряда
	 */
	private Double getTakeProfitPrice(Row row) throws ValueException {
		OrderType type = getType(row);
		if ( type == OrderType.TAKE_PROFIT || type == OrderType.TPSL ) {
			return converter.getDouble(row, PRICE1);
		} else {
			return null;
		}
	}
	
	/**
	 * Получить пару значение цены и единицы цены.
	 * <p>
	 * @param row ряд
	 * @param valueId идентификатор элемента вещественного типа со значением 
	 * @param unitId идентификатор строкового элемента с единицами
	 * @return цена или null, если значение нулевое или единица не определена
	 * @throws ValueException
	 */
	private Price getPrice(Row row, String valueId, String unitId)
			throws ValueException
	{
		if ( converter.getString(row, unitId).length() == 0 ) {
			return null;
		}
		PriceUnit unit = (PriceUnit)
			converter.getStringMappedTo(row, unitId, UNIT_MAP);
		return new Price(unit, converter.getDouble(row, valueId));
	}
	
	/**
	 * Получить отступ от пиковой цены.
	 * <p>
	 * @param row ряд
	 * @return отступ
	 * @throws ValueException ошибка доступа к элементу ряда
	 */
	private Price getOffset(Row row) throws ValueException {
		return getPrice(row, OFFSET, OFFSET_UNITS);
	}
	
	/**
	 * Получить защитный спрэд.
	 * <p>
	 * @param row ряд
	 * @return спрэд
	 * @throws ValueException ошибка доступа к элементу ряда
	 */
	private Price getSpread(Row row) throws ValueException {
		return getPrice(row, SPREAD, SPREAD_UNITS);
	}
	
	/**
	 * Получить направление стоп-заявки.
	 * <p>
	 * @param row ряд
	 * @return направление
	 * @throws ValueException ошибка доступа к элементу ряда
	 */
	private OrderDirection getDirection(Row row) throws ValueException {
		return (OrderDirection) converter.getStringMappedTo(row, DIR, DIR_MAP);
	}
	
	/**
	 * Получить статус стоп-заявки.
	 * <p>
	 * @param row ряд
	 * @return статус
	 * @throws ValueException ошибка доступа к элементу ряда
	 */
	private OrderStatus getStatus(Row row) throws ValueException {
		return (OrderStatus)
			converter.getStringMappedTo(row, STATUS, STATUS_MAP);
	}
	
	/**
	 * Получить тип стоп-заявки.
	 * <p>
	 * @param row ряд
	 * @return тип стоп-заявки
	 * @throws ValueException ошибка доступа к элементу ряда
	 */
	private OrderType getType(Row row) throws ValueException {
		return (OrderType) converter.getStringMappedTo(row, TYPE, TYPE_MAP);
	}

	@Override
	public Object getKeyValue(Row row) throws DDEException {
		try {
			return converter.getLong(row, ID);
		} catch ( ValueException e ) {
			throw new DDEException(e.getMessage());
		}
	}

	@Override
	public void fireUpdateCache() {
		cache.fireUpdateCache();
	}

	@Override
	public void clearCache() {
		cache.clear();
	}

	@Override
	public boolean shouldCache(Row row) throws DDEException {
		return true;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null ) {
			return false;
		}
		if ( other.getClass() != StopOrdersGateway.class ) {
			return false;
		}
		StopOrdersGateway o = (StopOrdersGateway) other;
		return new EqualsBuilder()
			.append(cache, o.cache)
			.append(converter, o.converter)
			.isEquals();
	}

}
