package ru.prolib.aquila.ib.subsys.api;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.Contract;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.utils.*;
import ru.prolib.aquila.ib.subsys.IBServiceLocator;

/**
 * 2012-11-14<br>
 * $Id: IBRequestFactoryImplTest.java 499 2013-02-07 10:43:25Z whirlwind $
 */
public class IBRequestFactoryImplTest {
	private static IMocksControl control;
	private static EventSystem eSys;
	private static EventDispatcher eDisp;
	private static IBServiceLocator locator;
	private static Counter reqId;
	private static IBRequestFactoryImpl factory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		locator = control.createMock(IBServiceLocator.class);
		eSys = control.createMock(EventSystem.class);
		eDisp = control.createMock(EventDispatcher.class);
		reqId = control.createMock(Counter.class);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		factory = new IBRequestFactoryImpl(eSys, locator, reqId);
	}
	
	@Test
	public void testConstruct2() throws Exception {
		factory = new IBRequestFactoryImpl(eSys, locator);
		assertSame(eSys, factory.getEventSystem());
		assertSame(locator, factory.getServiceLocator());
		assertEquals(new SimpleCounter(), factory.getRequestNumerator());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		assertSame(eSys, factory.getEventSystem());
		assertSame(locator, factory.getServiceLocator());
		assertSame(reqId, factory.getRequestNumerator());
	}
	
	@Test
	public void testRequestContract() throws Exception {
		expect(eSys.createEventDispatcher()).andReturn(eDisp);
		EventType onError = control.createMock(EventType.class);
		expect(eSys.createGenericType(same(eDisp))).andReturn(onError);
		EventType onResponse = control.createMock(EventType.class);
		expect(eSys.createGenericType(same(eDisp))).andReturn(onResponse);
		expect(reqId.incrementAndGet()).andReturn(123);
		Contract contract = new Contract();
		IBRequestContract expected = null, actual = null;
		control.replay();
		actual = factory.requestContract(contract);
		control.verify();
		expected = new IBRequestContractImpl(locator, eDisp,
				onError, onResponse, 123, contract);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testRequestMarketData() throws Exception {
		expect(eSys.createEventDispatcher()).andReturn(eDisp);
		EventType onError = control.createMock(EventType.class);
		EventType onTick = control.createMock(EventType.class);
		expect(eSys.createGenericType(same(eDisp))).andReturn(onError);
		expect(eSys.createGenericType(same(eDisp))).andReturn(onTick);
		expect(reqId.incrementAndGet()).andReturn(456);
		Contract contract = new Contract();
		IBRequestMarketData expected = null, actual = null;
		control.replay();
		actual = factory.requestMarketData(contract);
		control.verify();
		expected = new IBRequestMarketDataImpl(locator, eDisp,
				onError, onTick, 456, contract);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testRequestAccountUpdates1() throws Exception {
		expect(eSys.createEventDispatcher()).andReturn(eDisp);
		EventType onUpdAcc = control.createMock(EventType.class);
		expect(eSys.createGenericType(eDisp)).andReturn(onUpdAcc);
		EventType onUpdPort = control.createMock(EventType.class);
		expect(eSys.createGenericType(eDisp)).andReturn(onUpdPort);
		IBRequestAccountUpdates expected = null, actual = null;
		control.replay();
		actual = factory.requestAccountUpdates("TEST");
		control.verify();
		expected = new IBRequestAccountUpdatesImpl(locator, eDisp,
				onUpdAcc, onUpdPort, "TEST");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(factory.equals(factory));
		assertFalse(factory.equals(null));
		assertFalse(factory.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventSystem> vEs = new Variant<EventSystem>()
			.add(eSys)
			.add(control.createMock(EventSystem.class));
		Variant<IBServiceLocator> vLoc = new Variant<IBServiceLocator>(vEs)
			.add(locator)
			.add(control.createMock(IBServiceLocator.class));
		Variant<Counter> vId = new Variant<Counter>(vLoc)
			.add(reqId)
			.add(control.createMock(Counter.class));
		Variant<?> iterator = vId;
		int foundCnt = 0;
		IBRequestFactoryImpl x = null, found = null;
		do {
			x = new IBRequestFactoryImpl(vEs.get(), vLoc.get(), vId.get());
			if ( factory.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(eSys, found.getEventSystem());
		assertSame(locator, found.getServiceLocator());
		assertSame(reqId, found.getRequestNumerator());
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121207, 231519)
			.append(eSys)
			.append(locator)
			.append(reqId)
			.toHashCode();
		assertEquals(hashCode, factory.hashCode());
	}

}
