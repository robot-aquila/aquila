package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.utils.experimental.chart.ChartSpaceManager;

public class CategoryAxisDriverProxyImplTest {
	private IMocksControl control;
	private CategoryAxisDriver driverMock;
	private CategoryAxisRulerRenderer rendererMock;
	private CategoryAxisDisplayMapper mapperMock;
	private ChartSpaceManager spaceManagerMock;
	private CategoryAxisDriverProxyImpl service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		driverMock = control.createMock(CategoryAxisDriver.class);
		rendererMock = control.createMock(CategoryAxisRulerRenderer.class);
		mapperMock = control.createMock(CategoryAxisDisplayMapper.class);
		spaceManagerMock = control.createMock(ChartSpaceManager.class);
		service = new CategoryAxisDriverProxyImpl(driverMock);
	}
	
	@Test
	public void testRegisterForRulers() {
		spaceManagerMock.registerAxis(driverMock);
		control.replay();
		
		service.registerForRulers(spaceManagerMock);
		
		control.verify();
	}
	
	@Test
	public void testGetCurrentMapper() {
		service.setCurrentMapper(mapperMock);
		control.replay();
		
		assertSame(mapperMock, service.getCurrentMapper());
		
		control.verify();
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetCurrentMapper_ThrowsIfNotDefined() {
		service.getCurrentMapper();
	}
	
	@Test
	public void testGetRulerRenderer() {
		expect(driverMock.getRenderer("foobar")).andReturn(rendererMock);
		control.replay();
		
		assertSame(rendererMock, service.getRulerRenderer("foobar"));
		
		control.verify();
	}

}
