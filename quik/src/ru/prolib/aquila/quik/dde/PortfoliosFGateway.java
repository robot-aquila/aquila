package ru.prolib.aquila.quik.dde;

import ru.prolib.aquila.core.data.ValueException;
import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.dde.DDEException;

/**
 * Шлюз к кэшу таблицы портфелей ФОРТС.
 */
public class PortfoliosFGateway implements CacheGateway {
	private static final String ACCOUNT_CODE = "TRDACCID";
	private static final String FIRM_ID = "FIRMID";
	private static final String CASH = "CBPLPLANNED";
	private static final String BALANCE = "CBPLIMIT";
	private static final String VARMARGIN = "VARMARGIN";
	private static final String TYPE = "LIMIT_TYPE";
	private static final String TYPE_MONEY = "Ден.средства";
	private static final String[] REQUIRED_HEADERS = {
		ACCOUNT_CODE,
		FIRM_ID,
		CASH,
		BALANCE,
		VARMARGIN,
		TYPE,
	};
	
	private final PortfoliosFCache cache;
	private final RowDataConverter converter;
	
	public PortfoliosFGateway(PortfoliosFCache cache,
			RowDataConverter converter)
	{
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
			cache.put(new PortfolioFCache(converter.getString(row,ACCOUNT_CODE),
				converter.getString(row, FIRM_ID),
				converter.getDouble(row, BALANCE),
				converter.getDouble(row, CASH),
				converter.getDouble(row, VARMARGIN)));
		} catch (ValueException e ) {
			throw new DDEException(e.getMessage());
		}
	}

	@Override
	public Object getKeyValue(Row row) throws DDEException {
		try {
			return converter.getString(row, FIRM_ID) + "#"
				+ converter.getString(row, ACCOUNT_CODE);
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
		try {
			return converter.getString(row, TYPE).equals(TYPE_MONEY);
		} catch ( ValueException e ) {
			throw new DDEException(e.getMessage());
		}
	}

}
