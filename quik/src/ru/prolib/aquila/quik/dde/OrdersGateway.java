package ru.prolib.aquila.quik.dde;

import java.util.Hashtable;
import java.util.Map;

import ru.prolib.aquila.core.BusinessEntities.OrderDirection;
import ru.prolib.aquila.core.BusinessEntities.OrderStatus;
import ru.prolib.aquila.core.BusinessEntities.OrderType;
import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.dde.DDEException;

/**
 * Шлюз к кэшу таблицы заявок.
 */
public class OrdersGateway implements CacheGateway {
	private static final String ID = "ORDERNUM";
	private static final String TRANS_ID = "TRANSID";
	private static final String STATUS = "STATUS";
	private static final String SEC_CODE = "SECCODE";
	private static final String SEC_CLASS = "CLASSCODE";
	private static final String ACCOUNT_CODE = "ACCOUNT";
	private static final String CLIENT_CODE = "CLIENTCODE";
	private static final String DIR = "BUYSELL";
	private static final String QTY = "QTY";
	private static final String QTY_REST = "BALANCE";
	private static final String PRICE = "PRICE";
	private static final String DATE = "ORDERDATE";
	private static final String TIME = "ORDERTIME";
	private static final String WITHDRAW_DATE = "WITHDRAW_DATE";
	private static final String WITHDRAW_TIME = "WITHDRAW_TIME";
	private static final String TYPE = "MODE";
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
		QTY_REST,
		PRICE,
		DATE,
		TIME,
		WITHDRAW_DATE,
		WITHDRAW_TIME,
		TYPE,
	};
	private static final Map<String, Object> STATUS_MAP;
	private static final Map<String, Object> DIR_MAP;
	
	static {
		STATUS_MAP = new Hashtable<String, Object>();
		STATUS_MAP.put("ACTIVE", OrderStatus.ACTIVE);
		STATUS_MAP.put("FILLED", OrderStatus.FILLED);
		STATUS_MAP.put("KILLED", OrderStatus.CANCELLED);
		
		DIR_MAP = new Hashtable<String, Object>();
		DIR_MAP.put("B", OrderDirection.BUY);
		DIR_MAP.put("S", OrderDirection.SELL);
	}
	
	private final OrdersCache cache;
	private final RowDataConverter converter;
	
	public OrdersGateway(OrdersCache cache, RowDataConverter converter) {
		super();
		this.cache = cache;
		this.converter = converter;
	}

	@Override
	public String[] getRequiredHeaders() {
		return REQUIRED_HEADERS;
	}

	@Override
	public void toCache(Row row) throws DDEException {
		try {
			OrderType type = getType(row);
			Long transId = converter.getLong(row, TRANS_ID);
			cache.put(new OrderCache(converter.getLong(row, ID),
					transId != 0 ? transId : null,
					getStatus(row),
					converter.getString(row, SEC_CODE),
					converter.getString(row, SEC_CLASS),
					converter.getString(row, ACCOUNT_CODE),
					converter.getString(row, CLIENT_CODE),
					getDirection(row),
					converter.getLong(row, QTY),
					converter.getLong(row, QTY_REST),
					type == OrderType.MARKET ?
							null : converter.getDouble(row, PRICE),
					converter.getTime(row, DATE, TIME, false),
					converter.getTime(row, WITHDRAW_DATE, WITHDRAW_TIME, true),
					type));
		} catch ( ValueException e ) {
			throw new DDEException(e.getMessage());
		}
	}
	
	@Override
	public Object getKeyValue(Row row) throws DDEException {
		try {
			return converter.getLong(row, ID);
		} catch ( ValueException e ) {
			throw new DDEException(e.getMessage());
		}
	}
	
	private OrderStatus getStatus(Row row) throws ValueException {
		return (OrderStatus) converter.getStringMappedTo(row,STATUS,STATUS_MAP);
	}
	
	private OrderDirection getDirection(Row row) throws ValueException {
		return (OrderDirection) converter.getStringMappedTo(row, DIR, DIR_MAP);
	}
	
	private OrderType getType(Row row) throws ValueException {
		String type = converter.getString(row, TYPE).substring(0, 1);
		if ( type.equals("L") ) {
			return OrderType.LIMIT;
		} else if ( type.equals("M") ) {
			return OrderType.MARKET;
		} else {
			return OrderType.OTHER;
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
	public boolean shouldCache(Row row) {
		return true;
	}

}
