package ru.prolib.aquila.ib.subsys.contract;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.easymock.*;
import org.junit.*;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.utils.TerminalDecorator;
import ru.prolib.aquila.core.utils.*;
import ru.prolib.aquila.ib.event.IBEventContract;
import ru.prolib.aquila.ib.subsys.*;
import ru.prolib.aquila.ib.subsys.api.*;

/**
 * 2013-01-05<br>
 * $Id: IBContractsStorageImplTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class IBContractsStorageImplTest {
	private IMocksControl control;
	private EventDispatcher dispatcher;
	private IBContractsStorageImpl contracts1;
	private IBServiceLocator locator;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		locator = new IBServiceLocatorImpl(new TerminalDecorator());
		dispatcher = locator.getEventSystem().createEventDispatcher();
		
		contracts1 = new IBContractsStorageImpl(locator, dispatcher);

		locator.getEventSystem().getEventQueue().start();
	}
	
	@After
	public void tearDown() throws Exception {
		locator.getEventSystem().getEventQueue().stop();
		locator.getEventSystem().getEventQueue().join();
	}
	
	/**
	 * Создать событие о деталях контракта.
	 * <p>
	 * Использует вызов {@link #createEvent(int, int, Contract, int)} для
	 * создания контракта в комбинации указанных параметров и субтипа
	 * {@link IBEventContract#SUBTYPE_NORM} и нулевым идентификатором запроса.
	 * <p>
	 * @param id идентификатор контракта (всегда переписывает данные контракта)
	 * @param cont детали контракта или null, если не имеет значения
	 * @return
	 */
	private IBEventContract createEvent(int id, ContractDetails cont) {
		return createEvent(id, IBEventContract.SUBTYPE_NORM, cont, 0);
	}
	
	/**
	 * Создать событие о деталях контракта.
	 * <p>
	 * @param id идентификатор контракта (всегда переписывает данные контракта)
	 * @param subType субтип события
	 * @param cont контракт или null, если не имеет значения
	 * @param requestId идентификатор запроса
	 * @return событие с указанными параметрами
	 */
	private IBEventContract createEvent(int id, int subType,
			ContractDetails cont, int requestId)
	{
		ContractDetails details = (cont == null ? new ContractDetails() : cont);
		if ( details.m_summary == null ) {
			details.m_summary = new Contract();
		}
		details.m_summary.m_conId = id;
		return new IBEventContract(locator.getApiClient().OnContractDetails(),
				requestId, subType, details);
	}
	
	/**
	 * Проверить обработку события с информацией о контракте.
	 * <p>
	 * Ожидается, что объект должен обработать событие с указанными
	 * параметрами.
	 * <p>
	 * @param id идентификатор контракта (всегда переписывает в контракте).
	 * @param subType субтип события, константа из {@link IBEventContract}.
	 * @param cont контракт или null, если экземпляр не имеет значения.  
	 */
	private void checkOnContractDetails(int id, int subType, Contract cont) {
		control = createStrictControl();
		dispatcher = control.createMock(EventDispatcher.class);
		contracts1 = new IBContractsStorageImpl(locator, dispatcher);
		ContractDetails details = new ContractDetails();
		details.m_summary = (cont == null ? new Contract() : cont);
		IBEventContract in = createEvent(id, subType, details, 0);
		Event ex = new IBEventContract(contracts1.OnContractLoadedOnce(id), in);
		dispatcher.dispatchForCurrentList(eq(ex));
		dispatcher.removeListeners(contracts1.OnContractLoadedOnce(id));
		control.replay();
		
		contracts1.onEvent(in);
		
		control.verify();
	}
	
	@Test
	public void testStart() throws Exception {
		contracts1.start();
		assertTrue(locator.getApiClient().OnContractDetails().isListener(contracts1));
	}
	
	@Test
	public void testOnEvent_OnContractDetailsOkForRegular() throws Exception {
		checkOnContractDetails(123, IBEventContract.SUBTYPE_NORM, null);
		checkOnContractDetails(123, IBEventContract.SUBTYPE_NORM, null);
	}
	
	@Test
	public void testOnEvent_OnContractDetailsOkForBond() throws Exception {
		checkOnContractDetails(123, IBEventContract.SUBTYPE_BOND, null);
		checkOnContractDetails(123, IBEventContract.SUBTYPE_BOND, null);
	}
	
	@Test
	public void testOnEvent_OnContractDetailsSkipDiffType() throws Exception {
		EventType type = control.createMock(EventType.class);
		Event event = createEvent(12345, IBEventContract.SUBTYPE_NORM, null, 0);
		control.replay();
		
		contracts1.onEvent(new IBEventContract(type, (IBEventContract) event));
		
		control.verify();
	}
	
	@Test
	public void testOnEvent_OnContractDetailsSkipEnd() throws Exception {
		Event event = createEvent(12345, IBEventContract.SUBTYPE_END, null, 0);
		control.replay();
		
		contracts1.onEvent(event);
		
		control.verify();
	}
	
	@Test (expected=IBContractUnavailableException.class)
	public void testGetContract_ThrowsIfNotLoaded() throws Exception {
		control.replay();
		
		contracts1.getContract(123);
		
		control.verify();
	}

	@Test
	public void testGetContract_Ok() throws Exception {
		ContractDetails contract = new ContractDetails();
		Event event = createEvent(567, contract);
		contracts1.onEvent(event);

		assertSame(contract, contracts1.getContract(567));
		assertSame(contract, contracts1.getContract(567));
	}
	
	@Test
	public void testIsContractAvailable() throws Exception {
		ContractDetails contract = new ContractDetails();
		Event event = createEvent(678, contract);
		contracts1.onEvent(event);

		assertFalse(contracts1.isContractAvailable(567));
		assertTrue(contracts1.isContractAvailable(678));
	}
	
	@Test
	public void testLoadContract_FirstTime() throws Exception {
		IBRequestFactory factory = control.createMock(IBRequestFactory.class);
		locator.setRequestFactory(factory);
		Contract contract = new Contract();
		contract.m_conId = 890;
		IBRequestContract req1 = control.createMock(IBRequestContract.class);
		expect(factory.requestContract(contract)).andReturn(req1);
		req1.start();
		expectLastCall().times(3);
		control.replay();
		
		contracts1.loadContract(890); // first time
		contracts1.loadContract(890); // next times
		contracts1.loadContract(890); // ...
		
		control.verify();
	}
	
	@Test
	public void testOnContractLoadedOnce() throws Exception {
		EventType type1 = contracts1.OnContractLoadedOnce(123),
				  type2 = contracts1.OnContractLoadedOnce(234);
		assertSame(type1, contracts1.OnContractLoadedOnce(123));
		assertSame(type2, contracts1.OnContractLoadedOnce(234));
		assertNotSame(type1, type2);
	}
	
	@Test
	public void testComplexEventListening() throws Exception {
		control.replay();
		final CountDownLatch finished = new CountDownLatch(1);
		IBEventContract fix[] = {
				createEvent(5, null),
				createEvent(6, null),
				createEvent(5, null),
				createEvent(6, null),
				createEvent(7, null),
		};
		List<Event> exp = new Vector<Event>();
		exp.add(new IBEventContract(contracts1.OnContractLoadedOnce(5),fix[0]));
		exp.add(new IBEventContract(contracts1.OnContractLoadedOnce(6),fix[1]));
		exp.add(new IBEventContract(contracts1.OnContractLoadedOnce(7),fix[4]));
		final List<Event> actual = new Vector<Event>();
		contracts1.OnContractLoadedOnce(5).addListener(new EventListener() {
			@Override public void onEvent(Event event) {
				actual.add(event);
			}
		});
		contracts1.OnContractLoadedOnce(6).addListener(new EventListener() {
			@Override public void onEvent(Event event) {
				actual.add(event);
			}
		});
		contracts1.OnContractLoadedOnce(7).addListener(new EventListener() {
			@Override public void onEvent(Event event) {
				actual.add(event);
				finished.countDown();
			}
		});
		for ( int i = 0; i < fix.length; i ++ ) {
			contracts1.onEvent(fix[i]);
		}
		assertTrue(finished.await(100, TimeUnit.MILLISECONDS));
		assertEquals(exp, actual);
		control.verify();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(locator, contracts1.getServiceLocator());
		assertSame(dispatcher, contracts1.getEventDispatcher());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(contracts1.equals(contracts1));
		assertFalse(contracts1.equals(this));
		assertFalse(contracts1.equals(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<IBServiceLocator> vLoc = new Variant<IBServiceLocator>()
			.add(locator)
			.add(control.createMock(IBServiceLocator.class));
		Variant<EventDispatcher> vDisp = new Variant<EventDispatcher>(vLoc)
			.add(dispatcher)
			.add(control.createMock(EventDispatcher.class));
		Variant<?> iterator = vDisp;
		int foundCnt = 0;
		IBContractsStorageImpl x = null, found = null;
		do {
			x = new IBContractsStorageImpl(vLoc.get(), vDisp.get());
			if ( contracts1.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(locator, found.getServiceLocator());
		assertSame(dispatcher, found.getEventDispatcher());
	}
	
}
