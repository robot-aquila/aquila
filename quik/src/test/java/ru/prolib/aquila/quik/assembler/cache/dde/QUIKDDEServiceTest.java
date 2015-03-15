package ru.prolib.aquila.quik.assembler.cache.dde;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.util.*;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.dde.*;
import ru.prolib.aquila.dde.utils.table.*;
import ru.prolib.aquila.quik.assembler.cache.dde.QUIKDDEService;

public class QUIKDDEServiceTest {
	private IMocksControl control;
	private DDETableHandler handler1, handler2;
	private EditableTerminal terminal;
	private DDETable table1, table2;
	private QUIKDDEService service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		handler1 = control.createMock(DDETableHandler.class);
		handler2 = control.createMock(DDETableHandler.class);
		terminal = control.createMock(EditableTerminal.class);
		service = new QUIKDDEService("foobar", terminal);
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
		terminal.firePanicEvent(1, "Test error");
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
	
	static class FR {
		private final String name;
		private final DDETableHandler handler;
		FR(String name, DDETableHandler handler) {
			this.name = name;
			this.handler = handler;
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		EditableTerminal t1 = new TerminalImpl("foo"),
						 t2 = new TerminalImpl("foo");
		List<FR> rows1 = new Vector<FR>();
		rows1.add(new FR("first", handler1));
		rows1.add(new FR("second", handler2));
		List<FR> rows2 = new Vector<FR>();
		rows2.add(new FR("test", handler2));
		
		service = new QUIKDDEService("foobar", t1);
		for ( FR row : rows1 ) {
			service.setHandler(row.name, row.handler);
		}
		
		Variant<String> vName = new Variant<String>()
			.add("foobar")
			.add("zulu4");
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>(vName)
			.add(t1)
			.add(t2);
		Variant<List<FR>> vRows = new Variant<List<FR>>(vTerm)
			.add(rows1)
			.add(rows2);
		Variant<?> iterator = vRows;
		int foundCnt = 0;
		QUIKDDEService x, found = null;
		do {
			x = new QUIKDDEService(vName.get(), vTerm.get());
			for ( FR row : vRows.get() ) {
				x.setHandler(row.name, row.handler);
			}
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals("foobar", found.getName());
		assertSame(t1, found.getTerminal());
		Map<String, DDETableHandler> expected
			= new Hashtable<String, DDETableHandler>();
		expected.put("first", handler1);
		expected.put("second", handler2);
		assertEquals(expected, found.getHandlers());
	}
	
	@Test
	public void testClearHandlers() throws Exception {
		service.clearHandlers();
		assertEquals(0, service.getHandlers().size());
	}
	
}
