package ru.prolib.aquila.ta.ds.quik;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import ru.prolib.aquila.ChaosTheory.AssetsLazy;
import ru.prolib.aquila.ChaosTheory.ServiceBuilder;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderException;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderHelperImpl;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;

/**
 * Пример XML узла конфигурации:
 * 
 * <Assets type="quik" assets="[export]assets" deals="[export]allDeals" />
 *
 * Атрибут assets указывает имя таблицы с информацией об активах
 * (см. docs/Assets-Dde.PNG и docs/Assets-Table.PNG). Атрибут deals указывает
 * имя таблицы всех сделок (см. docs/AllDeals-Dde.PNG и docs/AllDeals-Table.PNG)
 */
public class AssetsBuilderQuik implements ServiceBuilder {
	private final ServiceBuilderHelper helper;
	
	public AssetsBuilderQuik(ServiceBuilderHelper helper) {
		super();
		this.helper = helper;
	}
	
	public AssetsBuilderQuik() {
		this(new ServiceBuilderHelperImpl());
	}

	@Override
	public Object create(ServiceLocator locator, HierarchicalStreamReader reader)
			throws ServiceBuilderException, ServiceLocatorException
	{
		AssetsQuik assets = new AssetsQuik(helper.getAttribute("assets",reader),
				helper.getAttribute("deals", reader));
		assets.registerHandler(locator.getRXltDdeDispatcher());
		return new AssetsLazy(assets);
	}

}
