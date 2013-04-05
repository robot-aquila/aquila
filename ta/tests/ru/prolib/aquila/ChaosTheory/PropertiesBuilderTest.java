package ru.prolib.aquila.ChaosTheory;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;


public class PropertiesBuilderTest {
	ServiceBuilderHelper helper;
	Props props;
	HierarchicalStreamReader reader;
	ServiceLocator locator;
	PropertiesBuilder builder;
	IMocksControl control;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		helper = control.createMock(ServiceBuilderHelper.class);
		locator = control.createMock(ServiceLocator.class);
		reader = control.createMock(HierarchicalStreamReader.class);
		props = new PropsImpl();
		builder = new PropertiesBuilder(helper);
	}
	
	@Test
	public void testCreate_Ok() throws Exception {
		expect(helper.getProps(reader)).andReturn(props);
		control.replay();
		
		assertSame(props, (Props)builder.create(locator, reader));
		
		control.verify();
	}

}
