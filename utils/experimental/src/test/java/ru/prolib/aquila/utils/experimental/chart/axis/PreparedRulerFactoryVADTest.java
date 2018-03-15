package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class PreparedRulerFactoryVADTest {
	private IMocksControl control;
	private ValueAxisDriver driverMock;
	private ValueAxisDisplayMapper mapperMock;
	private ValueAxisRulerRenderer rendererMock;
	private PreparedRuler preparedRulerMock;
	private Object deviceStub;
	private PreparedRulerFactoryVAD service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		driverMock = control.createMock(ValueAxisDriver.class);
		mapperMock = control.createMock(ValueAxisDisplayMapper.class);
		rendererMock = control.createMock(ValueAxisRulerRenderer.class);
		preparedRulerMock = control.createMock(PreparedRuler.class);
		deviceStub = new Object();
		service = new PreparedRulerFactoryVAD(driverMock, mapperMock, deviceStub);
	}

	@Test (expected=IllegalArgumentException.class)
	public void testPrepareRuler_ThrowsIfWrongAxisID() {
		expect(driverMock.getID()).andReturn("zulu24");
		control.replay();
		
		service.prepareRuler(new RulerRendererID("foo", "bar"));
	}
	
	@Test
	public void testPrepareRuler() {
		expect(driverMock.getID()).andReturn("foo");
		expect(driverMock.getRenderer("bar")).andReturn(rendererMock);
		expect(rendererMock.prepareRuler(mapperMock, deviceStub)).andReturn(preparedRulerMock);
		control.replay();
		
		PreparedRuler actual = service.prepareRuler(new RulerRendererID("foo", "bar"));
		
		control.verify();
		assertSame(preparedRulerMock, actual);
	}

}
