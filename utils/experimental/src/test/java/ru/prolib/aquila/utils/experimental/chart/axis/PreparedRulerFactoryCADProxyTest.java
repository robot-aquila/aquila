package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;

import org.easymock.IMocksControl;

import static org.easymock.EasyMock.*;

import org.junit.Before;
import org.junit.Test;

public class PreparedRulerFactoryCADProxyTest {
	private IMocksControl control;
	private CategoryAxisDriverProxy proxyMock;
	private CategoryAxisDisplayMapper mapperMock;
	private CategoryAxisRulerRenderer rendererMock;
	private PreparedRuler preparedRulerMock;
	private Object deviceStub;
	private PreparedRulerFactoryCADProxy service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		proxyMock = control.createMock(CategoryAxisDriverProxy.class);
		mapperMock = control.createMock(CategoryAxisDisplayMapper.class);
		rendererMock = control.createMock(CategoryAxisRulerRenderer.class);
		preparedRulerMock = control.createMock(PreparedRuler.class);
		deviceStub = new Object();
		service = new PreparedRulerFactoryCADProxy(proxyMock, mapperMock, deviceStub);
	}

	@Test
	public void testPrepareRuler() {
		expect(proxyMock.getRulerRenderer("foo")).andReturn(rendererMock);
		expect(rendererMock.prepareRuler(mapperMock, deviceStub)).andReturn(preparedRulerMock);
		control.replay();
		
		PreparedRuler actual = service.prepareRuler(new RulerRendererID("zulu", "foo"));
		
		control.verify();
		assertSame(preparedRulerMock, actual);
	}

}
