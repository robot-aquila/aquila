package ru.prolib.aquila.ChaosTheory;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

abstract public class ServiceBuilderSwitch implements ServiceBuilder {
	private final static Logger logger = LoggerFactory.getLogger(ServiceBuilderSwitch.class);
	private final HashMap<String, Variant> map;
	
	public ServiceBuilderSwitch() {
		super();
		map = new HashMap<String, Variant>();
	}
	
	/**
	 * Определить билдер для варианта.
	 * @param variant
	 * @param builder
	 * @return
	 */
	public ServiceBuilderSwitch set(String variant, ServiceBuilder builder) {
		return set(variant, builder, null);
	}
	
	/**
	 * Определить билдер для варианта с пост-акцией. 
	 * @param variant
	 * @param builder
	 * @param action
	 * @return
	 */
	public ServiceBuilderSwitch set(String variant, ServiceBuilder builder,
			ServiceBuilderAction action)
	{
		synchronized ( map ) {
			map.put(variant, new Variant(builder, action));
		}
		return this;
	}

	@Override
	public Object create(ServiceLocator locator, HierarchicalStreamReader reader)
			throws ServiceBuilderException, ServiceLocatorException
	{
		ServiceBuilder builder = null;
		String variant = currentVariant(locator, reader);
		synchronized ( map ) {
			builder = map.get(variant);
		}
		if ( builder == null ) {
			throw new ServiceBuilderSwitchException(variant);
		}
		logger.debug("Switch to: {}", variant);
		return builder.create(locator, reader);
	}
	
	abstract protected String currentVariant(ServiceLocator locator,
									         HierarchicalStreamReader reader)
		throws ServiceBuilderException, ServiceLocatorException;
	
	private static class Variant implements ServiceBuilder {
		private final ServiceBuilder builder;
		private final ServiceBuilderAction action;
		
		public Variant(ServiceBuilder builder, ServiceBuilderAction action) {
			this.builder = builder;
			this.action = action;
		}

		@Override
		public Object create(ServiceLocator locator,
				HierarchicalStreamReader reader)
				throws ServiceBuilderException, ServiceLocatorException
		{
			Object object = builder.create(locator, reader);
			if ( action != null ) {
				action.execute(object, locator, reader);
			}
			return object;
		}
		
	}

}
