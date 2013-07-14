package ru.prolib.aquila.quik.assembler.cache.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.FirePanicEvent;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.dde.*;
import ru.prolib.aquila.dde.utils.table.*;
import ru.prolib.aquila.quik.assembler.cache.dde.QUIKDDEService;

public class QUIKDDEServiceTest {
	private IMocksControl control;
	private DDETableHandler handler1, handler2;
	private FirePanicEvent panic;
	private DDETable table1, table2;
	private QUIKDDEService service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		handler1 = control.createMock(DDETableHandler.class);
		handler2 = control.createMock(DDETableHandler.class);
		panic = control.createMock(FirePanicEvent.class);
		service = new QUIKDDEService("foobar", panic);
		service.setHandler("first", handler1);
		service.setHandler("second", handler2);
		table1 = new DDETableImpl(new Object[] { 1, 2 }, "second", "item", 2);
		table2 = new DDETableImpl(new Object[] { 1, 2 }, "foobar", "item", 2);
	}
	
	@Test
	public void testOnConnect() throws Exception {
		control.replay();
		
		assertFalse(service.onConnect("foobar"));
		assertTrue(service.onConnect("first"));
		assertTrue(service.onConnect("second"));
		
		control.verify();
	}
	
	@Test
	public void testOnConnectConfirm() throws Exception {
		control.replay();
		
		service.onConnectConfirm("foobar");
		
		control.verify();
	}
	
	@Test
	public void testOnData() throws Exception {
		byte dataBuffer[] = { 1, 2, 3 };
		control.replay();
		
		assertFalse(service.onData("foobar", "item", dataBuffer));
		
		control.verify();
	}
	
	@Test
	public void testOnDisconnect() throws Exception {
		control.replay();
		
		service.onDisconnect("foobar");
		
		control.verify();
	}
	
	@Test
	public void testOnRegister() throws Exception {
		control.replay();
		
		service.onRegister();
		
		control.verify();
	}
	
	@Test
	public void testOnUnregister() throws Exception {
		control.replay();
		
		service.onUnregister();
		
		control.verify();
	}
	
	@Test
	public void testOnTable_IfHandlerNotExists() throws Exception {
		control.replay();
		
		service.onTable(table2);
		
		control.verify();
	}
	
	@Test
	public void testOnTable_IfHandlerExists() throws Exception {
		handler2.handle(same(table1));
		control.replay();
		
		service.onTable(table1);
		
		control.verify();
	}
	
	@Test
	public void testOnTable_IfHandlerExistsAndThrows() throws Exception {
		handler2.handle(same(table1));
		expectLastCall().andThrow(new DDEException("Test error"));
		panic.firePanicEvent(1, "Test error");
		control.replay();
		
		service.onTable(table1);
		
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vName = new Variant<String>()
			.add("foobar")
			.add("zulu4");
		Variant<FirePanicEvent> vPanic = new Variant<FirePanicEvent>(vName)
			.add(panic)
			.add(control.createMock(FirePanicEvent.class));
		Map<String,DDETableHandler> map1=new HashMap<String,DDETableHandler>();
		map1.put("first", handler1);
		map1.put("second", handler2);
		Map<String,DDETableHandler> map2=new HashMap<String,DDETableHandler>();
		map2.put("test", handler2);
		Variant<Map<String, DDETableHandler>> vHandlers =
				new Variant<Map<String, DDETableHandler>>(vPanic)
			.add(map1)
			.add(map2);
		Variant<?> iterator = vHandlers;
		int foundCnt = 0;
		QUIKDDEService x = null, found = null;
		Iterator<Map.Entry<String, DDETableHandler>> it;
		Map.Entry<String, DDETableHandler> entry;
		do {
			x = new QUIKDDEService(vName.get(), vPanic.get());
			it = vHandlers.get().entrySet().iterator();
			while ( it.hasNext() ) {
				entry = it.next();
				x.setHandler(entry.getKey(), entry.getValue());
			}
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("foobar", found.getName());
		assertSame(panic, found.getFirePanicEvent());
		assertEquals(map1, found.getHandlers());
	}
	
}
