package ru.prolib.aquila.ChaosTheory;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public interface ServiceBuilder {
	
	public Object create(ServiceLocator locator,
						 HierarchicalStreamReader reader)
			throws ServiceBuilderException, ServiceLocatorException;

}
