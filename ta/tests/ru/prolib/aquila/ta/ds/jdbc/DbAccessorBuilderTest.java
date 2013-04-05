package ru.prolib.aquila.ta.ds.jdbc;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertNotNull;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.ChaosTheory.Props;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public class DbAccessorBuilderTest {
	ServiceBuilderHelper helper;
	Props props;
	HierarchicalStreamReader reader;
	ServiceLocator locator;
	IMocksControl control;
	DbAccessorBuilder builder;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		helper = control.createMock(ServiceBuilderHelper.class);
		props = control.createMock(Props.class);
		reader = control.createMock(HierarchicalStreamReader.class);
		locator = control.createMock(ServiceLocator.class);
		builder = new DbAccessorBuilder(helper);
	}
	
	@Test
	public void testCreate_Ok() throws Exception {
		expect(helper.getProps(reader)).andReturn(props);
		expect(props.getString(DbAccessorBuilder.DRIVER_CLASS,
				DbAccessorImpl.DEFAULT_DRIVER))
			.andReturn("foo.bar.driver");
		expect(props.getString(DbAccessorBuilder.URL))
			.andReturn("dsn");
		expect(props.getString(DbAccessorBuilder.USER, null))
			.andReturn("user");
		expect(props.getString(DbAccessorBuilder.PASSWORD, null))
			.andReturn("pass");
		control.replay();
		
		DbAccessorImpl dba = (DbAccessorImpl) builder.create(locator, reader);
		assertNotNull(dba);
		
		control.verify();
	}

}
