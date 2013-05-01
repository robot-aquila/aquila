package ru.prolib.aquila.quik.dde;

import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.dde.DDEException;

/**
 * Шлюз к кэшу таблицы собственных сделок.
 */
public class TradesGateway implements CacheGateway {
	private static final String ID = "TRADENUM";
	private static final String DATE = "SESSION_DATE";
	private static final String TIME = "TRADETIME";
	private static final String ORDER_ID = "ORDERNUM";
	private static final String PRICE = "PRICE";
	private static final String QTY = "QTY";
	private static final String VALUE = "VALUE";
	private static final String[] REQUIRED_HEADERS = {
		ID,
		DATE,
		TIME,
		ORDER_ID,
		PRICE,
		QTY,
		VALUE,
	};
	
	private final TradesCache cache;
	private final RowDataConverter converter;
	
	public TradesGateway(TradesCache cache, RowDataConverter converter) {
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
			cache.put(new TradeCache(converter.getLong(row, ID),
					converter.getTime(row, DATE, TIME, false),
					converter.getLong(row, ORDER_ID),
					converter.getDouble(row, PRICE),
					converter.getLong(row, QTY),
					converter.getDouble(row, VALUE)));
			
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

	@Override
	public void fireUpdateCache() {
		cache.fireUpdateCache();
	}

	@Override
	public void clearCache() {
		cache.clear();
	}
	
}
