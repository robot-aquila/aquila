package ru.prolib.aquila.ta.ds.quik;

import java.io.File;

import ru.prolib.aquila.ChaosTheory.AssetsException;
import ru.prolib.aquila.ChaosTheory.AssetsNotExistsException;
import ru.prolib.aquila.ChaosTheory.Props;
import ru.prolib.aquila.ChaosTheory.PropsException;
import ru.prolib.aquila.ChaosTheory.PropsNotExistsException;
import ru.prolib.aquila.ChaosTheory.ServiceBuilder;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderBadConfigException;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderException;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderHelperImpl;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * Конструктор портфеля QUIK. Так же инициализирует и устанавливает объект
 * хранилище заявок. Пример XML конфигурации:
 * 
 *  <Properties>
 *  	<Account>SPBFUT00XX</Account>
 *  	<Asset>RIH2</Asset>
 *  </Properties>
 * 
 *	<Portfolio type="quik">
 *      <OutputFile>/home/shared/quik-export/transaction.tri</OutputFile>
 *      <ResultFile>/home/shared/quik-export/transaction.tro</ResultFile>
 *  </Portfolio>
 */
public class PortfolioBuilderQuik implements ServiceBuilder {
	private final ServiceBuilderHelper helper;
	
	public PortfolioBuilderQuik(ServiceBuilderHelper helper) {
		super();
		this.helper = helper;
	}
	
	public PortfolioBuilderQuik() {
		this(new ServiceBuilderHelperImpl());
	}

	@Override
	public Object create(ServiceLocator locator,
						 HierarchicalStreamReader reader)
		throws ServiceBuilderException, ServiceLocatorException
	{
		try {
			return unsafe(locator, reader);
		} catch ( PropsNotExistsException e ) {
			throw new ServiceBuilderBadConfigException(e.getMessage(), e);
		} catch ( AssetsNotExistsException e ) {
			throw new ServiceBuilderBadConfigException(e.getMessage(), e);
		} catch ( Tr2QuikException e ) {
			throw new ServiceBuilderBadConfigException(e.getMessage(), e);
		} catch ( Exception e ) {
			throw new ServiceBuilderException(e.getMessage(), e);
		}
	}
	
	private PortfolioQuik unsafe(ServiceLocator locator,
			 						  HierarchicalStreamReader reader)
		throws ServiceBuilderException, ServiceLocatorException,
			   PropsException, Tr2QuikException, AssetsException
	{
		Props globals = locator.getProperties();
		Props config = helper.getProps(reader);
		PortfolioOrdersQuik orders = new PortfolioOrdersQuik();
		orders.registerHandler(locator.getRXltDdeDispatcher());
		locator.setPortfolioOrders(orders);
		return new PortfolioQuik(globals.getString("Account"),
				locator.getAssets().getByCode(globals.getString("Asset")),
				new Tr2QuikImpl(new File(config.getString("OutputFile")),
								new File(config.getString("ResultFile"))),
				locator.getPortfolioState(), orders);
	}

}
