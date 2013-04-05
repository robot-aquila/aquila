package ru.prolib.aquila.ta.ds.quik;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import ru.prolib.aquila.ChaosTheory.ServiceBuilder;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderException;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderHelperImpl;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;

public class PortfolioStateBuilderQuikForts implements ServiceBuilder {
	public static final String ACCOUNT_PROP		= "Account";
	public static final String ASSET_ATTR		= "asset";
	public static final String POSITION_ATTR	= "position";
	public static final String LIMITS_ATTR		= "limits";
	
	private final ServiceBuilderHelper helper;
	
	public PortfolioStateBuilderQuikForts(ServiceBuilderHelper helper) {
		super();
		this.helper = helper;
	}
	
	public PortfolioStateBuilderQuikForts() {
		this(new ServiceBuilderHelperImpl());
	}

	@Override
	public Object create(ServiceLocator locator,
						 HierarchicalStreamReader reader)
			throws ServiceBuilderException, ServiceLocatorException
	{
		try {
			PortfolioStateQuikForts state = new PortfolioStateQuikForts(
				locator.getProperties().getString(ACCOUNT_PROP),
				locator.getAssets()
					.getByCode(helper.getString(ASSET_ATTR, reader)),
				helper.getString(POSITION_ATTR, reader),
				helper.getString(LIMITS_ATTR, reader));
			state.registerHandler(locator.getRXltDdeDispatcher());
			return state;
		} catch ( Exception e ) {
			throw new ServiceBuilderException(e.getMessage(), e);
		}
	}

}
