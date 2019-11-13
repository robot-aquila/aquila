package ru.prolib.aquila.ui.subman;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.BusinessEntities.TerminalRegistry;

public class SSDescRepoTest {
	private static EventQueue queue;
	private static Symbol symbol1, symbol2;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		queue = new EventQueueImpl();
		symbol1 = new Symbol("S:foo@EX1:USD");
		symbol2 = new Symbol("B:bar@EX2:RUR");
	}

	@Rule
	public ExpectedException eex = ExpectedException.none();
	private IMocksControl control;
	private Terminal termMock1, termMock2;
	private SubscrHandler hdrMock1, hdrMock2;
	private Map<String, Terminal> termMap;
	private TerminalRegistry registry;
	private AtomicInteger idSeq;
	private SSDescRepo service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		hdrMock1 = control.createMock(SubscrHandler.class);
		hdrMock2 = control.createMock(SubscrHandler.class);
		termMap = new LinkedHashMap<>();
		termMap.put("foo", termMock1 = control.createMock(Terminal.class));
		termMap.put("bar", termMock2 = control.createMock(Terminal.class));
		registry = new TerminalRegistry(termMap);
		idSeq = new AtomicInteger(1025);
		service = new SSDescRepo(registry, idSeq, new SSDescFactory(queue), "GAMU");
	}
	
	@Test
	public void testGetOrCreate_Throws() {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.getOrCreate(891);
	}
	
	@Test
	public void testRemove_Throws() {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.remove(891);
	}
	
	@Test
	public void testSubscribe() {
		expect(termMock1.subscribe(symbol1, MDLevel.L1_BBO)).andReturn(hdrMock1);
		control.replay();
		
		int actual_id = service.subscribe("foo", symbol1, MDLevel.L1_BBO);
		
		control.verify();
		assertEquals(1026, actual_id);
		assertEquals(1026, idSeq.get());
		SSDesc desc = service.getOrThrow(1026);
		assertEquals(1026, desc.getID());
		assertEquals("foo", desc.getTerminalID());
		assertEquals(symbol1, desc.getSymbol());
		assertEquals(MDLevel.L1_BBO, desc.getLevel());
		assertEquals(hdrMock1, desc.getHandler());
		assertEquals(5, desc.getContents().size());
	}
	
	@Test
	public void testUnsubscribe_IfNotExists() {
		control.replay();
		
		service.unsibscribe(19872);
		
		control.verify();
	}

	@Test
	public void testUnsubscribe() {
		expect(termMock2.subscribe(symbol2, MDLevel.L0)).andReturn(hdrMock2);
		control.replay();
		service.subscribe("bar", symbol2, MDLevel.L0);
		control.resetToStrict();
		hdrMock2.close();
		control.replay();
		
		service.unsibscribe(1026);
		
		control.verify();
		assertFalse(service.contains(1026));
	}

}
