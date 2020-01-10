package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueFactory;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounter.Field;

public class SymbolSubscrRepositoryTest {
	private static EventQueue queue;
	private static Symbol symbol1, symbol2, symbol3;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		queue = new EventQueueFactory().createDefault();
		symbol1 = new Symbol("S:foo@EX1:USD");
		symbol2 = new Symbol("B:bar@EX2:RUR");
		symbol3 = new Symbol("F:buz@EX3:EUR");
	}

	@Rule
	public ExpectedException eex = ExpectedException.none();
	private SymbolSubscrRepository service;
	
	@Before
	public void setUp() throws Exception {
		service = new SymbolSubscrRepository(new SymbolSubscrCounterFactory(queue), "TEST");
	}
	
	@Test
	public void testCtor2_Q() {
		service = new SymbolSubscrRepository(queue, "BEST");
		
		SymbolSubscrCounterFactory factory = (SymbolSubscrCounterFactory) service.getFactory();
		assertNotNull(factory);
		assertSame(queue, factory.getEventQueue());
		assertEquals("BEST", service.getRepoID());
	}
	
	@Test
	public void testGetOrCreate_Throws_Exists() {
		service.subscribe(symbol1, MDLevel.L0);
		service.subscribe(symbol2, MDLevel.L2);
		service.subscribe(symbol3, MDLevel.L1);
		eex.expect(UnsupportedOperationException.class);

		service.getOrCreate(symbol1);
	}

	@Test
	public void testGetOrCreate_Throws_NotExists() {
		eex.expect(UnsupportedOperationException.class);

		service.getOrCreate(symbol1);
	}
	
	@Test
	public void testRemove_Throws_Exists() {
		service.subscribe(symbol1, MDLevel.L0);
		service.subscribe(symbol2, MDLevel.L2);
		service.subscribe(symbol3, MDLevel.L1);
		eex.expect(UnsupportedOperationException.class);

		service.remove(symbol1);
	}

	@Test
	public void testRemove_Throws_NotExists() {
		eex.expect(UnsupportedOperationException.class);

		service.remove(symbol1);
	}
	
	@Test
	public void testSubscribe_L0_Create() {
		SymbolSubscrCounter counter = service.subscribe(symbol1, MDLevel.L0);
		
		assertNotNull(counter);
		assertEquals(symbol1, counter.getSymbol());
		assertEquals(1, counter.getNumL0());
		assertEquals(0, counter.getNumL1_BBO());
		assertEquals(0, counter.getNumL1());
		assertEquals(0, counter.getNumL2());
		
		assertTrue(service.contains(symbol1));
		assertSame(counter, service.getOrThrow(symbol1));
	}
	
	@Test
	public void testSubscribe_L0_Update() {
		SymbolSubscrCounter orig = service.subscribe(symbol1, MDLevel.L0);
		orig.consume(new DeltaUpdateBuilder()
				.withToken(Field.NUM_L0, 5)
				.withToken(Field.NUM_L1_BBO, 3)
				.withToken(Field.NUM_L1, 2)
				.withToken(Field.NUM_L2, 1)
				.buildUpdate()
			);
		
		SymbolSubscrCounter counter = service.subscribe(symbol1, MDLevel.L0);

		assertSame(orig, counter);
		assertEquals(symbol1, counter.getSymbol());
		assertEquals(6, counter.getNumL0());
		assertEquals(3, counter.getNumL1_BBO());
		assertEquals(2, counter.getNumL1());
		assertEquals(1, counter.getNumL2());
	}

	@Test
	public void testSubscribe_L1_BBO_Create() {
		SymbolSubscrCounter counter = service.subscribe(symbol2, MDLevel.L1_BBO);
		
		assertNotNull(counter);
		assertEquals(symbol2, counter.getSymbol());
		assertEquals(1, counter.getNumL0());
		assertEquals(1, counter.getNumL1_BBO());
		assertEquals(0, counter.getNumL1());
		assertEquals(0, counter.getNumL2());
		
		assertTrue(service.contains(symbol2));
		assertSame(counter, service.getOrThrow(symbol2));
	}
	
	@Test
	public void testSubscribe_L1_BBO_Update() {
		SymbolSubscrCounter orig = service.subscribe(symbol3, MDLevel.L1_BBO);
		orig.consume(new DeltaUpdateBuilder()
				.withToken(Field.NUM_L0, 5)
				.withToken(Field.NUM_L1_BBO, 3)
				.withToken(Field.NUM_L1, 2)
				.withToken(Field.NUM_L2, 1)
				.buildUpdate()
			);
		
		SymbolSubscrCounter counter = service.subscribe(symbol3, MDLevel.L1_BBO);

		assertSame(orig, counter);
		assertEquals(symbol3, counter.getSymbol());
		assertEquals(6, counter.getNumL0());
		assertEquals(4, counter.getNumL1_BBO());
		assertEquals(2, counter.getNumL1());
		assertEquals(1, counter.getNumL2());
	}
	
	@Test
	public void testSubscribe_L1_Create() {
		SymbolSubscrCounter counter = service.subscribe(symbol2, MDLevel.L1);
		
		assertNotNull(counter);
		assertEquals(symbol2, counter.getSymbol());
		assertEquals(1, counter.getNumL0());
		assertEquals(1, counter.getNumL1_BBO());
		assertEquals(1, counter.getNumL1());
		assertEquals(0, counter.getNumL2());
		
		assertTrue(service.contains(symbol2));
		assertSame(counter, service.getOrThrow(symbol2));
	}
	
	@Test
	public void testSubscribe_L1_Update() {
		SymbolSubscrCounter orig = service.subscribe(symbol3, MDLevel.L1);
		orig.consume(new DeltaUpdateBuilder()
				.withToken(Field.NUM_L0, 5)
				.withToken(Field.NUM_L1_BBO, 3)
				.withToken(Field.NUM_L1, 2)
				.withToken(Field.NUM_L2, 1)
				.buildUpdate()
			);
		
		SymbolSubscrCounter counter = service.subscribe(symbol3, MDLevel.L1);

		assertSame(orig, counter);
		assertEquals(symbol3, counter.getSymbol());
		assertEquals(6, counter.getNumL0());
		assertEquals(4, counter.getNumL1_BBO());
		assertEquals(3, counter.getNumL1());
		assertEquals(1, counter.getNumL2());
	}
	
	@Test
	public void testSubscribe_L2_Create() {
		SymbolSubscrCounter counter = service.subscribe(symbol2, MDLevel.L2);
		
		assertNotNull(counter);
		assertEquals(symbol2, counter.getSymbol());
		assertEquals(1, counter.getNumL0());
		assertEquals(1, counter.getNumL1_BBO());
		assertEquals(1, counter.getNumL1());
		assertEquals(1, counter.getNumL2());
		
		assertTrue(service.contains(symbol2));
		assertSame(counter, service.getOrThrow(symbol2));
	}
	
	@Test
	public void testSubscribe_L2_Update() {
		SymbolSubscrCounter orig = service.subscribe(symbol3, MDLevel.L2);
		orig.consume(new DeltaUpdateBuilder()
				.withToken(Field.NUM_L0, 5)
				.withToken(Field.NUM_L1_BBO, 3)
				.withToken(Field.NUM_L1, 2)
				.withToken(Field.NUM_L2, 1)
				.buildUpdate()
			);
		
		SymbolSubscrCounter counter = service.subscribe(symbol3, MDLevel.L2);

		assertSame(orig, counter);
		assertEquals(symbol3, counter.getSymbol());
		assertEquals(6, counter.getNumL0());
		assertEquals(4, counter.getNumL1_BBO());
		assertEquals(3, counter.getNumL1());
		assertEquals(2, counter.getNumL2());
	}
	
	@Test
	public void testUnsubscribe_L0_Create() {
		SymbolSubscrCounter counter = service.unsubscribe(symbol1, MDLevel.L0);
		
		assertNotNull(counter);
		assertEquals(symbol1, counter.getSymbol());
		assertEquals(-1, counter.getNumL0());
		assertEquals( 0, counter.getNumL1_BBO());
		assertEquals( 0, counter.getNumL1());
		assertEquals( 0, counter.getNumL2());
		
		assertTrue(service.contains(symbol1));
		assertSame(counter, service.getOrThrow(symbol1));
	}

	@Test
	public void testUnsubscribe_L0_Update() {
		SymbolSubscrCounter orig = service.subscribe(symbol3, MDLevel.L0);
		orig.consume(new DeltaUpdateBuilder()
				.withToken(Field.NUM_L0, 5)
				.withToken(Field.NUM_L1_BBO, 3)
				.withToken(Field.NUM_L1, 2)
				.withToken(Field.NUM_L2, 1)
				.buildUpdate()
			);
		
		SymbolSubscrCounter counter = service.unsubscribe(symbol3, MDLevel.L0);
		
		assertSame(orig, counter);
		assertEquals(symbol3, counter.getSymbol());
		assertEquals(4, counter.getNumL0());
		assertEquals(3, counter.getNumL1_BBO());
		assertEquals(2, counter.getNumL1());
		assertEquals(1, counter.getNumL2());
	}

	@Test
	public void testUnsubscribe_L1_BBO_Create() {
		SymbolSubscrCounter counter = service.unsubscribe(symbol1, MDLevel.L1_BBO);
		
		assertNotNull(counter);
		assertEquals(symbol1, counter.getSymbol());
		assertEquals(-1, counter.getNumL0());
		assertEquals(-1, counter.getNumL1_BBO());
		assertEquals( 0, counter.getNumL1());
		assertEquals( 0, counter.getNumL2());
		
		assertTrue(service.contains(symbol1));
		assertSame(counter, service.getOrThrow(symbol1));
	}

	@Test
	public void testUnsubscribe_L1_BBO_Update() {
		SymbolSubscrCounter orig = service.subscribe(symbol3, MDLevel.L1_BBO);
		orig.consume(new DeltaUpdateBuilder()
				.withToken(Field.NUM_L0, 5)
				.withToken(Field.NUM_L1_BBO, 3)
				.withToken(Field.NUM_L1, 2)
				.withToken(Field.NUM_L2, 1)
				.buildUpdate()
			);
		
		SymbolSubscrCounter counter = service.unsubscribe(symbol3, MDLevel.L1_BBO);
		
		assertSame(orig, counter);
		assertEquals(symbol3, counter.getSymbol());
		assertEquals(4, counter.getNumL0());
		assertEquals(2, counter.getNumL1_BBO());
		assertEquals(2, counter.getNumL1());
		assertEquals(1, counter.getNumL2());
	}

	@Test
	public void testUnsubscribe_L1_Create() {
		SymbolSubscrCounter counter = service.unsubscribe(symbol1, MDLevel.L1);
		
		assertNotNull(counter);
		assertEquals(symbol1, counter.getSymbol());
		assertEquals(-1, counter.getNumL0());
		assertEquals(-1, counter.getNumL1_BBO());
		assertEquals(-1, counter.getNumL1());
		assertEquals( 0, counter.getNumL2());
		
		assertTrue(service.contains(symbol1));
		assertSame(counter, service.getOrThrow(symbol1));
	}

	@Test
	public void testUnsubscribe_L1_Update() {
		SymbolSubscrCounter orig = service.subscribe(symbol3, MDLevel.L1);
		orig.consume(new DeltaUpdateBuilder()
				.withToken(Field.NUM_L0, -2) // no rule about negatives
				.withToken(Field.NUM_L1_BBO, 3)
				.withToken(Field.NUM_L1, 2)
				.withToken(Field.NUM_L2, 1)
				.buildUpdate()
			);
		
		SymbolSubscrCounter counter = service.unsubscribe(symbol3, MDLevel.L1);
		
		assertSame(orig, counter);
		assertEquals(symbol3, counter.getSymbol());
		assertEquals(-3, counter.getNumL0());
		assertEquals( 2, counter.getNumL1_BBO());
		assertEquals( 1, counter.getNumL1());
		assertEquals( 1, counter.getNumL2());
	}

	@Test
	public void testUnsubscribe_L2_Create() {
		SymbolSubscrCounter counter = service.unsubscribe(symbol1, MDLevel.L2);
		
		assertNotNull(counter);
		assertEquals(symbol1, counter.getSymbol());
		assertEquals(-1, counter.getNumL0());
		assertEquals(-1, counter.getNumL1_BBO());
		assertEquals(-1, counter.getNumL1());
		assertEquals(-1, counter.getNumL2());
		
		assertTrue(service.contains(symbol1));
		assertSame(counter, service.getOrThrow(symbol1));
	}

	@Test
	public void testUnsubscribe_L2_Update() {
		SymbolSubscrCounter orig = service.subscribe(symbol3, MDLevel.L2);
		orig.consume(new DeltaUpdateBuilder()
				.withToken(Field.NUM_L0, -2) // no rule about negatives
				.withToken(Field.NUM_L1_BBO, -3)
				.withToken(Field.NUM_L1, -2)
				.withToken(Field.NUM_L2, -1)
				.buildUpdate()
			);
		
		SymbolSubscrCounter counter = service.unsubscribe(symbol3, MDLevel.L2);
		
		assertSame(orig, counter);
		assertEquals(symbol3, counter.getSymbol());
		assertEquals(-3, counter.getNumL0());
		assertEquals(-4, counter.getNumL1_BBO());
		assertEquals(-3, counter.getNumL1());
		assertEquals(-2, counter.getNumL2());
	}

}
