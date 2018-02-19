package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class RulerRendererRegistryImplTest {
	private IMocksControl control;
	private RulerRenderer rendererMock1, rendererMock2, rendererMock3;
	private LinkedHashMap<String, RulerRenderer> renderers;
	private RulerRendererRegistryImpl service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		rendererMock1 = control.createMock(RulerRenderer.class);
		rendererMock2 = control.createMock(RulerRenderer.class);
		rendererMock3 = control.createMock(RulerRenderer.class);
		renderers = new LinkedHashMap<>();
		service = new RulerRendererRegistryImpl(renderers);
	}
	
	@Test
	public void testRegisterRenderer() {
		expect(rendererMock1.getID()).andReturn("foo");
		control.replay();
		
		service.registerRenderer(rendererMock1);
		
		control.verify();
		assertSame(rendererMock1, renderers.get("foo"));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testRegisterRenderer_ThrowsIfAlreadyExists() {
		expect(rendererMock1.getID()).andReturn("foo");
		control.replay();
		renderers.put("foo", rendererMock2);
		
		service.registerRenderer(rendererMock1);
	}
	
	@Test
	public void testGetRenderer() {
		renderers.put("foo", rendererMock1);
		renderers.put("bar", rendererMock2);
		control.replay();
		
		assertSame(rendererMock1, service.getRenderer("foo"));
		assertSame(rendererMock2, service.getRenderer("bar"));
		
		control.verify();
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testGetRenderer_ThrowsIfNotExists() {
		control.replay();
		
		service.getRenderer("foo");
	}

	@Test
	public void testGetRendererIDs() {
		renderers.put("foo", rendererMock1);
		renderers.put("bar", rendererMock2);
		renderers.put("buz", rendererMock3);
		control.replay();
		
		List<String> actual = service.getRendererIDs();
		
		control.verify();
		List<String> expected = new ArrayList<>();
		expected.add("foo");
		expected.add("bar");
		expected.add("buz");
		assertEquals(expected, actual);
	}

}
