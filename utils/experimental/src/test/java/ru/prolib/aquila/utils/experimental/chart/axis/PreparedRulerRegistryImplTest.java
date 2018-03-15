package ru.prolib.aquila.utils.experimental.chart.axis;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class PreparedRulerRegistryImplTest {
	private IMocksControl control;
	private PreparedRulerFactory factoryMock;
	private PreparedRuler preparedRulerMock1, preparedRulerMock2;
	private Map<RulerRendererID, PreparedRuler> rulersCache;
	private PreparedRulerRegistryImpl service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		factoryMock = control.createMock(PreparedRulerFactory.class);
		preparedRulerMock1 = control.createMock(PreparedRuler.class);
		preparedRulerMock2 = control.createMock(PreparedRuler.class);
		rulersCache = new LinkedHashMap<>();
		service = new PreparedRulerRegistryImpl(factoryMock, rulersCache);
	}

	@Test
	public void testGetPreparedRuler_RulerRendererID_NewPreparedRuler() {
		RulerRendererID rrid = new RulerRendererID("foo", "bar");
		expect(factoryMock.prepareRuler(rrid)).andReturn(preparedRulerMock1);
		control.replay();
		
		PreparedRuler actual = service.getPreparedRuler(rrid);
		
		control.verify();
		assertSame(preparedRulerMock1, actual);
		assertSame(preparedRulerMock1, rulersCache.get(rrid));
	}

	@Test
	public void testGetPreparedRuler_RulerRendererID_ExistingPreparedRuler() {
		RulerRendererID rrid1 = new RulerRendererID("foo", "bar"),
						rrid2 = new RulerRendererID("zoo", "baz");
		rulersCache.put(rrid1, preparedRulerMock1);
		rulersCache.put(rrid2, preparedRulerMock2);
		control.replay();
		
		assertSame(preparedRulerMock1, service.getPreparedRuler(rrid1));
		assertSame(preparedRulerMock2, service.getPreparedRuler(rrid2));
		
		control.verify();
	}

	@Test
	public void testGetPreparedRuler_RulerID_NewPreparedRuler() {
		RulerRendererID rrid = new RulerRendererID("foo", "bar");
		expect(factoryMock.prepareRuler(rrid)).andReturn(preparedRulerMock1);
		control.replay();
		
		PreparedRuler actual = service.getPreparedRuler(new RulerID(rrid, true));
		
		control.verify();
		assertSame(preparedRulerMock1, actual);
		assertSame(preparedRulerMock1, rulersCache.get(rrid));
	}

	@Test
	public void testGetPreparedRuler_RulerID_ExistingPreparedRuler() {
		RulerRendererID rrid1 = new RulerRendererID("foo", "bar"),
						rrid2 = new RulerRendererID("zoo", "baz");
		rulersCache.put(rrid1, preparedRulerMock1);
		rulersCache.put(rrid2, preparedRulerMock2);
		control.replay();
		
		assertSame(preparedRulerMock1, service.getPreparedRuler(new RulerID(rrid1, true)));
		assertSame(preparedRulerMock1, service.getPreparedRuler(new RulerID(rrid1, false)));
		assertSame(preparedRulerMock2, service.getPreparedRuler(new RulerID(rrid2, true)));
		assertSame(preparedRulerMock2, service.getPreparedRuler(new RulerID(rrid2, false)));
		
		control.verify();
	}

}
