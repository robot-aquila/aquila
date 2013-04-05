package ru.prolib.aquila.ta.ds.jdbc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.prolib.aquila.ChaosTheory.ServiceBuilder;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderException;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderFormatException;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderHelperImpl;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;
import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.ds.DataSet;
import ru.prolib.aquila.ta.ds.DataSetDate;
import ru.prolib.aquila.ta.ds.DataSetDouble;
import ru.prolib.aquila.ta.ds.MarketData;
import ru.prolib.aquila.ta.ds.MarketDataImpl;
import ru.prolib.aquila.ta.ds.MarketDataReaderUseDecorator;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * Пример XML-узла конфигурации сервиса:
 * 
 *	<MarketData type="jdbc" table="rts_5m" column="period_time"
 * 		prepareLimit="200" updateLimit="1" startTime="2011-01-01 00:00:00" />
 *
 */
public class MarketDataBuilderJdbc implements ServiceBuilder {
	public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	public static final String TABLE_ATTR			= "table";
	public static final String COLUMN_ATTR			= "column";
	public static final String START_TIME_ATTR		= "startTime";
	public static final String PREPARE_LIMIT_ATTR	= "prepareLimit";
	public static final String UPDATE_LIMIT_ATTR	= "updateLimit";
	
	public static final String COLUMN_DEFAULT		= "period_time";
	public static final int PREPARE_LIMIT_DEFAULT	= 200;
	public static final int UPDATE_LIMIT_DEFAULT	= 999;
	
	private final ServiceBuilderHelper helper;
	
	public MarketDataBuilderJdbc(ServiceBuilderHelper helper) {
		super();
		this.helper = helper;
	}
	
	public MarketDataBuilderJdbc() {
		this(new ServiceBuilderHelperImpl());
	}

	@Override
	public Object create(ServiceLocator locator,
						 HierarchicalStreamReader reader)
			throws ServiceBuilderException, ServiceLocatorException
	{
		String defTime =  df.format(new Date());
		Date time = null;
		try {
			time = df.parse(helper.getString(START_TIME_ATTR, defTime, reader));
		} catch ( ParseException e ) {
			throw new ServiceBuilderFormatException(START_TIME_ATTR
					+ " format error: " + e.getMessage(), e);
		}
		
		String column = helper.getString(COLUMN_ATTR, COLUMN_DEFAULT, reader); 
		MarketDataReaderJdbc dataReader = new MarketDataReaderJdbc(
			locator.getDatabase(),
			helper.getString(TABLE_ATTR, reader),
			column,
			time,
			helper.getInt(PREPARE_LIMIT_ATTR, PREPARE_LIMIT_DEFAULT, reader),
			helper.getInt(UPDATE_LIMIT_ATTR, UPDATE_LIMIT_DEFAULT, reader));
		MarketDataReaderUseDecorator deco =
			new MarketDataReaderUseDecorator(dataReader);
		MarketData data = new MarketDataImpl(deco);
		try {
			addMainValues(data, deco.getDecorator(), column);
		} catch ( ValueException e ) {
			throw new ServiceBuilderException(e.getMessage(), e);
		}
		return data;
	}
	
	private void addMainValues(MarketData md, DataSet ds, String time)
		throws ValueException
	{
		md.addValue(new DataSetDate(MarketData.TIME,   ds, time));
		md.addValue(new DataSetDouble(MarketData.OPEN, ds, "open"));
		md.addValue(new DataSetDouble(MarketData.HIGH, ds, "high"));
		md.addValue(new DataSetDouble(MarketData.LOW,  ds, "low"));
		md.addValue(new DataSetDouble(MarketData.CLOSE,ds, "close"));
		md.addValue(new DataSetDouble(MarketData.VOL,  ds, "volume"));
		md.addMedian(MarketData.HIGH, MarketData.LOW, MarketData.MEDIAN);
	}

}
