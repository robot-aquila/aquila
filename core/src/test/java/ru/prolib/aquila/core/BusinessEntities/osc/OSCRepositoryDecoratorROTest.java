package ru.prolib.aquila.core.BusinessEntities.osc;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.ObservableStateContainer;
import ru.prolib.aquila.core.concurrency.LID;

public class OSCRepositoryDecoratorROTest {
	@Rule
	public ExpectedException eex = ExpectedException.none();
	private IMocksControl control;
	private OSCRepository<Integer, ObservableStateContainer> repoMock;
	private ObservableStateContainer contMock1, contMock2, contMock3;
	private EventType typeMock;
	private OSCRepositoryDecoratorRO<Integer, ObservableStateContainer> service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		repoMock = control.createMock(OSCRepository.class);
		contMock1 = control.createMock(ObservableStateContainer.class);
		contMock2 = control.createMock(ObservableStateContainer.class);
		contMock3 = control.createMock(ObservableStateContainer.class);
		typeMock = control.createMock(EventType.class);
		service = new OSCRepositoryDecoratorRO<>(repoMock);
	}
	
	@Test
	public void testGetLID() {
		LID lidMock = control.createMock(LID.class);
		expect(repoMock.getLID()).andReturn(lidMock);
		control.replay();
		
		assertSame(lidMock, service.getLID());
		
		control.verify();
	}
	
	@Test
	public void testLock() {
		repoMock.lock();
		control.replay();
		
		service.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		repoMock.unlock();
		control.replay();
		
		service.unlock();
		
		control.verify();
	}
	
	@Test
	public void testContains() {
		expect(repoMock.contains(222)).andReturn(true);
		expect(repoMock.contains(444)).andReturn(false);
		control.replay();
		
		assertTrue(service.contains(222));
		assertFalse(service.contains(444));
		
		control.verify();
	}
	
	@Test
	public void testGetOrThrow() {
		expect(repoMock.getOrThrow(876)).andReturn(contMock1);
		control.replay();
		
		assertSame(contMock1, service.getOrThrow(876));
		
		control.verify();
	}
	
	@Test
	public void testGetOrCreate() {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.getOrCreate(725);
	}
	
	@Test
	public void testRemove() {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.remove(555);
	}
	
	@Test
	public void testClose() {
		eex.expect(UnsupportedOperationException.class);
		control.replay();
		
		service.close();
	}
	
	@Test
	public void testGetEntities() {
		Collection<ObservableStateContainer> list = new ArrayList<>();
		list.add(contMock1);
		list.add(contMock2);
		list.add(contMock3);
		expect(service.getEntities()).andReturn(list);
		control.replay();
		
		assertSame(list, service.getEntities());
		
		control.verify();
	}
	
	@Test
	public void testOnEntityUpdate() {
		expect(repoMock.onEntityUpdate()).andReturn(typeMock);
		control.replay();
		
		assertSame(typeMock, service.onEntityUpdate());
		
		control.verify();
	}

	@Test
	public void testOnEntityAvailable() {
		expect(repoMock.onEntityAvailable()).andReturn(typeMock);
		control.replay();
		
		assertSame(typeMock, service.onEntityAvailable());
		
		control.verify();
	}
	
	@Test
	public void testOnEntityClose() {
		expect(repoMock.onEntityClose()).andReturn(typeMock);
		control.replay();
		
		assertSame(typeMock, service.onEntityClose());
		
		control.verify();
	}

}
