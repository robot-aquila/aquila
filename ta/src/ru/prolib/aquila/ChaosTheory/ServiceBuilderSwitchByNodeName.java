package ru.prolib.aquila.ChaosTheory;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public class ServiceBuilderSwitchByNodeName extends ServiceBuilderSwitch {
	
	public ServiceBuilderSwitchByNodeName() {
		super();
	}

	@Override
	protected String currentVariant(ServiceLocator locator,
									HierarchicalStreamReader reader)
		throws ServiceBuilderException,	ServiceLocatorException
	{
		return reader.getNodeName();
	}

}
