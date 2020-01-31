package ru.prolib.aquila.data;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.concurrency.LID;

public class DFGroupRepoTest {
	private static final int FEED1 = 11;
	private static final int FEED2 = 12;
	private static final int FEED3 = 13;
	
	private IMocksControl control;
	private LID lidMock;
	private Lock lockMock;
	private Map<String, DFGroup<String, Integer>> groups;
	private DFGroupFactory<String, Integer> factoryMock;
	private DFGroupRepo<String, Integer> service;
	private DFGroup<String, Integer> gMock1, gMock2, gMock3, gMock4, gMock5;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		lidMock = control.createMock(LID.class);
		lockMock = control.createMock(Lock.class);
		groups = new LinkedHashMap<>();
		factoryMock = control.createMock(DFGroupFactory.class);
		service = new DFGroupRepo<>(lidMock, lockMock, groups, factoryMock);
		gMock1 = control.createMock(DFGroup.class);
		gMock2 = control.createMock(DFGroup.class);
		gMock3 = control.createMock(DFGroup.class);
		gMock4 = control.createMock(DFGroup.class);
		gMock5 = control.createMock(DFGroup.class);
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
	public void testGetPendingSubscr() {
		groups.put("foo", gMock1);
		groups.put("bar", gMock2);
		groups.put("buz", gMock3);
		lockMock.lock();
		expect(gMock1.getFeedStatus(FEED2)).andReturn(DFSubscrStatus.PENDING_SUBSCR);
		expect(gMock2.getFeedStatus(FEED2)).andReturn(DFSubscrStatus.NOT_SUBSCR);
		expect(gMock3.getFeedStatus(FEED2)).andReturn(DFSubscrStatus.PENDING_SUBSCR);
		lockMock.unlock();
		control.replay();
		
		Collection<String> actual = service.getPendingSubscr(FEED2);
		
		control.verify();
		assertEquals(Arrays.asList("foo", "buz"), actual);
	}
	
	@Test
	public void testGetSubscribed() {
		groups.put("foo", gMock1);
		groups.put("bar", gMock2);
		groups.put("buz", gMock3);
		groups.put("lol", gMock4);
		groups.put("dup", gMock5);
		lockMock.lock();
		expect(gMock1.getFeedStatus(FEED3)).andReturn(DFSubscrStatus.SUBSCR);
		expect(gMock2.getFeedStatus(FEED3)).andReturn(DFSubscrStatus.NOT_SUBSCR);
		expect(gMock3.getFeedStatus(FEED3)).andReturn(DFSubscrStatus.PENDING_SUBSCR);
		expect(gMock4.getFeedStatus(FEED3)).andReturn(DFSubscrStatus.SUBSCR);
		expect(gMock5.getFeedStatus(FEED3)).andReturn(DFSubscrStatus.SUBSCR);
		lockMock.unlock();
		control.replay();
		
		Collection<String> actual = service.getSubscribed(FEED3);
		
		control.verify();
		assertEquals(Arrays.asList("foo", "lol", "dup"), actual);
	}

	@Test
	public void testGetPendingUnsubscr() {
		groups.put("foo", gMock1);
		groups.put("bar", gMock2);
		groups.put("buz", gMock3);
		lockMock.lock();
		expect(gMock1.getFeedStatus(FEED1)).andReturn(DFSubscrStatus.PENDING_SUBSCR);
		expect(gMock2.getFeedStatus(FEED1)).andReturn(DFSubscrStatus.PENDING_UNSUBSCR);
		expect(gMock3.getFeedStatus(FEED1)).andReturn(DFSubscrStatus.PENDING_UNSUBSCR);
		lockMock.unlock();
		control.replay();
		
		Collection<String> actual = service.getPendingUnsubscr(FEED1);
		
		control.verify();
		assertEquals(Arrays.asList("bar", "buz"), actual);
	}
	
	@Test
	public void testSubscribed2_Key() {
		groups.put("foo", gMock1);
		lockMock.lock();
		gMock1.setFeedStatus(FEED3, DFSubscrStatus.SUBSCR);
		lockMock.unlock();
		control.replay();
		
		service.subscribed("foo", FEED3);
		
		control.verify();
	}

	@Test
	public void testSubscribed2_Key_AKeyPointsToNonExistentGroup() {
		lockMock.lock();
		expect(factoryMock.produce("boo")).andReturn(gMock1);
		gMock1.setFeedStatus(FEED2, DFSubscrStatus.SUBSCR);
		lockMock.unlock();
		control.replay();
		
		service.subscribed("boo", FEED2);
		
		control.verify();
		assertSame(gMock1, groups.get("boo"));
	}

	@Test
	public void testSubscribed2_Coll() {
		groups.put("one", gMock1);
		groups.put("bat", gMock2);
		groups.put("foo", gMock3);
		groups.put("bar", gMock4);
		groups.put("top", gMock5);
		lockMock.lock();
		gMock2.setFeedStatus(FEED3, DFSubscrStatus.SUBSCR);
		gMock4.setFeedStatus(FEED3, DFSubscrStatus.SUBSCR);
		gMock5.setFeedStatus(FEED3, DFSubscrStatus.SUBSCR);
		lockMock.unlock();
		control.replay();
		
		service.subscribed(Arrays.asList("bat", "bar", "top"), FEED3);
		
		control.verify();
	}
	
	@Test
	public void testSubscribed2_Coll_AKeyPointsToNonExistingGroup() {
		groups.put("one", gMock1);
		groups.put("top", gMock2);
		lockMock.lock();
		expect(factoryMock.produce("bat")).andReturn(gMock3);
		gMock3.setFeedStatus(FEED2, DFSubscrStatus.SUBSCR);
		expect(factoryMock.produce("zoo")).andReturn(gMock4);
		gMock4.setFeedStatus(FEED2, DFSubscrStatus.SUBSCR);
		gMock2.setFeedStatus(FEED2, DFSubscrStatus.SUBSCR);
		lockMock.unlock();
		control.replay();
		
		service.subscribed(Arrays.asList("bat", "zoo", "top"), FEED2);

		control.verify();
		assertSame(gMock3, groups.get("bat"));
		assertSame(gMock4, groups.get("zoo"));
	}
	
	@Test
	public void testUnsubscribed2_Coll() {
		groups.put("one", gMock1);
		groups.put("bat", gMock2);
		groups.put("foo", gMock3);
		groups.put("bar", gMock4);
		groups.put("top", gMock5);
		lockMock.lock();
		gMock1.setFeedStatus(FEED1, DFSubscrStatus.NOT_SUBSCR);
		gMock3.setFeedStatus(FEED1, DFSubscrStatus.NOT_SUBSCR);
		gMock5.setFeedStatus(FEED1, DFSubscrStatus.NOT_SUBSCR);
		lockMock.unlock();
		control.replay();
		
		service.unsubscribed(Arrays.asList("one", "foo", "top"), FEED1);
		
		control.verify();
	}
	
	@Test
	public void testUnsubscribed2_Coll_AKeyPointsToNonExistingGroup() {
		groups.put("one", gMock1);
		groups.put("bat", gMock2);
		groups.put("foo", gMock3);
		lockMock.lock();
		gMock2.setFeedStatus(FEED1, DFSubscrStatus.NOT_SUBSCR);
		expect(factoryMock.produce("zad")).andReturn(gMock4);
		gMock4.setFeedStatus(FEED1, DFSubscrStatus.NOT_SUBSCR);
		expect(factoryMock.produce("daf")).andReturn(gMock5);
		gMock5.setFeedStatus(FEED1, DFSubscrStatus.NOT_SUBSCR);
		gMock3.setFeedStatus(FEED1, DFSubscrStatus.NOT_SUBSCR);
		lockMock.unlock();
		control.replay();
		
		service.unsubscribed(Arrays.asList("bat", "zad", "daf", "foo"), FEED1);
		
		control.verify();
	}
	
	@Test
	public void testUnsubscribed2_Key() {
		groups.put("one", gMock1);
		groups.put("bat", gMock2);
		groups.put("foo", gMock3);
		lockMock.lock();
		gMock2.setFeedStatus(FEED2, DFSubscrStatus.NOT_SUBSCR);
		lockMock.unlock();
		control.replay();
		
		service.unsubscribed("bat", FEED2);
		
		control.verify();
	}

	@Test
	public void testUnsubscribed2_Key_AKeyPointsToNonExistentGroup() {
		groups.put("one", gMock1);
		groups.put("bat", gMock2);
		groups.put("foo", gMock3);
		lockMock.lock();
		expect(factoryMock.produce("gap")).andReturn(gMock5);
		gMock5.setFeedStatus(FEED2, DFSubscrStatus.NOT_SUBSCR);
		lockMock.unlock();
		control.replay();
		
		service.unsubscribed("gap", FEED2);
		
		control.verify();
		assertSame(gMock5, groups.get("gap"));
	}
	
	@Test
	public void testUnsubscribed0() {
		groups.put("one", gMock1);
		groups.put("bat", gMock2);
		groups.put("foo", gMock3);
		groups.put("bar", gMock4);
		groups.put("top", gMock5);
		lockMock.lock();
		gMock1.markAllNotSubscribed();
		gMock2.markAllNotSubscribed();
		gMock3.markAllNotSubscribed();
		gMock4.markAllNotSubscribed();
		gMock5.markAllNotSubscribed();
		lockMock.unlock();
		control.replay();
		
		service.unsubscribed();
		
		control.verify();
	}
	
	@Test
	public void testNotAvailable_ExistingGroup() {
		groups.put("one", gMock1);
		groups.put("bat", gMock2);
		groups.put("foo", gMock3);
		groups.put("bar", gMock4);
		groups.put("top", gMock5);
		lockMock.lock();
		gMock3.setNotFound(true);
		lockMock.unlock();
		control.replay();
		
		service.notAvailable("foo");
		
		control.verify();
	}

	@Test
	public void testNotAvailable_CreatedGroup() {
		groups.put("one", gMock1);
		groups.put("bat", gMock2);
		groups.put("bar", gMock4);
		groups.put("top", gMock5);
		lockMock.lock();
		expect(factoryMock.produce("foo")).andReturn(gMock3);
		gMock3.setNotFound(true);
		lockMock.unlock();
		control.replay();
		
		service.notAvailable("foo");
		
		control.verify();
		assertSame(gMock3, groups.get("foo"));
	}
	
	@Test
	public void testHaveToSubscribe_ExistingGroup() {
		groups.put("one", gMock1);
		groups.put("bat", gMock2);
		groups.put("foo", gMock3);
		lockMock.lock();
		expect(gMock2.markToSubscribe(FEED3)).andReturn(true);
		lockMock.unlock();
		lockMock.lock();
		expect(gMock2.markToSubscribe(FEED3)).andReturn(false);
		lockMock.unlock();
		control.replay();
		
		assertTrue(service.haveToSubscribe("bat", FEED3));
		assertFalse(service.haveToSubscribe("bat", FEED3));
		
		control.verify();
	}
	
	@Test
	public void testHaveToSubscribe_CreatedGroup() {
		groups.put("one", gMock1);
		groups.put("bat", gMock2);
		groups.put("foo", gMock3);
		lockMock.lock();
		expect(factoryMock.produce("boo")).andReturn(gMock4);
		expect(gMock4.markToSubscribe(FEED2)).andReturn(true);
		lockMock.unlock();
		control.replay();
		
		assertTrue(service.haveToSubscribe("boo", FEED2));
		
		control.verify();
		assertSame(gMock4, groups.get("boo"));
	}

	@Test
	public void testHaveToUnsubscribe_ExistingGroup() {
		groups.put("one", gMock1);
		lockMock.lock();
		expect(gMock1.markToUnsubscribe(FEED3)).andReturn(false);
		lockMock.unlock();
		control.replay();
		
		assertFalse(service.haveToUnsubscribe("one", FEED3));
		
		control.verify();
	}
	
	@Test
	public void testHaveToUnsubscribe_CreatedGroup() {
		lockMock.lock();
		expect(factoryMock.produce("zoo")).andReturn(gMock5);
		expect(gMock5.markToUnsubscribe(FEED1)).andReturn(true);
		lockMock.unlock();
		control.replay();
		
		assertTrue(service.haveToUnsubscribe("zoo", FEED1));
		
		control.verify();
		assertSame(gMock5, groups.get("zoo"));
	}
	
	@Test
	public void testIsNotAvailable_NotExists() {
		groups.put("one", gMock1);
		lockMock.lock();
		lockMock.unlock();
		control.replay();
		
		assertFalse(service.isNotAvailable("bat"));
		
		control.verify();
	}
	
	@Test
	public void testIsNotAvailable_Exists_Available() {
		groups.put("one", gMock1);
		groups.put("bat", gMock2);
		lockMock.lock();
		expect(gMock2.isNotFound()).andReturn(false);
		lockMock.unlock();
		control.replay();
		
		assertFalse(service.isNotAvailable("bat"));
		
		control.verify();
	}
	
	@Test
	public void testIsNotAvailable_Exists_NotAvailable() {
		groups.put("one", gMock1);
		groups.put("bat", gMock2);
		lockMock.lock();
		expect(gMock2.isNotFound()).andReturn(true);
		lockMock.unlock();
		control.replay();
		
		assertTrue(service.isNotAvailable("bat"));
		
		control.verify();
	}
	
	@Test
	public void testHaveToUnsubscribeAll() {
		groups.put("one", gMock1);
		groups.put("bat", gMock2);
		groups.put("foo", gMock3);
		groups.put("bar", gMock4);
		groups.put("top", gMock5);
		lockMock.lock();
		expect(gMock1.getFeedStatus(FEED2)).andReturn(DFSubscrStatus.NOT_AVAILABLE);
		expect(gMock2.getFeedStatus(FEED2)).andReturn(DFSubscrStatus.NOT_SUBSCR);
		expect(gMock3.getFeedStatus(FEED2)).andReturn(DFSubscrStatus.PENDING_UNSUBSCR);
		expect(gMock4.getFeedStatus(FEED2)).andReturn(DFSubscrStatus.PENDING_SUBSCR);
		gMock4.setFeedStatus(FEED2, DFSubscrStatus.NOT_SUBSCR);
		expect(gMock5.getFeedStatus(FEED2)).andReturn(DFSubscrStatus.SUBSCR);
		gMock5.setFeedStatus(FEED2, DFSubscrStatus.PENDING_UNSUBSCR);
		lockMock.unlock();
		control.replay();
		
		Collection<String> actual = service.haveToUnsubscribeAll(FEED2);
		
		control.verify();
		assertEquals(Arrays.asList("foo", "top"), actual);
	}

}
