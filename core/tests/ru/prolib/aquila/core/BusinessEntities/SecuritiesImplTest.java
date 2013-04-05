package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventSystemImpl;
import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.utils.SecurityFactory;
import ru.prolib.aquila.core.BusinessEntities.utils.SecurityFactoryImpl;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-06-09<br>
 * $Id: SecuritiesImplTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class SecuritiesImplTest {
	private static IMocksControl control;
	private static Terminal terminal;
	private SecurityFactory factory;
	private EventSystem eventSystem;
	private EventQueueImpl queue;
	private SecuritiesImpl secs;
	private EditableSecurity s1,s2,s3;
	private EventDispatcher dispatcher;
	private EventType onAvailable,onChanged,onTrade;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(Terminal.class);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		eventSystem = new EventSystemImpl();
		queue = (EventQueueImpl) eventSystem.getEventQueue();
		factory = new SecurityFactoryImpl(eventSystem, terminal);
		dispatcher = eventSystem.createEventDispatcher();
		onAvailable = eventSystem.createGenericType(dispatcher);
		onChanged = eventSystem.createGenericType(dispatcher);
		onTrade = eventSystem.createGenericType(dispatcher);
		secs = new SecuritiesImpl(factory, dispatcher, onAvailable,
				onChanged, onTrade, "RUR", SecurityType.STK);
		s1 = getSec("SBER", "EQBR", "RUR", SecurityType.STK);
		s2 = getSec("RIM2", "SPBFUT", "USD", SecurityType.FUT);
		s3 = getSec("SBER", "RTSS", "RUR", SecurityType.STK);
		queue.start();
	}
	
	@After
	public void tearDown() throws Exception {
		queue.stop();
	}
	
	/**
	 * Создать тестовый инструмент.
	 * <p>
	 * @param code код инструмента
	 * @param classCode код класса инструмента
	 * @param curr код валюты
	 * @param type тип инструмента
	 * @return экземпляр инструмента
	 */
	private EditableSecurity getSec(String code, String classCode,
			String curr, SecurityType type)
	{
		return secs.getEditableSecurity(new SecurityDescriptor(code,
				classCode, curr, type));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(secs.equals(secs));
		assertFalse(secs.equals(null));
		assertFalse(secs.equals(this));
	}
	
	@Test
	public void testGetSecuritiesCount() throws Exception {
		assertEquals(3, secs.getSecuritiesCount());
	}
	
	@Test
	public void testConstruct() throws Exception {
		Variant<SecurityFactory> vSecFact = new Variant<SecurityFactory>()
			.add(factory)
			.add(null);
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>(vSecFact)
			.add(dispatcher)
			.add(null);
		Variant<EventType> vAvail = new Variant<EventType>(vDisp)
			.add(null)
			.add(onAvailable);
		Variant<EventType> vChanged = new Variant<EventType>(vAvail)
			.add(null)
			.add(onChanged);
		Variant<EventType> vTrade = new Variant<EventType>(vChanged)
			.add(onTrade)
			.add(null);
		Variant<String> vCur = new Variant<String>(vTrade)
			.add(null)
			.add("RUR");
		Variant<SecurityType> vTyp = new Variant<SecurityType>(vCur)
			.add(null)
			.add(SecurityType.STK);
		Variant<?> iterator = vTyp;
		int exceptionCnt = 0;
		SecuritiesImpl found = null;
		do {
			try {
				found = new SecuritiesImpl(vSecFact.get(), vDisp.get(),
						vAvail.get(), vChanged.get(), vTrade.get(),
						vCur.get(), vTyp.get());
			} catch ( NullPointerException e ) {
				exceptionCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(exceptionCnt, iterator.count() - 1);
		assertSame(factory, found.getSecurityFactory());
		assertSame(dispatcher, found.getEventDispatcher());
		assertSame(onAvailable, found.OnSecurityAvailable());
		assertSame(onChanged, found.OnSecurityChanged());
		assertSame(onTrade, found.OnSecurityTrade());
		assertEquals("RUR", found.getDefaultCurrency());
		assertSame(SecurityType.STK, found.getDefaultType());
	}
	
	@Test
	public void testGetEditableSecurity_ReturnExisting() throws Exception {
		assertSame(s2, secs.getEditableSecurity(new SecurityDescriptor("RIM2",
				"SPBFUT", "USD", SecurityType.FUT)));
	}
	
	@Test
	public void testGetEditableSecurity_CreateNew() throws Exception {
		factory = control.createMock(SecurityFactory.class);
		secs = new SecuritiesImpl(factory, dispatcher, onAvailable,
				onChanged, onTrade, "USD", SecurityType.CASH);
		SecurityDescriptor descr =
				new SecurityDescriptor("A", "B", "USD", SecurityType.STK);
		expect(factory.createSecurity(eq(descr))).andReturn(s3);
		control.replay();
		
		EditableSecurity s = secs.getEditableSecurity(descr);
		assertSame(s3, s);
		assertSame(s, secs.getEditableSecurity(descr));
		
		control.verify();
		assertTrue(s3.OnChanged().isListener(secs));
		assertTrue(s3.OnTrade().isListener(secs));
	}
	
	@Test
	public void testGetSecurities() throws Exception {
		assertNotNull(s1);
		assertNotNull(s2);
		assertNotNull(s3);

		List<Security> list = secs.getSecurities();
		assertNotNull(list);
		assertEquals(3, list.size());
		assertSame(s1, list.get(0));
		assertSame(s2, list.get(1));
		assertSame(s3, list.get(2));
	}
	
	@Test
	public void testGetSecurity2_Ok() throws Exception {
		assertNotNull(s1);
		assertNotNull(s3);

		assertSame(s3, secs.getSecurity("SBER", "RTSS"));
		assertSame(s1, secs.getSecurity("SBER", "EQBR"));
	}
	
	@Test (expected=SecurityNotExistsException.class)
	public void testGetSecurity2_ThrowsIfNotExists() throws Exception {
		secs.getSecurity("GAZP", "EQBR");
	}
	
	@Test
	public void testGetSecurity1C_Ok() throws Exception {
		assertNotNull(s2);

		assertSame(s2, secs.getSecurity("RIM2"));
	}
	
	@Test (expected=SecurityNotExistsException.class)
	public void testGetSecurity1C_ThrowsIfNotExists() throws Exception {
		secs.getSecurity("ABC");
	}
	
	@Test (expected=SecurityAmbiguousException.class)
	public void testGetSecurity1C_ThrowsIfAmbiguous() throws Exception {
		secs.getSecurity("SBER");
	}
	
	@Test
	public void testGetSecurity1D_Ok() throws Exception {
		assertNotNull(s1);
		assertNotNull(s2);
		assertNotNull(s3);

		assertSame(s1, secs.getSecurity(new SecurityDescriptor("SBER",
				"EQBR", "RUR", SecurityType.STK)));
		assertSame(s2, secs.getSecurity(new SecurityDescriptor("RIM2",
				"SPBFUT", "USD", SecurityType.FUT)));
		assertSame(s3, secs.getSecurity(new SecurityDescriptor("SBER",
				"RTSS", "RUR", SecurityType.STK)));
	}
	
	@Test (expected=SecurityNotExistsException.class)
	public void testGetSecurity1D_ThrowsIfNotExists() throws Exception {
		secs.getSecurity(new SecurityDescriptor("ABC", "DEF", "EUR",
				SecurityType.STK));
	}
	
	@Test
	public void testIsSecurityExists2() throws Exception {
		assertTrue(secs.isSecurityExists("SBER", "EQBR"));
		assertTrue(secs.isSecurityExists("SBER", "RTSS"));
		assertFalse(secs.isSecurityExists("FOO", "BAR"));
		assertFalse(secs.isSecurityExists("SBER", "BAR"));
		assertFalse(secs.isSecurityExists("FOO", "EQBR"));
	}
	
	@Test
	public void testIsSecurityExists1C_Ok() throws Exception {
		assertTrue(secs.isSecurityExists("SBER"));
		assertTrue(secs.isSecurityExists("RIM2"));
		assertFalse(secs.isSecurityExists("FOO"));
	}
	
	@Test
	public void testIsExists1D_Ok() throws Exception {
		SecurityDescriptor
			sd1 = new SecurityDescriptor("SBER","EQBR","RUR", SecurityType.STK),
			sd2 = new SecurityDescriptor("SBER","RTSS","RUR", SecurityType.STK),
			sd3 = new SecurityDescriptor("FOO", "BAR", "ZEN", SecurityType.FUT);
		assertTrue(secs.isSecurityExists(sd1));
		assertTrue(secs.isSecurityExists(sd2));
		assertFalse(secs.isSecurityExists(sd3));
	}
	
	@Test
	public void testIsSecurityAmbiguous() throws Exception {
		assertTrue(secs.isSecurityAmbiguous("SBER"));
		assertFalse(secs.isSecurityAmbiguous("RIM2"));
		assertFalse(secs.isSecurityAmbiguous("FOO"));
	}
	
	@Test
	public void testFireSecurityAvailableEvent() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		final SecurityEvent expected =
			new SecurityEvent(secs.OnSecurityAvailable(), s1);
		secs.OnSecurityAvailable().addListener(new EventListener() {
			@Override
			public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
		});
		secs.fireSecurityAvailableEvent(s1);
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testOnEvent_SecurityChanged() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		final SecurityEvent expected= new SecurityEvent(onChanged, s1);
		secs.OnSecurityChanged().addListener(new EventListener(){
			@Override
			public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
		});
		secs.onEvent(new SecurityEvent(s1.OnChanged(), s1));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void testOnEvent_SecurityTrade() throws Exception {
		final CountDownLatch finished = new CountDownLatch(1);
		final SecurityEvent expected= new SecurityEvent(onTrade, s1);
		secs.OnSecurityTrade().addListener(new EventListener(){
			@Override
			public void onEvent(Event event) {
				assertEquals(expected, event);
				finished.countDown();
			}
		});
		secs.onEvent(new SecurityEvent(s1.OnTrade(), s1));
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
	}

}
