package ru.prolib.aquila.test;

import ru.prolib.aquila.ChaosTheory.Asset;
import ru.prolib.aquila.ChaosTheory.AssetsException;
import ru.prolib.aquila.ChaosTheory.PortfolioException;
import ru.prolib.aquila.ChaosTheory.Props;
import ru.prolib.aquila.ChaosTheory.PropsException;
import ru.prolib.aquila.ChaosTheory.ServiceBuilder;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderException;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderHelperImpl;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;
import ru.prolib.aquila.util.SequenceLong;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * Конструктор тестировочного портфеля. Так же инициализирует и устанавливает
 * объекты хранилища заявок и состояния портфеля. Пример XML конфигурации.
 * 
 *	<Properties>
 *		<Asset>RIH2</Asset>
 *	</Properties>
 *
 *	<Portfolio type="test" initialMoney="1000000" />
 */
public class TestPortfolioBuilder implements ServiceBuilder {
	private final ServiceBuilderHelper helper;
	
	public TestPortfolioBuilder() {
		this(new ServiceBuilderHelperImpl());
	}
	
	public TestPortfolioBuilder(ServiceBuilderHelper helper) {
		super();
		this.helper = helper;
	}

	@Override
	public Object create(ServiceLocator locator,
						 HierarchicalStreamReader reader)
			throws ServiceBuilderException, ServiceLocatorException
	{
		try {
			return unsafe(locator, reader);
		} catch ( PortfolioException e ) {
			throw new ServiceBuilderException(e.getMessage(), e);
		} catch ( AssetsException e ) {
			throw new ServiceBuilderException(e.getMessage(), e);
		} catch ( PropsException e ) {
			throw new ServiceBuilderException(e.getMessage(), e);
		}
	}
	
	private TestPortfolio unsafe(ServiceLocator locator,
						 HierarchicalStreamReader reader)
			throws ServiceBuilderException,
				   ServiceLocatorException,
				   PortfolioException,
				   AssetsException,
				   PropsException
	{
		
		Props globals = locator.getProperties();
		Asset asset = locator.getAssets().getByCode(globals.getString("Asset"));
		SequenceLong id = new SequenceLong();
		TestPortfolioOrders orders = new TestPortfolioOrders(id);
		TestPortfolioState state = new TestPortfolioState(asset);
		state.setMoney(helper.getDouble("initialMoney", 0.0d, reader));
		orders.startService(locator.getMarketData());
		state.startService(orders);
		locator.setPortfolioOrders(orders);
		locator.setPortfolioState(state);
		return new TestPortfolio(asset, id, orders, state);
	}

}
