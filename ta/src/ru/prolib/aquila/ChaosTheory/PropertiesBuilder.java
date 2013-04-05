package ru.prolib.aquila.ChaosTheory;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public class PropertiesBuilder implements ServiceBuilder {
	private final ServiceBuilderHelper helper;
	
	public PropertiesBuilder(ServiceBuilderHelper helper) {
		super();
		this.helper = helper;
	}
	
	public PropertiesBuilder() {
		this(new ServiceBuilderHelperImpl());
	}

	@Override
	public Object create(ServiceLocator locator, HierarchicalStreamReader reader)
			throws ServiceBuilderException, ServiceLocatorException
	{
		return helper.getProps(reader);
	}

}
