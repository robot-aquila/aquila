package ru.prolib.aquila.quik.dde;

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

public class CacheServiceTest {
	private IMocksControl control;
	private DDEService underlyingService;
	private DDETableHandler handler1, handler2;
	private FirePanicEvent panic;
	private DDETable table1, table2;
	private CacheService service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		underlyingService = control.createMock(DDEService.class);
		handler1 = control.createMock(DDETableHandler.class);
		handler2 = control.createMock(DDETableHandler.class);
		panic = control.createMock(FirePanicEvent.class);
		service = new CacheService("foobar", panic, underlyingService);
		service.setHandler("first", handler1);
		service.setHandler("second", handler2);
		table1 = new DDETableImpl(new Object[] { 1, 2 }, "second", "item", 2);
		table2 = new DDETableImpl(new Object[] { 1, 2 }, "foobar", "item", 2);
	}
	
	@Test
	public void testOnConnect() throws Exception {
		// TODO: после выпила базового сервиса реализовать проверку регистрации
		// указанной таблицы. Отклонять запросы на подключение неизвестных
		// таблиц.
		expect(underlyingService.onConnect("foobar")).andReturn(true);
		control.replay();
		
		assertTrue(service.onConnect("foobar"));
		
		control.verify();
	}
	
	@Test
	public void testOnConnectConfirm() throws Exception {
		underlyingService.onConnectConfirm("foobar");
		control.replay();
		
		service.onConnectConfirm("foobar");
		
		control.verify();
	}
	
	@Test
	public void testOnData() throws Exception {
		byte dataBuffer[] = { 1, 2, 3 };
		expect(underlyingService.onData(eq("foobar"), eq("item"),
				aryEq(dataBuffer))).andReturn(true); // не важно что он вернет,
													// мы хотим таблицу
		control.replay();
		
		assertFalse(service.onData("foobar", "item", dataBuffer));
		
		control.verify();
	}
	
	@Test
	public void testOnDisconnect() throws Exception {
		underlyingService.onDisconnect("foobar");
		control.replay();
		
		service.onDisconnect("foobar");
		
		control.verify();
	}
	
	@Test
	public void testOnRegister() throws Exception {
		underlyingService.onRegister();
		control.replay();
		
		service.onRegister();
		
		control.verify();
	}
	
	@Test
	public void testOnUnregister() throws Exception {
		underlyingService.onUnregister();
		control.replay();
		
		service.onUnregister();
		
		control.verify();
	}
	
	@Test
	public void testOnTable_IfHandlerNotExists() throws Exception {
		underlyingService.onTable(same(table2));
		control.replay();
		
		service.onTable(table2);
		
		control.verify();
	}
	
	@Test
	public void testOnTable_IfHandlerExists() throws Exception {
		handler2.handle(same(table1));
		underlyingService.onTable(same(table1));
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
		Variant<DDEService> vUnder = new Variant<DDEService>(vName)
			.add(underlyingService)
			.add(control.createMock(DDEService.class));
		Map<String,DDETableHandler> map1=new HashMap<String,DDETableHandler>();
		map1.put("first", handler1);
		map1.put("second", handler2);
		Map<String,DDETableHandler> map2=new HashMap<String,DDETableHandler>();
		map2.put("test", handler2);
		Variant<Map<String, DDETableHandler>> vHandlers =
				new Variant<Map<String, DDETableHandler>>(vUnder)
			.add(map1)
			.add(map2);
		Variant<?> iterator = vHandlers;
		int foundCnt = 0;
		CacheService x = null, found = null;
		Iterator<Map.Entry<String, DDETableHandler>> it;
		Map.Entry<String, DDETableHandler> entry;
		do {
			x = new CacheService(vName.get(), vPanic.get(), vUnder.get());
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
		assertSame(underlyingService, found.getUnderlyingService());
		assertEquals(map1, found.getHandlers());
	}
	
}
