package ru.prolib.aquila.core.concurrency;

import static org.junit.Assert.*;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.BusinessEntity;
import ru.prolib.aquila.core.concurrency.EventSuppressor;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.concurrency.Lockable;
import ru.prolib.aquila.core.utils.Variant;

public class EventSuppressorTest {
	private IMocksControl control;
	private LID lid1, lid2;
	private BusinessEntity entityMock1, entityMock2, entityMock3;
	private LinkedHashSet<BusinessEntity> set1, set2;
	private Lockable multilockMock1, multilockMock2;
	private EventSuppressor suppressor;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		lid1 = LID.createInstance();
		lid2 = LID.createInstance();
		entityMock1 = control.createMock(BusinessEntity.class);
		entityMock2 = control.createMock(BusinessEntity.class);
		entityMock3 = control.createMock(BusinessEntity.class);
		expect(entityMock1.getLID()).andStubReturn(LID.createInstance());
		expect(entityMock2.getLID()).andStubReturn(LID.createInstance());
		expect(entityMock3.getLID()).andStubReturn(LID.createInstance());
		multilockMock1 = control.createMock(Lockable.class);
		multilockMock2 = control.createMock(Lockable.class);
		set1 = new LinkedHashSet<>();
		set1.add(entityMock1);
		set1.add(entityMock2);
		set1.add(entityMock3);
		set2 = new LinkedHashSet<>();
		set2.add(entityMock2);
		set2.add(entityMock1);
		suppressor = new EventSuppressor(lid1, set1, multilockMock1);
	}
	
	@Test
	public void testCtor1() {
		control.replay();
		suppressor = new EventSuppressor(set2);
		LID.isLastCreatedLID(suppressor.getLID());
	}
	
	@Test
	public void testLock() {
		multilockMock1.lock();
		entityMock1.suppressEvents();
		entityMock2.suppressEvents();
		entityMock3.suppressEvents();
		control.replay();
		
		suppressor.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		multilockMock1.unlock();
		entityMock1.restoreEvents();
		entityMock2.restoreEvents();
		entityMock3.restoreEvents();
		control.replay();
		
		suppressor.unlock();
		
		control.verify();
	}

	@Test
	public void testEquals_SpecialCases() {
		assertTrue(suppressor.equals(suppressor));
		assertFalse(suppressor.equals(null));
		assertFalse(suppressor.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<LID> vLid = new Variant<>(lid1, lid2);
		Variant<Set<BusinessEntity>> vSet = new Variant<>(vLid, set1, set2);
		Variant<Lockable> vMLock = new Variant<>(vSet, multilockMock1, multilockMock2);
		Variant<?> iterator = vMLock;
		int foundCnt = 0;
		EventSuppressor x, found = null;
		do {
			x = new EventSuppressor(vLid.get(), vSet.get(), vMLock.get());
			if ( suppressor.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(lid1, found.getLID());
		assertEquals(set1, found.getObjects());
		assertSame(multilockMock1, found.getMultilock());
	}

}
