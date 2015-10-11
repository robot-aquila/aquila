package ru.prolib.aquila.datatools.storage.moex;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.HashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.Scheduler;
import ru.prolib.aquila.core.BusinessEntities.Security;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.utils.BasicTerminalBuilder;
import ru.prolib.aquila.datatools.storage.SecuritySessionProperties;
import ru.prolib.aquila.datatools.storage.dao.RepositoryObjectNotFoundException;
import ru.prolib.aquila.datatools.storage.dao.SecurityPropertiesRepository;
import ru.prolib.aquila.datatools.storage.dao.SecuritySessionPropertiesRepository;
import ru.prolib.aquila.datatools.storage.dao.SymbolRepository;
import ru.prolib.aquila.datatools.storage.model.SecurityPropertiesEntity;
import ru.prolib.aquila.datatools.storage.model.SecuritySessionPropertiesEntity;
import ru.prolib.aquila.datatools.storage.model.SymbolEntity;

public class MOEXSecurityStorageServiceImplTest {
	private static final SecurityDescriptor descr1, descr2;
	
	static {
		descr1 = new SecurityDescriptor("RTS-12.15", "SPBFUT", "USD", SecurityType.FUT);
		descr2 = new SecurityDescriptor("GAZP", "EQBR", "RUR", SecurityType.STK);
	}
	
	private IMocksControl control;
	private SymbolRepository symbolRepositoryMock;
	private SecurityPropertiesRepository securityPropertiesRepositoryMock;
	private SecuritySessionPropertiesRepository securitySessionPropertiesRepositoryMock;
	private MOEXUtils moexUtilsMock;
	private Scheduler schedulerMock;
	private Map<SecurityDescriptor, SecuritySessionProperties> entityCache;
	private Map<SecurityDescriptor, Boolean> propertiesSaved;
	private EditableTerminal terminal;
	private Security security1, security2;
	private MOEXSecurityStorageServiceImpl service;
	private DateTime time1, time2;
	private SecuritySessionPropertiesEntity sessProps1, sessProps2;
	private SymbolEntity symbol1, symbol2;
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		symbolRepositoryMock = control.createMock(SymbolRepository.class);
		securityPropertiesRepositoryMock = control.createMock(SecurityPropertiesRepository.class);
		securitySessionPropertiesRepositoryMock = control.createMock(SecuritySessionPropertiesRepository.class);
		moexUtilsMock = control.createMock(MOEXUtils.class);
		schedulerMock = control.createMock(Scheduler.class);
		entityCache = new HashMap<SecurityDescriptor, SecuritySessionProperties>();
		propertiesSaved = new HashMap<SecurityDescriptor, Boolean>();
		terminal = new BasicTerminalBuilder()
			.withScheduler(schedulerMock)
			.buildTerminal();
		security1 = terminal.getEditableSecurity(descr1);
		security2 = terminal.getEditableSecurity(descr2);
		service = new MOEXSecurityStorageServiceImpl(moexUtilsMock, entityCache, propertiesSaved);
		service.setSymbolRepository(symbolRepositoryMock);
		service.setSecurityPropertiesRepository(securityPropertiesRepositoryMock);
		service.setSecuritySessionPropertiesRepository(securitySessionPropertiesRepositoryMock);
		// just helper objects
		time1 = new DateTime(2013, 1, 15, 13, 34, 48);
		time2 = new DateTime(2012, 1, 15, 18, 45, 0);
		sessProps1 = new SecuritySessionPropertiesEntity();
		sessProps2 = new SecuritySessionPropertiesEntity();
		symbol1 = new SymbolEntity();
		symbol2 = new SymbolEntity();
	}
	
	@Test
	public void testSnapshotSessionAttributes2_Case1() throws Exception {
		// Case 1:	constant security properties entity already checked
		// 			previously cached session properties must be rewritten
		propertiesSaved.put(descr1, true);
		entityCache.put(descr1, sessProps1);
		expect(securitySessionPropertiesRepositoryMock.createEntity()).andReturn(sessProps2);
		expect(symbolRepositoryMock.getByDescriptor(descr1)).andReturn(symbol1);
		moexUtilsMock.fillSessionProperties(security1, sessProps2);
		expect(moexUtilsMock.getClearingTime(descr1, time1)).andReturn(time2);
		securitySessionPropertiesRepositoryMock.save(sessProps2);
		control.replay();
		
		service.snapshotSessionAttributes(security1, time1);
		
		control.verify();
		assertSame(symbol1, sessProps2.getSymbol());
		assertSame(sessProps2, entityCache.get(descr1));
		assertEquals(time1, sessProps2.getSnapshotTime());
		assertEquals(time2, sessProps2.getClearingTime());
	}
	
	@Test
	public void testSnapshotSessionAttributes2_Case2() throws Exception {
		// Case 2:	constant security properties was not checked
		//			constant security properties entity is not written to DB
		//			session properties entity was not cached
		
		// Processing of constant security properties
		SecurityPropertiesEntity props2 = new SecurityPropertiesEntity();
		expect(securityPropertiesRepositoryMock.getByDescriptor(descr2))
			.andThrow(new RepositoryObjectNotFoundException("xxx"));
		expect(securityPropertiesRepositoryMock.createEntity()).andReturn(props2);
		expect(symbolRepositoryMock.getByDescriptor(descr2)).andReturn(symbol2);
		moexUtilsMock.fillProperties(security2, props2);
		securityPropertiesRepositoryMock.save(props2);
		// Processing of session properties
		expect(securitySessionPropertiesRepositoryMock.createEntity()).andReturn(sessProps2);
		expect(symbolRepositoryMock.getByDescriptor(descr2)).andReturn(symbol2);
		moexUtilsMock.fillSessionProperties(security2, sessProps2);
		expect(moexUtilsMock.getClearingTime(descr2, time1)).andReturn(time2);
		securitySessionPropertiesRepositoryMock.save(sessProps2);
		control.replay();
		
		service.snapshotSessionAttributes(security2, time1);
		
		control.verify();
		assertTrue(propertiesSaved.get(descr2));
		assertEquals(symbol2, props2.getSymbol());
		assertSame(sessProps2, entityCache.get(descr2));
		assertEquals(time1, sessProps2.getSnapshotTime());
		assertEquals(time2, sessProps2.getClearingTime());
	}
	
	@Test
	public void testSnapshotSessionAttributes2_Case3() throws Exception {
		// Case 3:	constant security properties was not checked
		//			constant security properties is written to DB
		//			session properties was not cached
		// Processing of constant security properties
		SecurityPropertiesEntity props1 = new SecurityPropertiesEntity();
		expect(securityPropertiesRepositoryMock.getByDescriptor(descr1)).andReturn(props1);
		// Processing of session properties
		expect(securitySessionPropertiesRepositoryMock.createEntity()).andReturn(sessProps1);
		expect(symbolRepositoryMock.getByDescriptor(descr1)).andReturn(symbol1);
		moexUtilsMock.fillSessionProperties(security1, sessProps1);
		expect(moexUtilsMock.getClearingTime(descr1, time1)).andReturn(time2);
		securitySessionPropertiesRepositoryMock.save(sessProps1);
		control.replay();
		
		service.snapshotSessionAttributes(security1, time1);
		
		control.verify();
		assertTrue(propertiesSaved.get(descr1));
		assertSame(sessProps1, entityCache.get(descr1));
		assertEquals(time1, sessProps1.getSnapshotTime());
		assertEquals(time2, sessProps1.getClearingTime());
	}
	
	@Test
	public void testSnapshotSessionAttributes1_Case1() throws Exception {
		// Case 1:	constant security properties already checked
		//			session properties entity was not cached
		propertiesSaved.put(descr1, true);
		expect(schedulerMock.getCurrentTime()).andReturn(time1);
		expect(securitySessionPropertiesRepositoryMock.createEntity()).andReturn(sessProps2);
		expect(symbolRepositoryMock.getByDescriptor(descr1)).andReturn(symbol1);
		moexUtilsMock.fillSessionProperties(security1, sessProps2);
		expect(moexUtilsMock.getClearingTime(descr1, time1)).andReturn(time2);
		securitySessionPropertiesRepositoryMock.save(sessProps2);
		control.replay();
		
		service.snapshotSessionAttributes(security1);
		
		control.verify();
		assertSame(sessProps2, entityCache.get(descr1));
		assertSame(symbol1, sessProps2.getSymbol());
		assertEquals(time1, sessProps2.getSnapshotTime());
		assertEquals(time2, sessProps2.getClearingTime());
	}

	@Test
	public void testSnapshotSessionAttributes1_Case2() throws Exception {
		// Case 2:	constant security properties already checked
		//			session properties entity was cached and not updated
		propertiesSaved.put(descr2, true);
		entityCache.put(descr2, sessProps1);
		expect(schedulerMock.getCurrentTime()).andReturn(time1);
		expect(securitySessionPropertiesRepositoryMock.createEntity()).andReturn(sessProps2);
		expect(symbolRepositoryMock.getByDescriptor(descr2)).andReturn(symbol2);
		moexUtilsMock.fillSessionProperties(security2, sessProps2);
		expect(moexUtilsMock.isPropertiesEquals(sessProps1, sessProps2)).andReturn(true);
		control.replay();
		
		service.snapshotSessionAttributes(security2);
		
		control.verify();
		assertSame(sessProps1, entityCache.get(descr2));
	}
	
	@Test
	public void testSnapshotSessionAttributes1_Case3() throws Exception {
		// Case 3:	constant security properties already checked
		//			session properties entity was cached and updated
		propertiesSaved.put(descr1, true);
		entityCache.put(descr1, sessProps1);
		expect(schedulerMock.getCurrentTime()).andReturn(time1);
		expect(securitySessionPropertiesRepositoryMock.createEntity()).andReturn(sessProps2);
		expect(symbolRepositoryMock.getByDescriptor(descr1)).andReturn(symbol1);
		moexUtilsMock.fillSessionProperties(security1, sessProps2);
		expect(moexUtilsMock.isPropertiesEquals(sessProps1, sessProps2)).andReturn(false);
		expect(moexUtilsMock.getClearingTime(descr1, time1)).andReturn(time2);
		securitySessionPropertiesRepositoryMock.save(sessProps2);
		control.replay();
		
		service.snapshotSessionAttributes(security1);
		
		control.verify();
		assertSame(sessProps2, entityCache.get(descr1));
		assertSame(symbol1, sessProps2.getSymbol());
		assertEquals(time1, sessProps2.getSnapshotTime());
		assertEquals(time2, sessProps2.getClearingTime());
	}

}
