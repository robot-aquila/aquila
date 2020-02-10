package ru.prolib.aquila.qforts.impl;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.BusinessEntities.EditableOrder;
import ru.prolib.aquila.core.BusinessEntities.EditablePortfolio;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.DataProviderStub;

public class QFObjectRegistryImplTest {
	
	static void interruptAll(Thread ...threads) {
		for ( Thread thread : threads ) {
			if ( thread.isAlive() ) {
				thread.interrupt();
			}
		}
	}
	
	private LinkedHashSet<EditablePortfolio> portfolios;
	private LinkedHashSet<EditableSecurity> securities;
	private LinkedHashMap<Symbol, LinkedHashSet<EditableOrder>> orders;
	private QFObjectRegistry registry;
	private static Account account1, account2, account3;
	private static Symbol symbol1, symbol2, symbol3;
	private EditableTerminal terminal;

	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		account1 = new Account("TEST1");
		account2 = new Account("TEST2");
		account3 = new Account("TEST3");
		symbol1 = new Symbol("BEST");
		symbol2 = new Symbol("QUEST");
		symbol3 = new Symbol("GUEST");
	}

	@Before
	public void setUp() throws Exception {
		portfolios = new LinkedHashSet<>();
		securities = new LinkedHashSet<>();
		orders = new LinkedHashMap<>();
		registry = new QFObjectRegistryImpl(portfolios, securities, orders);
		terminal = new BasicTerminalBuilder()
			.withDataProvider(new DataProviderStub())
			.buildTerminal();
		terminal.getEditableSecurity(symbol1);
		terminal.getEditableSecurity(symbol2);
		terminal.getEditableSecurity(symbol3);
		terminal.getEditablePortfolio(account1);
		terminal.getEditablePortfolio(account2);
		terminal.getEditablePortfolio(account3);
	}
	
	@Test
	public void testIsRegistered_Portfolio() {
		EditablePortfolio p1 = terminal.getEditablePortfolio(account1),
				p2 = terminal.getEditablePortfolio(account2),
				p3 = terminal.getEditablePortfolio(account3);
		
		assertFalse(registry.isRegistered(p1));
		assertFalse(registry.isRegistered(p2));
		assertFalse(registry.isRegistered(p3));
		
		portfolios.add(p1);
		portfolios.add(p3);

		assertTrue(registry.isRegistered(p1));
		assertFalse(registry.isRegistered(p2));
		assertTrue(registry.isRegistered(p3));
	}
	
	@Test
	public void testIsRegistered_Security() {
		EditableSecurity s1 = terminal.getEditableSecurity(symbol1),
				s2 = terminal.getEditableSecurity(symbol2),
				s3 = terminal.getEditableSecurity(symbol3);
		
		assertFalse(registry.isRegistered(s1));
		assertFalse(registry.isRegistered(s2));
		assertFalse(registry.isRegistered(s3));
		
		securities.add(s1);
		securities.add(s3);
		
		assertTrue(registry.isRegistered(s1));
		assertFalse(registry.isRegistered(s2));
		assertTrue(registry.isRegistered(s3));
	}
	
	@Test
	public void testIsRegistered_Order() {
		EditableOrder o1 = (EditableOrder) terminal.createOrder(account1,
				symbol1,
				OrderAction.BUY,
				CDecimalBD.of(1L),
				CDecimalBD.of("115.04")),
			o2 = (EditableOrder) terminal.createOrder(account2,
				symbol2,
				OrderAction.BUY,
				CDecimalBD.of(1L),
				CDecimalBD.of("86.19")),
			o3 = (EditableOrder) terminal.createOrder(account3,
				symbol3,
				OrderAction.SELL,
				CDecimalBD.of(1L),
				CDecimalBD.of("70.56"));
		
		assertFalse(registry.isRegistered(o1));
		assertFalse(registry.isRegistered(o2));
		assertFalse(registry.isRegistered(o3));
		
		registry.register(o1);
		registry.register(o3);
		
		assertTrue(registry.isRegistered(o1));
		assertFalse(registry.isRegistered(o2));
		assertTrue(registry.isRegistered(o3));
	}
	
	@Test
	public void testRegister_Portfolio() {
		EditablePortfolio p1 = terminal.getEditablePortfolio(account1),
				p2 = terminal.getEditablePortfolio(account2),
				p3 = terminal.getEditablePortfolio(account3);
		
		registry.register(p1);
		registry.register(p2);
		registry.register(p3);
		
		LinkedHashSet<EditablePortfolio> expected = new LinkedHashSet<>();
		expected.add(p1);
		expected.add(p2);
		expected.add(p3);
		assertEquals(expected, portfolios);
	}
	
	@Test
	public void testRegister_Security() {
		EditableSecurity s1 = terminal.getEditableSecurity(symbol1),
				s3 = terminal.getEditableSecurity(symbol3);
		
		registry.register(s1);
		registry.register(s3);
		
		LinkedHashSet<EditableSecurity> expected = new LinkedHashSet<>();
		expected.add(s1);
		expected.add(s3);
		assertEquals(expected, securities);
	}
	
	@Test
	public void testRegister_Order() {
		EditableOrder s1o1 = (EditableOrder) terminal.createOrder(account1,
				symbol1,
				OrderAction.BUY,
				CDecimalBD.of(1L),
				CDecimalBD.of("115.04")),
			s1o2 = (EditableOrder) terminal.createOrder(account2,
				symbol1,
				OrderAction.BUY,
				CDecimalBD.of(100L),
				CDecimalBD.of("86.19")),
			s1o3 = (EditableOrder) terminal.createOrder(account3,
				symbol1,
				OrderAction.SELL,
				CDecimalBD.of(10L),
				CDecimalBD.of("70.56")),
			s2o1 = (EditableOrder) terminal.createOrder(account1,
				symbol2,
				OrderAction.SELL,
				CDecimalBD.of(10L),
				CDecimalBD.of("23.96")),
			s2o2 = (EditableOrder) terminal.createOrder(account3,
				symbol2,
				OrderAction.BUY,
				CDecimalBD.of(5L),
				CDecimalBD.of("11.05"));
		
		assertTrue(registry.register(s1o1));
		assertTrue(registry.register(s1o2));
		assertTrue(registry.register(s1o3));
		assertTrue(registry.register(s2o1));
		assertTrue(registry.register(s2o2));
		
		LinkedHashMap<Symbol, LinkedHashSet<EditableOrder>> expected = new LinkedHashMap<>();
		expected.put(symbol1, new LinkedHashSet<>());
		expected.get(symbol1).add(s1o1);
		expected.get(symbol1).add(s1o2);
		expected.get(symbol1).add(s1o3);
		expected.put(symbol2, new LinkedHashSet<>());
		expected.get(symbol2).add(s2o1);
		expected.get(symbol2).add(s2o2);
		assertEquals(expected, orders);
	}
	
	@Test
	public void testRegister_Order_SkipDuplicates() {
		EditableOrder
			s1o1 = (EditableOrder) terminal.createOrder(account1,
				symbol1,
				OrderAction.BUY,
				CDecimalBD.of(1L),
				CDecimalBD.of("115.04")),
			s1o2 = (EditableOrder) terminal.createOrder(account2,
				symbol1,
				OrderAction.BUY,
				CDecimalBD.of(100L),
				CDecimalBD.of("86.19"));
		
		assertTrue(registry.register(s1o1));
		assertTrue(registry.register(s1o2));
		assertFalse(registry.register(s1o1));
		assertFalse(registry.register(s1o2));
		
		LinkedHashMap<Symbol, LinkedHashSet<EditableOrder>> expected = new LinkedHashMap<>();
		expected.put(symbol1, new LinkedHashSet<>());
		expected.get(symbol1).add(s1o1);
		expected.get(symbol1).add(s1o2);
		assertEquals(expected, orders);
	}
	
	@Test
	public void testGetSecurityList() {
		EditableSecurity s1 = terminal.getEditableSecurity(symbol1),
				s2 = terminal.getEditableSecurity(symbol2),
				s3 = terminal.getEditableSecurity(symbol3);
		securities.add(s2);
		securities.add(s1);
		securities.add(s3);
		
		List<EditableSecurity> actual = registry.getSecurityList();
		
		List<EditableSecurity> expected = new ArrayList<>();
		expected.add(s2);
		expected.add(s1);
		expected.add(s3);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetPortfolioList() {
		EditablePortfolio p1 = terminal.getEditablePortfolio(account1),
				p2 = terminal.getEditablePortfolio(account2),
				p3 = terminal.getEditablePortfolio(account3);
		portfolios.add(p3);
		portfolios.add(p1);
		portfolios.add(p2);
		
		List<EditablePortfolio> actual = registry.getPortfolioList();
		
		List<EditablePortfolio> expected = new ArrayList<>();
		expected.add(p3);
		expected.add(p1);
		expected.add(p2);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetOrderList() {
		EditableOrder s1o1 = (EditableOrder) terminal.createOrder(account1,
				symbol1,
				OrderAction.BUY,
				CDecimalBD.of(1L),
				CDecimalBD.of("115.04")),
			s1o2 = (EditableOrder) terminal.createOrder(account2,
				symbol1,
				OrderAction.BUY,
				CDecimalBD.of(100L),
				CDecimalBD.of("86.19")),
			s1o3 = (EditableOrder) terminal.createOrder(account3,
				symbol1,
				OrderAction.SELL,
				CDecimalBD.of(10L),
				CDecimalBD.of("70.56")),
			s2o1 = (EditableOrder) terminal.createOrder(account1,
				symbol2,
				OrderAction.SELL,
				CDecimalBD.of(10L),
				CDecimalBD.of("23.96")),	// +
			s2o2 = (EditableOrder) terminal.createOrder(account3,
				symbol2,
				OrderAction.BUY,
				CDecimalBD.of(5L),
				CDecimalBD.of("11.05")), 	// -
			s2o3 = (EditableOrder) terminal.createOrder(account2,
				symbol2,
				OrderAction.BUY,
				CDecimalBD.of(1L),
				CDecimalBD.of("55.00")), 	// +
			s2o4 = (EditableOrder) terminal.createOrder(account1,
				symbol2,
				OrderAction.BUY,
				CDecimalBD.of(5L),
				CDecimalBD.of("55.29")), 	// +
			s2o5 = (EditableOrder) terminal.createOrder(account2,
				symbol2,
				OrderAction.SELL,
				CDecimalBD.of(5L),
				CDecimalBD.of("55.01")); 	// -
		registry.register(s1o1);
		registry.register(s1o2);
		registry.register(s1o3);
		registry.register(s2o1);
		registry.register(s2o2);
		registry.register(s2o3);
		registry.register(s2o4);
		registry.register(s2o5);
			
		List<EditableOrder> actual = registry.getOrderList(symbol2, CDecimalBD.of("55.00"));
		
		List<EditableOrder> expected = new ArrayList<>();
		expected.add(s2o1);
		expected.add(s2o3);
		expected.add(s2o4);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testPurgeOrder() {
		EditableOrder s1o1 = (EditableOrder) terminal.createOrder(account1,
				symbol1,
				OrderAction.BUY,
				CDecimalBD.of(1L),
				CDecimalBD.of("115.04")),
			s1o2 = (EditableOrder) terminal.createOrder(account2,
				symbol1,
				OrderAction.BUY,
				CDecimalBD.of(100L),
				CDecimalBD.of("86.19")),
			s1o3 = (EditableOrder) terminal.createOrder(account3,
				symbol1,
				OrderAction.SELL,
				CDecimalBD.of(10L),
				CDecimalBD.of("70.56")),
			s2o1 = (EditableOrder) terminal.createOrder(account1,
				symbol2,
				OrderAction.SELL,
				CDecimalBD.of(10L),
				CDecimalBD.of("23.96")),
			s2o2 = (EditableOrder) terminal.createOrder(account3,
				symbol2,
				OrderAction.BUY,
				CDecimalBD.of(5L),
				CDecimalBD.of("11.05"));
		registry.register(s1o1);
		registry.register(s1o2);
		registry.register(s1o3);
		registry.register(s2o1);
		registry.register(s2o2);

		assertTrue(registry.purgeOrder(s1o2));
		assertTrue(registry.purgeOrder(s2o1));
		assertFalse(registry.purgeOrder(s1o2));
		assertFalse(registry.purgeOrder(s2o1));
		
		LinkedHashMap<Symbol, LinkedHashSet<EditableOrder>> expected = new LinkedHashMap<>();
		expected.put(symbol1, new LinkedHashSet<>());
		expected.get(symbol1).add(s1o1);
		expected.get(symbol1).add(s1o3);
		expected.put(symbol2, new LinkedHashSet<>());
		expected.get(symbol2).add(s2o2);
		assertEquals(expected, orders);
	}
	
	static abstract class AccessorOT extends Thread {
		protected final CountDownLatch goal = new CountDownLatch(1), lockerSignal, finished;
		
		public AccessorOT(CountDownLatch lockerSignal, CountDownLatch finished) {
			this.lockerSignal = lockerSignal;
			this.finished = finished;
		}
		
		public AccessorOT(CountDownLatch finished) {
			this(new CountDownLatch(1), finished);
		}
		
		@Override
		public void run() {
			try {
				lockerSignal.await(500L, TimeUnit.MILLISECONDS);
				if ( goal() ) {
					goal.countDown();
				}
			} catch ( InterruptedException e ) {
				e.printStackTrace();
			}
			finished.countDown();
		}
		
		protected abstract boolean goal();
		
		public void waitForFinish() throws InterruptedException, TimeoutException {
			if ( ! finished.await(1L, TimeUnit.SECONDS) ) {
				throw new TimeoutException();
			}
		}
		
		public boolean isGoalHit() {
			return goal.getCount() == 0;
		}
		
		public CountDownLatch getGoal() {
			return goal;
		}
		
		public CountDownLatch getFinished() {
			return finished;
		}
		
		public CountDownLatch getLocker() {
			return lockerSignal;
		}

	}
	
	static abstract class LockerOT extends Thread {
		protected final AccessorOT accessor;
		protected final CountDownLatch goal = new CountDownLatch(1);
		
		public LockerOT(AccessorOT accessor) {
			this.accessor = accessor;
		}
		
		abstract protected void lock();
		abstract protected void unlock();
		
		@Override
		public void run() {
			lock();
			try {
				accessor.getLocker().countDown();
				if ( goal() ) {
					goal.countDown();
				}
			} finally {
				unlock();
			}
			accessor.getFinished().countDown();
		}
		
		public boolean isGoalHit() {
			return goal.getCount() == 0;
		}
		
		protected abstract boolean goal();

	}
	
	static abstract class OrderLockerOT extends LockerOT {
		protected final EditableOrder order;

		public OrderLockerOT(AccessorOT accessor, EditableOrder order) {
			super(accessor);
			this.order = order;
		}

		@Override
		protected void lock() {
			order.lock();
		}

		@Override
		protected void unlock() {
			order.unlock();
		}
		
	}
	
	static class OrderLockerOT_LockAndWait extends OrderLockerOT {
		protected final QFObjectRegistry registry;

		public OrderLockerOT_LockAndWait(AccessorOT accessor,
				EditableOrder order,
				QFObjectRegistry registry)
		{
			super(accessor, order);
			this.registry = registry;
		}

		@Override
		protected boolean goal() {
			// actually, synchronization on this does not work because a registry uses internal monitor
			synchronized ( registry ) {
				try {
					return accessor.getGoal().await(100L, TimeUnit.MILLISECONDS);
				} catch ( InterruptedException e ) {
					e.printStackTrace();
					return false;
				}
			}
		}
		
	}
	
	static class OrderLockerOT_WeakWaitAndEnter extends OrderLockerOT {
		protected final QFObjectRegistry registry;

		public OrderLockerOT_WeakWaitAndEnter(AccessorOT accessor,
				EditableOrder order,
				QFObjectRegistry registry)
		{
			super(accessor, order);
			this.registry = registry;
		}

		@Override
		protected boolean goal() {
			try {
				Thread.sleep(100L);
				registry.getSecurityList();
				return true;
			} catch ( InterruptedException e ) {
				e.printStackTrace();
				return false;
			}
		}
		
	}
	
	@Test
	public void testIsRegistered_Order_DeadlockTest() throws Exception {
		EditableOrder order = (EditableOrder) terminal.createOrder(account1,
				symbol1,
				OrderAction.BUY,
				CDecimalBD.of(1L),
				CDecimalBD.of("115.04"));
		registry.register(order);
		CountDownLatch finished = new CountDownLatch(2);
		AccessorOT concurrent_thread = new AccessorOT(finished) {
			@Override
			protected boolean goal() {
				return registry.isRegistered(order);
			}
		};
		OrderLockerOT locker_thread = new OrderLockerOT_LockAndWait(concurrent_thread, order, registry);
		concurrent_thread.start();
		locker_thread.start();
		
		if ( finished.await(1L, TimeUnit.SECONDS) ) {
			assertTrue(locker_thread.isGoalHit());
			assertTrue(concurrent_thread.isGoalHit());
		} else {
			interruptAll(concurrent_thread, locker_thread);
			fail("Deadlock detected");
		}
	}
	
	@Test
	public void testRegister_Order_DeadlockTest() throws Exception {
		EditableOrder order = (EditableOrder) terminal.createOrder(account1,
				symbol1,
				OrderAction.BUY,
				CDecimalBD.of(1L),
				CDecimalBD.of("115.04"));
		CountDownLatch finished = new CountDownLatch(2);
		AccessorOT concurrent_thread = new AccessorOT(finished) {
			@Override
			public boolean goal() {
				registry.register(order);
				return true;
			}
		};
		// This only method which need an access to order properties.
		// So the order locking is must.
		OrderLockerOT locker_thread = new OrderLockerOT_WeakWaitAndEnter(concurrent_thread, order, registry);
		concurrent_thread.start();
		locker_thread.start();
		
		if ( finished.await(1L, TimeUnit.SECONDS) ) {
			assertTrue(locker_thread.isGoalHit());
			assertTrue(concurrent_thread.isGoalHit());
		} else {
			interruptAll(concurrent_thread, locker_thread);
			fail("Deadlock detected");
		}
	}
	
	@Test
	public void testPurgeOrder_DeadlockTest() throws Exception {
		EditableOrder order = (EditableOrder) terminal.createOrder(account1,
				symbol1,
				OrderAction.BUY,
				CDecimalBD.of(1L),
				CDecimalBD.of("115.04"));
		registry.register(order);
		CountDownLatch finished = new CountDownLatch(2);
		AccessorOT concurrent_thread = new AccessorOT(finished) {
			@Override
			protected boolean goal() {
				registry.purgeOrder(order);
				return true;
			}
		};
		OrderLockerOT locker_thread = new OrderLockerOT_LockAndWait(concurrent_thread, order, registry);
		concurrent_thread.start();
		locker_thread.start();
		
		if ( finished.await(1L, TimeUnit.SECONDS) ) {
			assertTrue(locker_thread.isGoalHit());
			assertTrue(concurrent_thread.isGoalHit());
		} else {
			interruptAll(concurrent_thread, locker_thread);
			fail("Deadlock detected");
		}
	}
	
	@Test
	public void testGetOrderList_DeadlockTest() throws Exception {
		EditableOrder
			order1 = (EditableOrder) terminal.createOrder(account1,
				symbol1,
				OrderAction.BUY,
				CDecimalBD.of(1L),
				CDecimalBD.of("115.04")),
			order2 = (EditableOrder) terminal.createOrder(account2,
				symbol1,
				OrderAction.BUY,
				CDecimalBD.of(100L),
				CDecimalBD.of("86.19")),
			order3 = (EditableOrder) terminal.createOrder(account3,
				symbol1,
				OrderAction.SELL,
				CDecimalBD.of(10L),
				CDecimalBD.of("70.56"));
		registry.register(order1);
		registry.register(order2);
		registry.register(order3);
		CountDownLatch finished = new CountDownLatch(4);
		CountDownLatch locker_signal = new CountDownLatch(3);
		AccessorOT concurrent_thread = new AccessorOT(locker_signal, finished) {
			@Override
			protected boolean goal() {
				List<EditableOrder> actual = registry.getOrderList(symbol1, of("100.00"));
				List<EditableOrder> expected = new ArrayList<>();
				expected.add(order1);
				expected.add(order3);
				return expected.equals(actual);
			}
		};
		OrderLockerOT locker_thread1 = new OrderLockerOT_LockAndWait(concurrent_thread, order1, registry);
		OrderLockerOT locker_thread2 = new OrderLockerOT_LockAndWait(concurrent_thread, order2, registry);
		OrderLockerOT locker_thread3 = new OrderLockerOT_LockAndWait(concurrent_thread, order3, registry);
		concurrent_thread.start();
		locker_thread1.start();
		locker_thread2.start();
		locker_thread3.start();
		
		if ( finished.await(1L, TimeUnit.SECONDS) ) {
			//assertTrue(locker_thread1.isGoalHit());
			//assertTrue(locker_thread2.isGoalHit());
			//assertTrue(locker_thread3.isGoalHit());
			assertTrue(concurrent_thread.isGoalHit());
		} else {
			interruptAll(concurrent_thread, locker_thread1, locker_thread2, locker_thread3);
			fail("Deadlock detected");
		}
	}

}
