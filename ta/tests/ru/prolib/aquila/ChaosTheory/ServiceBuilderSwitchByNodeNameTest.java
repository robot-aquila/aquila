package ru.prolib.aquila.ChaosTheory;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertSame;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;


public class ServiceBuilderSwitchByNodeNameTest {
	ServiceBuilderSwitch sw;
	ServiceLocator locator;
	HierarchicalStreamReader reader;
	ServiceBuilder builder1,builder2;
	ServiceBuilderAction action;
	IMocksControl control;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		locator = control.createMock(ServiceLocator.class);
		reader = control.createMock(HierarchicalStreamReader.class);
		builder1 = control.createMock(ServiceBuilder.class);
		builder2 = control.createMock(ServiceBuilder.class);
		action = control.createMock(ServiceBuilderAction.class);
		sw = new ServiceBuilderSwitchByNodeName()
			.set("Assets", builder1, action)
			.set("Portfolio", builder2);
	}
	
	@Test
	public void testCreate_SwitchOkWithAction() throws Exception {
		Object object = new Object();
		expect(reader.getNodeName()).andReturn("Assets");
		expect(builder1.create(locator, reader)).andReturn(object);
		action.execute(object, locator, reader);
		control.replay();
		
		assertSame(object, sw.create(locator, reader));
		
		control.verify();
	}
	
	@Test
	public void testCreate_SwitchOkWithoutAction() throws Exception {
		Object object = new Object();
		expect(reader.getNodeName()).andReturn("Portfolio");
		expect(builder2.create(locator, reader)).andReturn(object);
		control.replay();
		
		assertSame(object, sw.create(locator, reader));
		
		control.verify();
	}
	
	@Test (expected=ServiceBuilderSwitchException.class)
	public void testCreate_ThrowsSwitchException() throws Exception {
		expect(reader.getNodeName()).andReturn("Database");
		control.replay();
		
		sw.create(locator, reader);
	}

}
