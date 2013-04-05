package ru.prolib.aquila.ChaosTheory;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public interface ServiceBuilderAction {
	
	public void execute(Object constructed, ServiceLocator locator,
			HierarchicalStreamReader reader)
		throws ServiceBuilderException,ServiceLocatorException;

}
