package ru.prolib.aquila.ta.ds;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import ru.prolib.aquila.ChaosTheory.ServiceBuilder;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderException;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderHelperImpl;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;

/**
 * Меняет таймфрейм источника данных о торгах.
 * Для активации конфигурация узла MarketData должна содержать атрибут
 * timeframe с положительным значением.
 *
 */
public class MarketDataBuilderGrouper implements ServiceBuilder {
	private final ServiceBuilderHelper helper;
	private final ServiceBuilder builder;
	
	public MarketDataBuilderGrouper(ServiceBuilder marketDataBuilder,
									ServiceBuilderHelper helper)
	{
		super();
		this.helper = helper;
		this.builder = marketDataBuilder;
	}
	
	public MarketDataBuilderGrouper(ServiceBuilder marketDataBuilder) {
		this(marketDataBuilder, new ServiceBuilderHelperImpl());
	}

	@Override
	public Object create(ServiceLocator locator, HierarchicalStreamReader reader)
			throws ServiceBuilderException, ServiceLocatorException
	{
		MarketData data = (MarketData) builder.create(locator, reader);
		int timeframe = helper.getInt("timeframe", 0, reader);
		if ( timeframe > 0 ) {
			data = new MarketDataGrouper(data, timeframe);
		}
		return data;
	}

}
