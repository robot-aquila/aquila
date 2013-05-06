package ru.prolib.aquila.quik.dde;

import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.dde.DDEException;

/**
 * Шлюз к кэшу таблицы позиций по деривативам.
 */
public class PositionsFGateway implements CacheGateway {
	private static final String ACCOUNT_CODE = "TRDACCID";
	private static final String FIRM_ID = "FIRMID";
	private static final String SEC_SHORT_NAME = "SEC_SHORT_NAME";
	private static final String OPEN = "START_NET";
	private static final String CURR = "TOTAL_NET";
	private static final String VARMARGIN = "VARMARGIN";
	private static final String[] REQUIRED_HEADERS = {
		ACCOUNT_CODE,
		FIRM_ID,
		SEC_SHORT_NAME,
		OPEN,
		CURR,
		VARMARGIN,
	};

	private final PositionsFCache cache;
	private final RowDataConverter converter;
	
	public PositionsFGateway(PositionsFCache cache,
			RowDataConverter converter) {
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
			cache.put(new PositionFCache(converter.getString(row, ACCOUNT_CODE),
				converter.getString(row, FIRM_ID),
				converter.getString(row, SEC_SHORT_NAME),
				converter.getLong(row, OPEN),
				converter.getLong(row, CURR),
				converter.getDouble(row, VARMARGIN)));
		} catch ( ValueException e ) {
			throw new DDEException(e.getMessage());
		}
	}

	@Override
	public Object getKeyValue(Row row) throws DDEException {
		try {
			return converter.getString(row, FIRM_ID)
				+ "#" + converter.getString(row, ACCOUNT_CODE)
				+ "#" + converter.getString(row, SEC_SHORT_NAME);
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

}
