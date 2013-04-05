package ru.prolib.aquila.ta.ds.csv;

import ru.prolib.aquila.ChaosTheory.ServiceBuilder;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderException;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderHelperImpl;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;
import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.ds.DataSet;
import ru.prolib.aquila.ta.ds.DataSetDouble;
import ru.prolib.aquila.ta.ds.MarketData;
import ru.prolib.aquila.ta.ds.MarketDataImpl;
import ru.prolib.aquila.ta.ds.MarketDataReaderUseDecorator;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * Пример XML-узла конфигурации сервиса:
 * 
 * <MarketData type="csv" file="foobar.csv" />
 *
 */
public class MarketDataBuilderCsv implements ServiceBuilder {
	public static final String FILE_ATTR = "file";
	
	private final ServiceBuilderHelper helper;
	
	public MarketDataBuilderCsv(ServiceBuilderHelper helper) {
		super();
		this.helper = helper;
	}
	
	public MarketDataBuilderCsv() {
		this(new ServiceBuilderHelperImpl());
	}

	@Override
	public Object create(ServiceLocator locator, HierarchicalStreamReader reader)
			throws ServiceBuilderException, ServiceLocatorException
	{
		try {
			MarketDataReaderCsv dataReader =
				new MarketDataReaderCsv(helper.getString(FILE_ATTR, reader));
			MarketDataReaderUseDecorator deco =
				new MarketDataReaderUseDecorator(dataReader);
			MarketData data = new MarketDataImpl(deco);
			addMainValues(data, deco.getDecorator());
			return data;
		} catch ( Exception e ) {
			throw new ServiceBuilderException(e.getMessage(), e);
		}
	}
	
	private void addMainValues(MarketData md, DataSet ds)
		throws ValueException
	{
		md.addValue(new DataSetDateFinam(MarketData.TIME,ds,"<DATE>","<TIME>"));
		md.addValue(new DataSetDouble(MarketData.OPEN, ds, "<OPEN>"));
		md.addValue(new DataSetDouble(MarketData.HIGH, ds, "<HIGH>"));
		md.addValue(new DataSetDouble(MarketData.LOW,  ds, "<LOW>"));
		md.addValue(new DataSetDouble(MarketData.CLOSE,ds, "<CLOSE>"));
		md.addValue(new DataSetDouble(MarketData.VOL,  ds, "<VOL>"));
		md.addMedian(MarketData.HIGH, MarketData.LOW, MarketData.MEDIAN);
	}

}
