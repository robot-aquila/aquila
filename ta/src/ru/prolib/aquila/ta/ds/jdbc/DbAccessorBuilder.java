package ru.prolib.aquila.ta.ds.jdbc;

import ru.prolib.aquila.ChaosTheory.Props;
import ru.prolib.aquila.ChaosTheory.PropsException;
import ru.prolib.aquila.ChaosTheory.ServiceBuilder;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderException;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderHelperImpl;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public class DbAccessorBuilder implements ServiceBuilder {
	final static String DRIVER_CLASS = "driverClass";
	final static String URL = "url";
	final static String USER = "user";
	final static String PASSWORD = "password";
	
	private final ServiceBuilderHelper helper;
	
	public DbAccessorBuilder(ServiceBuilderHelper helper) {
		super();
		this.helper = helper;
	}
	
	public DbAccessorBuilder() {
		this(new ServiceBuilderHelperImpl());
	}

	@Override
	public Object create(ServiceLocator locator,
			HierarchicalStreamReader reader)
		throws ServiceBuilderException, ServiceLocatorException
	{
		Props props = helper.getProps(reader);
		try {
			return new DbAccessorImpl(
				props.getString(DRIVER_CLASS, DbAccessorImpl.DEFAULT_DRIVER),
				props.getString(URL),
				props.getString(USER, null),
				props.getString(PASSWORD, null)
			);
		} catch ( PropsException e ) {
			throw new ServiceBuilderException(e.getMessage(), e);
		}
	}

}
