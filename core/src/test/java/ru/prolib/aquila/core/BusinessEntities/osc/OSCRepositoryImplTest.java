package ru.prolib.aquila.core.BusinessEntities.osc;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.easymock.IAnswer;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.EventTypeImpl;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainer;
import ru.prolib.aquila.core.concurrency.LID;

public class OSCRepositoryImplTest {
	@Rule public ExpectedException eex = ExpectedException.none();
	private IMocksControl control;
	private Map<String, ObservableStateContainer> entityMap;
	private LID lidMock;
	private Lock lockMock;
	private OSCFactory<String, ObservableStateContainer> factoryMock;
	private ObservableStateContainer contMock1, contMock2, contMock3;
	private OSCRepositoryImpl<String, ObservableStateContainer> service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		entityMap = new LinkedHashMap<>();
		lidMock = control.createMock(LID.class);
		lockMock = control.createMock(Lock.class);
		factoryMock = control.createMock(OSCFactory.class);
		contMock1 = control.createMock(ObservableStateContainer.class);
		contMock2 = control.createMock(ObservableStateContainer.class);
		contMock3 = control.createMock(ObservableStateContainer.class);
		service = new OSCRepositoryImpl<>(entityMap, lidMock, lockMock, factoryMock, "repo-id");
	}
	
	@Test
	public void testEventTypes() {
		assertEquals("repo-id.ENTITY_UPDATE", service.onEntityUpdate().getId());
		assertEquals("repo-id.ENTITY_AVAILABLE", service.onEntityAvailable().getId());
		assertEquals("repo-id.ENTITY_CLOSE", service.onEntityClose().getId());
	}
	
	@Test
	public void testGetLID() {
		assertSame(lidMock, service.getLID());
	}
	
	@Test
	public void testLock() {
		lockMock.lock();
		control.replay();
		
		service.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		lockMock.unlock();
		control.replay();
		
		service.unlock();
		
		control.verify();
	}
	
	@Test
	public void testContains_Exists() {
		entityMap.put("foo", contMock1);
		entityMap.put("bar", contMock2);
		entityMap.put("boo", contMock3);
		lockMock.lock();
		lockMock.unlock();
		control.replay();

		assertTrue(service.contains("bar"));
		
		control.verify();
	}
	
	@Test
	public void testContains_NotExists() {
		lockMock.lock();
		lockMock.unlock();
		control.replay();
		
		assertFalse(service.contains("bar"));
		
		control.verify();
	}
	
	@Test
	public void testGetOrThrows_Exists() {
		entityMap.put("zulu24", contMock1);
		lockMock.lock();
		lockMock.unlock();
		control.replay();
		
		assertSame(contMock1, service.getOrThrow("zulu24"));
		
		control.verify();
	}
	
	@Test
	public void testGetOrThrows_NotExists() {
		eex.expect(IllegalArgumentException.class);
		eex.expectMessage("Entity not exists: zulu24");
		lockMock.lock();
		lockMock.unlock();
		control.replay();
		
		service.getOrThrow("zulu24");
	}
	
	@Test
	public void testGetOrCreate_Exists() {
		entityMap.put("kabu", contMock2);
		lockMock.lock();
		lockMock.unlock();
		control.replay();
		
		assertSame(contMock2, service.getOrCreate("kabu"));
		
		control.verify();
	}
	
	@Test
	public void testGetOrCreate_NotExists() {
		EventType onUpdate = new EventTypeImpl(),
				  onAvailable = new EventTypeImpl(),
				  onClose = new EventTypeImpl();
		lockMock.lock();
		expect(factoryMock.produce(service, "bumba")).andReturn(contMock3);
		expect(contMock3.onUpdate()).andStubReturn(onUpdate);
		expect(contMock3.onAvailable()).andStubReturn(onAvailable);
		expect(contMock3.onClose()).andStubReturn(onClose);
		lockMock.unlock();
		control.replay();
		
		assertSame(contMock3, service.getOrCreate("bumba"));
		
		control.verify();
		assertTrue(onUpdate.isAlternateType(service.onEntityUpdate));
		assertTrue(onAvailable.isAlternateType(service.onEntityAvailable));
		assertTrue(onClose.isAlternateType(service.onEntityClose));
		assertSame(contMock3, entityMap.get("bumba"));
	}
	
	@Test
	public void testRemove_Exists() {
		entityMap.put("foobar", contMock2);
		lockMock.lock();
		lockMock.unlock();
		contMock2.close();
		control.replay();
		
		assertTrue(service.remove("foobar"));
		
		control.verify();
		assertNull(entityMap.get("foobar"));
	}
	
	@Test
	public void testRemove_NotExists() {
		lockMock.lock();
		lockMock.unlock();
		control.replay();
		
		assertFalse(service.remove("foobar"));
		
		control.verify();
	}
	
	@Test
	public void testClose() {
		entityMap.put("one", contMock1);
		entityMap.put("two", contMock2);
		entityMap.put("gap", contMock3);
		lockMock.lock();
		lockMock.unlock();
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@Override
			public Void answer() throws Throwable {
				assertEquals(0, entityMap.size());
				return null;
			}
		});
		contMock1.close();
		contMock2.close();
		contMock3.close();
		control.replay();
		
		service.close();
		
		control.verify();
	}
	
	@Test
	public void testGetEntities() {
		entityMap.put("foo", contMock1);
		entityMap.put("bar", contMock2);
		entityMap.put("boo", contMock3);
		lockMock.lock();
		lockMock.unlock();
		control.replay();
		
		Collection<ObservableStateContainer> actual = service.getEntities();
		
		List<ObservableStateContainer> expected = new ArrayList<>();
		expected.add(contMock1);
		expected.add(contMock2);
		expected.add(contMock3);
		assertEquals(expected, actual);
	}

}
