package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TerminalRegistryTest {
	@Rule
	public ExpectedException eex = ExpectedException.none();
	private IMocksControl control;
	private Map<String, Terminal> mapStub;
	private Terminal termMock1, termMock2, termMock3;
	private TerminalRegistry service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		termMock1 = control.createMock(Terminal.class);
		termMock2 = control.createMock(Terminal.class);
		termMock3 = control.createMock(Terminal.class);
		mapStub = new LinkedHashMap<>();
		service = new TerminalRegistry(mapStub);
	}
	
	@Test
	public void testRegister_ThrowsIfAlreadyRegistered() {
		mapStub.put("foobar", termMock1);
		expect(termMock2.getTerminalID()).andReturn("foobar");
		control.replay();
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Terminal already registered: foobar");
		
		service.register(termMock2);
	}
	
	@Test
	public void testRegister() {
		mapStub.put("foo", termMock1);
		mapStub.put("bar", termMock2);
		expect(termMock3.getTerminalID()).andReturn("buz");
		control.replay();
		
		service.register(termMock3);
		
		control.verify();
		Map<String, Terminal> expected = new LinkedHashMap<>();
		expected.put("foo", termMock1);
		expected.put("bar", termMock2);
		expected.put("buz", termMock3);
		assertEquals(expected, mapStub);
	}
	
	@Test
	public void testGet() {
		mapStub.put("foo", termMock1);
		mapStub.put("bar", termMock2);
		mapStub.put("buz", termMock3);
		control.replay();
		
		assertSame(termMock1, service.get("foo"));
		assertSame(termMock2, service.get("bar"));
		assertSame(termMock3, service.get("buz"));
		
		control.verify();
	}
	
	@Test
	public void testGet_ThrowsIfNotExists() {
		mapStub.put("foo", termMock1);
		mapStub.put("bar", termMock2);
		control.replay();
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Terminal is not registered: buz");
		
		service.get("buz");
	}

	@Test
	public void testGetListIDs() {
		mapStub.put("foo", termMock1);
		mapStub.put("bar", termMock2);
		mapStub.put("buz", termMock3);
		control.replay();
		
		List<String> actual = service.getListIDs();
		
		List<String> expected = new ArrayList<>();
		expected.add("foo");
		expected.add("bar");
		expected.add("buz");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetDefault_TheFirstOneByDefault() {
		expect(termMock1.getTerminalID()).andStubReturn("zulu");
		expect(termMock2.getTerminalID()).andStubReturn("charlie");
		expect(termMock3.getTerminalID()).andStubReturn("delta");
		control.replay();
		service.register(termMock1);
		service.register(termMock2);
		service.register(termMock3);
		control.verify();
		
		assertSame(termMock1, service.getDefault());
	}
	
	@Test
	public void testGetDefault_DefinedByID() {
		expect(termMock1.getTerminalID()).andStubReturn("zulu");
		expect(termMock2.getTerminalID()).andStubReturn("charlie");
		expect(termMock3.getTerminalID()).andStubReturn("delta");
		control.replay();
		service.register(termMock1);
		service.register(termMock2);
		service.register(termMock3);
		control.verify();
		service.setDefaultID("charlie");

		assertSame(termMock2, service.getDefault());
	}
	
	@Test
	public void testGetDefault_ThrowsIfNoTerminal() {
		control.replay();
		eex.expect(IllegalStateException.class);
		eex.expectMessage("No terminal available");
		
		service.getDefault();
	}
	
	@Test
	public void testSetDefaultID_ThrowsIfNotRegistered() {
		expect(termMock1.getTerminalID()).andStubReturn("zulu");
		expect(termMock2.getTerminalID()).andStubReturn("charlie");
		expect(termMock3.getTerminalID()).andStubReturn("delta");
		control.replay();
		service.register(termMock1);
		service.register(termMock2);
		service.register(termMock3);
		control.verify();
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Terminal is not registered: bobby");
		
		service.setDefaultID("bobby");
	}

}
