package ru.prolib.aquila.ChaosTheory;

import ru.prolib.aquila.ta.ds.MarketData;
import ru.prolib.aquila.ta.ds.MarketDataBuilderGrouper;
import ru.prolib.aquila.ta.ds.csv.MarketDataBuilderCsv;
import ru.prolib.aquila.ta.ds.jdbc.DbAccessor;
import ru.prolib.aquila.ta.ds.jdbc.DbAccessorBuilder;
import ru.prolib.aquila.ta.ds.jdbc.MarketDataBuilderJdbc;
import ru.prolib.aquila.ta.ds.quik.AssetsBuilderQuik;
import ru.prolib.aquila.ta.ds.quik.ExportBuilderQuik;
import ru.prolib.aquila.ta.ds.quik.ExportQuik;
import ru.prolib.aquila.ta.ds.quik.PortfolioBuilderQuik;
import ru.prolib.aquila.ta.ds.quik.PortfolioStateBuilderQuikForts;
import ru.prolib.aquila.test.TestPortfolioBuilder;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public class ServiceLocatorDefaultBuilder extends
		ServiceBuilderSwitchByNodeName
{
	
	public ServiceLocatorDefaultBuilder() {
		super();
		set("Export", new ExportBuilderQuik(), new SetExportDispatcher());
		set("Properties", new PropertiesBuilder(), new SetProperties());
		set("Database", new DbAccessorBuilder(), new SetDatabase());
		set("MarketData", new ServiceBuilderSwitchByAttribute("type")
			.set("csv",
					new MarketDataBuilderGrouper(new MarketDataBuilderCsv()),
					new SetMarketData())
			.set("jdbc",
					new MarketDataBuilderGrouper(new MarketDataBuilderJdbc()),
					new SetMarketData())
		);
		set("Assets", new ServiceBuilderSwitchByAttribute("type")
			.set("local", new AssetsBuilderLocal(), new SetAssets())
			.set("quik",  new AssetsBuilderQuik(),  new SetAssets())
		);
		set("PortfolioState", new ServiceBuilderSwitchByAttribute("type")
			.set("quik", new PortfolioStateBuilderQuikForts(), new SetState())
		);
		set("Portfolio", new ServiceBuilderSwitchByAttribute("type")
			.set("quik", new PortfolioBuilderQuik(), new SetPortfolio())
			.set("test", new TestPortfolioBuilder(), new SetPortfolio())
		);
	}
	
	@Override
	public Object create(ServiceLocator locator,
						 HierarchicalStreamReader reader)
		throws ServiceBuilderException, ServiceLocatorException
	{
		while ( reader.hasMoreChildren() ) {
			reader.moveDown();
			super.create(locator, reader);
			reader.moveUp();
		}
		return locator;
	}
	
	public static class SetAssets implements ServiceBuilderAction {
		
		public SetAssets() {
			super();
		}

		@Override
		public void execute(Object constructed, ServiceLocator locator,
				HierarchicalStreamReader reader)
				throws ServiceBuilderException, ServiceLocatorException
		{
			locator.setAssets((Assets) constructed);
		}
		
	}
	
	public static class SetDatabase implements ServiceBuilderAction {
		
		public SetDatabase() {
			super();
		}

		@Override
		public void execute(Object constructed, ServiceLocator locator,
				HierarchicalStreamReader reader)
				throws ServiceBuilderException, ServiceLocatorException
		{
			locator.setDatabase((DbAccessor) constructed);
		}
		
	}
	
	public static class SetMarketData implements ServiceBuilderAction {
		
		public SetMarketData() {
			super();
		}

		@Override
		public void execute(Object constructed, ServiceLocator locator,
				HierarchicalStreamReader reader)
				throws ServiceBuilderException, ServiceLocatorException
		{
			locator.setMarketData((MarketData) constructed);
		}
		
	}
	
	public static class SetProperties implements ServiceBuilderAction {
		
		public SetProperties() {
			super();
		}

		@Override
		public void execute(Object constructed, ServiceLocator locator,
				HierarchicalStreamReader reader)
				throws ServiceBuilderException, ServiceLocatorException
		{
			locator.setProperties((Props) constructed);
		}
	}
	
	public static class SetExportDispatcher implements ServiceBuilderAction {
		
		public SetExportDispatcher() {
			super();
		}

		@Override
		public void execute(Object constructed, ServiceLocator locator,
				HierarchicalStreamReader reader)
				throws ServiceBuilderException, ServiceLocatorException
		{
			locator.setExportService((ExportQuik) constructed);
		}
		
	}
	
	public static class SetPortfolio implements ServiceBuilderAction {
		
		public SetPortfolio() {
			super();
		}

		@Override
		public void execute(Object constructed, ServiceLocator locator,
				HierarchicalStreamReader reader)
				throws ServiceBuilderException, ServiceLocatorException
		{
			locator.setPortfolio((Portfolio) constructed);
		}
		
	}
	
	public static class SetState implements ServiceBuilderAction {
		
		public SetState() {
			super();
		}

		@Override
		public void execute(Object constructed, ServiceLocator locator,
				HierarchicalStreamReader reader)
				throws ServiceBuilderException, ServiceLocatorException
		{
			locator.setPortfolioState((PortfolioState) constructed);
		}
		
	}

}
