package ru.prolib.aquila.dde.utils;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.dde.DDETable;

public class DDEObservableServiceImplTest {
	private IMocksControl control;
	private EventDispatcher dispatcher;
	private EventType etConnect,etDisconnect,etRegister,etUnregister;
	private EventType etData,etTable;
	private DDEAccessControl access;
	private DDEObservableServiceImpl service;
	private DDETable table;
	private EventSystem eventSystem;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		eventSystem = control.createMock(EventSystem.class);
		table = control.createMock(DDETable.class);
		dispatcher = control.createMock(EventDispatcher.class);
		etConnect = control.createMock(EventType.class);
		etDisconnect = control.createMock(EventType.class);
		etRegister = control.createMock(EventType.class);
		etUnregister = control.createMock(EventType.class);
		etData = control.createMock(EventType.class);
		etTable = control.createMock(EventType.class);
		access = control.createMock(DDEAccessControl.class);
		service = new DDEObservableServiceImpl("name", access, dispatcher,
				etConnect, etDisconnect, etRegister, etUnregister,
				etData, etTable);
	}
	
	@After
	public void tearDown() throws Exception {

	}
	
	@Test
	public void testConstruct_Ok() throws Exception {
		assertEquals("name", service.getName());
		assertSame(access, service.getAccessControl());
		assertSame(dispatcher, service.getEventDispatcher());
		assertSame(etConnect, service.OnConnect());
		assertSame(etDisconnect, service.OnDisconnect());
		assertSame(etRegister, service.OnRegister());
		assertSame(etUnregister, service.OnUnregister());
		assertSame(etData, service.OnData());
		assertSame(etTable, service.OnTable());
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfServiceIsNull() throws Exception {
		new DDEObservableServiceImpl(null, access, dispatcher,
				etConnect, etDisconnect, etRegister, etUnregister,
				etData, etTable);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfAccessControlIsNull() throws Exception {
		new DDEObservableServiceImpl("name", null, dispatcher,
				etConnect, etDisconnect, etRegister, etUnregister,
				etData, etTable);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfEventDispatcherIsNull() throws Exception {
		new DDEObservableServiceImpl("name", access, null,
				etConnect, etDisconnect, etRegister, etUnregister,
				etData, etTable);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfOnConnectIsNull() throws Exception {
		new DDEObservableServiceImpl("name", access, dispatcher,
				null, etDisconnect, etRegister, etUnregister,
				etData, etTable);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfOnDisconnectIsNull() throws Exception {
		new DDEObservableServiceImpl("name", access, dispatcher,
				etConnect, null, etRegister, etUnregister,
				etData, etTable);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfOnRegisterIsNull() throws Exception {
		new DDEObservableServiceImpl("name", access, dispatcher,
				etConnect, etDisconnect, null, etUnregister,
				etData, etTable);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfOnUnregisterIsNull() throws Exception {
		new DDEObservableServiceImpl("name", access, dispatcher,
				etConnect, etDisconnect, etRegister, null,
				etData, etTable);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfOnDataIsNull() throws Exception {
		new DDEObservableServiceImpl("name", access, dispatcher,
				etConnect, etDisconnect, etRegister, etUnregister,
				null, etTable);
	}
	
	@Test (expected=NullPointerException.class)
	public void testConstruct_ThrowsIfOnTableIsNull() throws Exception {
		new DDEObservableServiceImpl("name", access, dispatcher,
				etConnect, etDisconnect, etRegister, etUnregister,
				etData, null);
	}
	
	@Test
	public void testOnRegister() throws Exception {
		DDEEvent expected = new DDEEvent(etRegister, "name");
		dispatcher.dispatch(eq(expected));
		control.replay();
		
		service.onRegister();
		
		control.verify();
	}
	
	@Test
	public void testOnUnregister() throws Exception {
		DDEEvent expected = new DDEEvent(etUnregister, "name");
		dispatcher.dispatch(eq(expected));
		control.replay();
		
		service.onUnregister();
		
		control.verify();
	}
	
	@Test
	public void testOnConnect_IfAllowed() throws Exception {
		expect(access.isAllowed(eq(new DDEAccessSubject("name", "bar"))))
			.andReturn(true);
		control.replay();
		
		assertTrue(service.onConnect("bar"));
		
		control.verify();
	}
	
	@Test
	public void testOnConnect_IfRejected() throws Exception {
		expect(access.isAllowed(eq(new DDEAccessSubject("name", "foo"))))
			.andReturn(false);
		control.replay();
	
		assertFalse(service.onConnect("foo"));
	
		control.verify();
	}
	
	@Test
	public void testOnConnectConfirm() throws Exception {
		DDETopicEvent expected = new DDETopicEvent(etConnect, "name", "table");
		dispatcher.dispatch(eq(expected));
		control.replay();
		
		service.onConnectConfirm("table");
		
		control.verify();
	}
	
	@Test
	public void testOnDisconnectConfirm() throws Exception {
		DDETopicEvent expected = new DDETopicEvent(etDisconnect, "name", "ups");
		dispatcher.dispatch(eq(expected));
		control.replay();
		
		service.onDisconnect("ups");
		
		control.verify();
	}
	
	@Test
	public void testOnData() throws Exception {
		byte[] bin = new byte[128];
		DDEDataEvent expected = new DDEDataEvent(etData, "name", "A", "B", bin); 
		dispatcher.dispatch(eq(expected));
		control.replay();
		
		assertFalse(service.onData("A", "B", bin));
		
		control.verify();
	}
	
	@Test
	public void testOnTable() throws Exception {
		DDETableEvent expected = new DDETableEvent(etTable, "name", table);
		dispatcher.dispatch(eq(expected));
		control.replay();
		
		service.onTable(table);
		
		control.verify();
	}
	
	@Test
	public void testCreateService3() throws Exception {
		expect(eventSystem.createEventDispatcher()).andReturn(dispatcher);
		expect(dispatcher.createType()).andReturn(etConnect);
		expect(dispatcher.createType()).andReturn(etDisconnect);
		expect(dispatcher.createType()).andReturn(etRegister);
		expect(dispatcher.createType()).andReturn(etUnregister);
		expect(dispatcher.createType()).andReturn(etData);
		expect(dispatcher.createType()).andReturn(etTable);
		control.replay();
		
		service = (DDEObservableServiceImpl) DDEObservableServiceImpl
			.createService("zulu", access, eventSystem);
		
		control.verify();
		assertNotNull(service);
		assertEquals("zulu", service.getName());
		assertSame(dispatcher, service.getEventDispatcher());
		assertSame(etConnect, service.OnConnect());
		assertSame(etDisconnect, service.OnDisconnect());
		assertSame(etRegister, service.OnRegister());
		assertSame(etUnregister, service.OnUnregister());
		assertSame(etData, service.OnData());
		assertSame(etTable, service.OnTable());
		assertSame(access, service.getAccessControl());
	}
	
	@Test
	public void testCreateService2() throws Exception {
		expect(eventSystem.createEventDispatcher()).andReturn(dispatcher);
		expect(dispatcher.createType()).andReturn(etConnect);
		expect(dispatcher.createType()).andReturn(etDisconnect);
		expect(dispatcher.createType()).andReturn(etRegister);
		expect(dispatcher.createType()).andReturn(etUnregister);
		expect(dispatcher.createType()).andReturn(etData);
		expect(dispatcher.createType()).andReturn(etTable);
		control.replay();
		
		service = (DDEObservableServiceImpl) DDEObservableServiceImpl
			.createService("zulu", eventSystem);
		
		control.verify();
		assertNotNull(service);
		assertEquals("zulu", service.getName());
		assertSame(dispatcher, service.getEventDispatcher());
		assertSame(etConnect, service.OnConnect());
		assertSame(etDisconnect, service.OnDisconnect());
		assertSame(etRegister, service.OnRegister());
		assertSame(etUnregister, service.OnUnregister());
		assertSame(etData, service.OnData());
		assertSame(etTable, service.OnTable());
		assertEquals(new DDEAllowAllAccess(), service.getAccessControl());
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<String> vName = new Variant<String>()
			.add("name")
			.add("zulu");
		Variant<DDEAccessControl> vAcc = new Variant<DDEAccessControl>(vName)
			.add(access)
			.add(control.createMock(DDEAccessControl.class));
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>(vAcc)
			.add(dispatcher)
			.add(control.createMock(EventDispatcher.class));
		Variant<EventType> vCon = new Variant<EventType>(vDisp)
			.add(etConnect)
			.add(control.createMock(EventType.class));
		Variant<EventType> vDisc = new Variant<EventType>(vCon)
			.add(etDisconnect)
			.add(control.createMock(EventType.class));
		Variant<EventType> vReg = new Variant<EventType>(vDisc)
			.add(etRegister)
			.add(control.createMock(EventType.class));
		Variant<EventType> vUnr = new Variant<EventType>(vReg)
			.add(etUnregister)
			.add(control.createMock(EventType.class));
		Variant<EventType> vDat = new Variant<EventType>(vUnr)
			.add(etData)
			.add(control.createMock(EventType.class));
		Variant<EventType> vTab = new Variant<EventType>(vDat)
			.add(etTable)
			.add(control.createMock(EventType.class));
		int foundCnt = 0;
		DDEObservableServiceImpl found = null, x = null;
		do {
			x = new DDEObservableServiceImpl(vName.get(), vAcc.get(),
					vDisp.get(), vCon.get(), vDisc.get(), vReg.get(),
					vUnr.get(), vDat.get(), vTab.get());
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( vTab.next() );
		assertEquals(1, foundCnt);
		assertEquals("name", found.getName());
		assertSame(access, found.getAccessControl());
		assertSame(dispatcher, found.getEventDispatcher());
		assertSame(etConnect, found.OnConnect());
		assertSame(etDisconnect, found.OnDisconnect());
		assertSame(etRegister, found.OnRegister());
		assertSame(etUnregister, found.OnUnregister());
		assertSame(etData, found.OnData());
		assertSame(etTable, found.OnTable());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121107, /*0*/65919)
			.append("name")
			.append(access)
			.append(dispatcher)
			.append(etConnect)
			.append(etDisconnect)
			.append(etRegister)
			.append(etUnregister)
			.append(etData)
			.append(etTable)
			.toHashCode();
		assertEquals(hashCode, service.hashCode());
	}

}
