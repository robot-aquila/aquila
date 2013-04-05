package ru.prolib.aquila.ChaosTheory;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * Свитч билдера по атрибуту.
 */
public class ServiceBuilderSwitchByAttribute extends ServiceBuilderSwitch {
	private final String attr;
	private final ServiceBuilderHelper helper;
	
	public ServiceBuilderSwitchByAttribute(String attr) {
		this(attr, new ServiceBuilderHelperImpl());
	}
	
	public ServiceBuilderSwitchByAttribute(String attr,
										   ServiceBuilderHelper helper)
	{
		super();
		this.attr = attr;
		this.helper = helper;
	}

	@Override
	public String currentVariant(ServiceLocator locator,
				         		 HierarchicalStreamReader reader)
			throws ServiceBuilderException, ServiceLocatorException
	{
		return helper.getAttribute(attr, reader);
	}

}
