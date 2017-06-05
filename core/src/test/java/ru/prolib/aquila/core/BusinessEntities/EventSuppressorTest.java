package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.EventProducer;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.utils.Variant;

public class EventSuppressorTest {
	private IMocksControl control;
	private LID lid1, lid2;
	private EventProducer producerMock1, producerMock2, producerMock3;
	private LinkedHashSet<EventProducer> set1, set2;
	private EventSuppressor suppressor;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		lid1 = LID.createInstance();
		lid2 = LID.createInstance();
		producerMock1 = control.createMock(EventProducer.class);
		producerMock2 = control.createMock(EventProducer.class);
		producerMock3 = control.createMock(EventProducer.class);
		set1 = new LinkedHashSet<>();
		set1.add(producerMock1);
		set1.add(producerMock2);
		set1.add(producerMock3);
		set2 = new LinkedHashSet<>();
		set2.add(producerMock2);
		set2.add(producerMock1);
		suppressor = new EventSuppressor(lid1, set1);
	}
	
	@Test
	public void testCtor1() {
		suppressor = new EventSuppressor(set2);
		LID.isLastCreatedLID(suppressor.getLID());
	}
	
	@Test
	public void testLock() {
		producerMock1.suppressEvents();
		producerMock2.suppressEvents();
		producerMock3.suppressEvents();
		control.replay();
		
		suppressor.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		producerMock1.restoreEvents();
		producerMock2.restoreEvents();
		producerMock3.restoreEvents();
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
		Variant<Set<EventProducer>> vSet = new Variant<>(set1, set2);
		Variant<?> iterator = vSet;
		int foundCnt = 0;
		EventSuppressor x, found = null;
		do {
			x = new EventSuppressor(vLid.get(), vSet.get());
			if ( suppressor.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(lid1, found.getLID());
		assertEquals(set1, found.getObjects());
	}

}
