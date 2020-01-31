package ru.prolib.aquila.data;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

import org.easymock.IMocksControl;

import static org.easymock.EasyMock.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.EventQueueFactory;
import ru.prolib.aquila.core.BusinessEntities.BasicTerminalBuilder;
import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.EditableTerminal;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounter;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrCounter.Field;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.SymbolSubscrRepository;
import ru.prolib.aquila.core.data.DataProviderStub;
import ru.prolib.aquila.core.utils.Variant;

public class SymbolDataServiceDFBTest {
	private static EventQueue queue;
	private static Symbol symbol1, symbol2, symbol3, symbol4, symbol5;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		queue = new EventQueueFactory().createDefault();
		symbol1 = new Symbol("SBER");
		symbol2 = new Symbol("AAPL");
		symbol3 = new Symbol("MSFT");
		symbol4 = new Symbol("GAZP");
		symbol5 = new Symbol("LKOH");
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		queue.shutdown();
	}
	
	@Rule public ExpectedException eex = ExpectedException.none();
	private IMocksControl control;
	private SymbolSubscrRepository ssr, ssrMock;
	private DFGroupRepo<Symbol, MDLevel> dfssMock;
	private DataSource dsMock;
	private SymbolSubscrCounter counterMock1, counterMock2, counterMock3;
	private EditableTerminal terminal;
	private SymbolDataServiceDFB service;
	private SymbolDataServiceDFB.Insider service_ins, insiderMock;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dfssMock = control.createMock(DFGroupRepo.class);
		ssrMock = control.createMock(SymbolSubscrRepository.class);
		dsMock = control.createMock(DataSource.class);
		insiderMock = control.createMock(SymbolDataServiceDFB.Insider.class);
		counterMock1 = control.createMock(SymbolSubscrCounter.class);
		counterMock2 = control.createMock(SymbolSubscrCounter.class);
		counterMock3 = control.createMock(SymbolSubscrCounter.class);
		ssr = new SymbolSubscrRepository(queue, "TEST");
		terminal = new BasicTerminalBuilder()
				.withEventQueue(queue)
				.withTerminalID("DFB-Test")
				.withDataProvider(new DataProviderStub())
				.buildTerminal();
		service = new SymbolDataServiceDFB(insiderMock, ssrMock, dfssMock);
		service.setDataSource(dsMock);
		service.setTerminal(terminal);
		service_ins = new SymbolDataServiceDFB.Insider();
	}
	
	void makeConnected() {
		control.resetToStrict();
		expect(ssrMock.getEntities()).andReturn(Arrays.asList());
		control.replay();
		service.onConnectionStatusChange(true);
		control.resetToStrict();
	}
	
	void testInsider_SyncSubscrState_(boolean positive_counters) {
		SymbolSubscrCounter counter = ssr.subscribe(symbol1, MDLevel.L2);
		int count = positive_counters ? 1 : 0;
		counter.consume(new DeltaUpdateBuilder()
				.withToken(Field.NUM_L0, count)
				.withToken(Field.NUM_L1_BBO, count)
				.withToken(Field.NUM_L2, count)
				.buildUpdate());
		Variant<Boolean> vL0r = new Variant<>(true, false),
				vL1BBOr = new Variant<>(vL0r, true, false),
				vL2r = new Variant<>(vL1BBOr, true, false);
		Variant<?> iterator = vL2r;
		int neg_res_found_cnt = 0;
		Boolean neg_res_l0 = null, neg_res_l1bbo = null, neg_res_l2 = null;
		do {
			control.resetToStrict();
			if ( count > 0 ) {
				expect(dfssMock.haveToSubscribe(symbol1, MDLevel.L0)).andReturn(vL0r.get());
				expect(dfssMock.haveToSubscribe(symbol1, MDLevel.L1)).andReturn(vL1BBOr.get());
				expect(dfssMock.haveToSubscribe(symbol1, MDLevel.L2)).andReturn(vL2r.get());
			} else {
				expect(dfssMock.haveToUnsubscribe(symbol1, MDLevel.L0)).andReturn(vL0r.get());
				expect(dfssMock.haveToUnsubscribe(symbol1, MDLevel.L1)).andReturn(vL1BBOr.get());
				expect(dfssMock.haveToUnsubscribe(symbol1, MDLevel.L2)).andReturn(vL2r.get());				
			}
			control.replay();
			
			if ( ! service_ins.syncSubscrState(symbol1, counter, dfssMock) ) {
				neg_res_found_cnt ++;
				neg_res_l0 = vL0r.get();
				neg_res_l1bbo = vL1BBOr.get();
				neg_res_l2 = vL2r.get();
			}
			
			control.verify();
		} while ( iterator.next() );
		// The only case when result is negative - when nothing changed and all calls returned false
		assertEquals(1, neg_res_found_cnt);
		assertFalse(neg_res_l0);
		assertFalse(neg_res_l1bbo);
		assertFalse(neg_res_l2);
	}
	
	@Test
	public void testInsider_SyncSubscrState_ResultIndicatingOfChanges_AllCountersArePositive() {
		testInsider_SyncSubscrState_(true);
	}

	@Test
	public void testInsider_SyncSubscrState_ResultIndicatingOfChanges_AllCountersAreNegative() {
		testInsider_SyncSubscrState_(false);
	}

	@Test
	public void testInsider_SyncSubscrState_MethodSelectionDependsOnCounter() {
		SymbolSubscrCounter counter = ssr.subscribe(symbol1, MDLevel.L2);
		Variant<Integer> vL0c = new Variant<Integer>().add(0).add(1).add(8).add(25);
		Variant<Integer> vL1BBOc = new Variant<Integer>(vL0c).add(0).add(1).add(2).add(26);
		Variant<Integer> vL2c = new Variant<Integer>(vL1BBOc).add(0).add(1).add(5).add(12);
		Variant<?> iterator = vL2c;
		int l0, l1bbo, l2;
		boolean dontmatter = true;
		do {
			control.resetToStrict();
			counter.consume(new DeltaUpdateBuilder()
					.withToken(Field.NUM_L0, l0 = vL0c.get())
					.withToken(Field.NUM_L1_BBO, l1bbo = vL1BBOc.get())
					.withToken(Field.NUM_L1, ThreadLocalRandom.current().nextInt(-5, 6)) // should be ignored
					.withToken(Field.NUM_L2, l2 = vL2c.get())
					.buildUpdate());
			if ( l0 > 0 ) {
				expect(dfssMock.haveToSubscribe(symbol2, MDLevel.L0)).andReturn(dontmatter);
			} else {
				expect(dfssMock.haveToUnsubscribe(symbol2, MDLevel.L0)).andReturn(dontmatter);
			}
			if ( l1bbo > 0 ) {
				expect(dfssMock.haveToSubscribe(symbol2,  MDLevel.L1)).andReturn(dontmatter);
			} else {
				expect(dfssMock.haveToUnsubscribe(symbol2, MDLevel.L1)).andReturn(dontmatter);
			}
			if ( l2 > 0 ) {
				expect(dfssMock.haveToSubscribe(symbol2, MDLevel.L2)).andReturn(dontmatter);
			} else {
				expect(dfssMock.haveToUnsubscribe(symbol2, MDLevel.L2)).andReturn(dontmatter);
			}
			control.replay();
			
			service_ins.syncSubscrState(symbol2, counter, dfssMock);
			
			control.verify();
		} while ( iterator.next() );
	}
	
	@Test
	public void testInsider_ApplyPendingChanges() {
		EditableSecurity s1 = terminal.getEditableSecurity(symbol1),
				s2 = terminal.getEditableSecurity(symbol2),
				s3 = terminal.getEditableSecurity(symbol3),
				s4 = terminal.getEditableSecurity(symbol4),
				s5 = terminal.getEditableSecurity(symbol5);
		// phase 1.1
		expect(dfssMock.getPendingSubscr(MDLevel.L0)).andReturn(Arrays.asList(symbol1, symbol2));
		dsMock.subscribeSymbol(symbol1, s1);
		dfssMock.subscribed(symbol1, MDLevel.L0);
		dsMock.subscribeSymbol(symbol2, s2);
		dfssMock.subscribed(symbol2, MDLevel.L0);
		// phase 1.2
		expect(dfssMock.getPendingSubscr(MDLevel.L1)).andReturn(Arrays.asList(symbol3, symbol4, symbol5));
		dsMock.subscribeL1(symbol3, s3);
		dfssMock.subscribed(symbol3, MDLevel.L1);
		dsMock.subscribeL1(symbol4, s4);
		dfssMock.subscribed(symbol4, MDLevel.L1);
		dsMock.subscribeL1(symbol5, s5);
		dfssMock.subscribed(symbol5, MDLevel.L1);
		// phase 1.3
		expect(dfssMock.getPendingSubscr(MDLevel.L2)).andReturn(Arrays.asList(symbol1, symbol5));
		dsMock.subscribeMD(symbol1, s1);
		dfssMock.subscribed(symbol1, MDLevel.L2);
		dsMock.subscribeMD(symbol5, s5);
		dfssMock.subscribed(symbol5, MDLevel.L2);
		// phase 2.1
		expect(dfssMock.getPendingUnsubscr(MDLevel.L0)).andReturn(Arrays.asList(symbol3, symbol4, symbol5));
		dsMock.unsubscribeSymbol(symbol3, s3);
		dfssMock.unsubscribed(symbol3, MDLevel.L0);
		dsMock.unsubscribeSymbol(symbol4, s4);
		dfssMock.unsubscribed(symbol4, MDLevel.L0);
		dsMock.unsubscribeSymbol(symbol5, s5);
		dfssMock.unsubscribed(symbol5, MDLevel.L0);
		// phase 2.2
		expect(dfssMock.getPendingUnsubscr(MDLevel.L1)).andReturn(Arrays.asList(symbol5, symbol2));
		dsMock.unsubscribeL1(symbol5, s5);
		dfssMock.unsubscribed(symbol5, MDLevel.L1);
		dsMock.unsubscribeL1(symbol2, s2);
		dfssMock.unsubscribed(symbol2, MDLevel.L1);
		// phase 2.3
		expect(dfssMock.getPendingUnsubscr(MDLevel.L2)).andReturn(Arrays.asList(symbol3, symbol4, symbol1));
		dsMock.unsubscribeMD(symbol3, s3);
		dfssMock.unsubscribed(symbol3, MDLevel.L2);
		dsMock.unsubscribeMD(symbol4, s4);
		dfssMock.unsubscribed(symbol4, MDLevel.L2);
		dsMock.unsubscribeMD(symbol1, s1);
		dfssMock.unsubscribed(symbol1, MDLevel.L2);
		control.replay();
		
		service_ins.applyPendingChanges(dsMock, terminal, dfssMock);
		
		control.verify();
	}
	
	@Test
	public void testOnSubscribe_NotConnected() {
		expect(ssrMock.subscribe(symbol5, MDLevel.L1)).andReturn(counterMock1);
		control.replay();
		
		SubscrHandler actual = service.onSubscribe(symbol5, MDLevel.L1);
		
		control.verify();
		SubscrHandler expected = new SymbolSubscrHandler(service, symbol5, MDLevel.L1, actual.getConfirmation());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testOnSubscribe_NotAvailable() {
		makeConnected();
		expect(ssrMock.subscribe(symbol3, MDLevel.L2)).andReturn(counterMock1);
		expect(dfssMock.isNotAvailable(symbol3)).andReturn(true);
		control.replay();
		
		SubscrHandler actual = service.onSubscribe(symbol3,  MDLevel.L2);
		
		control.verify();
		SubscrHandler expected = new SymbolSubscrHandler(service, symbol3, MDLevel.L2, actual.getConfirmation());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testOnSubscribe_NotChanged() {
		makeConnected();
		expect(ssrMock.subscribe(symbol2, MDLevel.L1_BBO)).andReturn(counterMock1);
		expect(dfssMock.isNotAvailable(symbol2)).andReturn(false);
		expect(insiderMock.syncSubscrState(symbol2, counterMock1, dfssMock)).andReturn(false);
		control.replay();
		
		SubscrHandler actual = service.onSubscribe(symbol2, MDLevel.L1_BBO);
		
		control.verify();
		SubscrHandler expected = new SymbolSubscrHandler(service, symbol2, MDLevel.L1_BBO, actual.getConfirmation());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testOnSubscribe_Applied() {
		makeConnected();
		expect(ssrMock.subscribe(symbol4, MDLevel.L0)).andReturn(counterMock1);
		expect(dfssMock.isNotAvailable(symbol4)).andReturn(false);
		expect(insiderMock.syncSubscrState(symbol4, counterMock1, dfssMock)).andReturn(true);
		insiderMock.applyPendingChanges(dsMock, terminal, dfssMock);
		control.replay();

		SubscrHandler actual = service.onSubscribe(symbol4, MDLevel.L0);
		
		control.verify();
		SubscrHandler expected = new SymbolSubscrHandler(service, symbol4, MDLevel.L0, actual.getConfirmation());
		assertEquals(expected, actual);
	}
	
	@Test
	public void testOnSubscribe_ThrowsIfDataSourceNotDefined() {
		service.setDataSource(null);
		control.replay();
		eex.expect(NullPointerException.class);
		eex.expectMessage("Data source not defined");
		
		service.onSubscribe(symbol4, MDLevel.L0);
	}
	
	@Test
	public void testOnSubscribe_ThrowsIfTerminalNotDefined() {
		service.setTerminal(null);
		control.replay();
		eex.expect(NullPointerException.class);
		eex.expectMessage("Terminal not defined");
		
		service.onSubscribe(symbol4, MDLevel.L0);
	}
	
	@Test
	public void testOnUnsubscribe_NotConnected() {
		expect(ssrMock.unsubscribe(symbol3, MDLevel.L1)).andReturn(counterMock1);
		control.replay();
		
		service.onUnsubscribe(symbol3, MDLevel.L1);
		
		control.verify();
	}
	
	@Test
	public void testOnUnsubscribe_NotAvailable() {
		makeConnected();
		expect(ssrMock.unsubscribe(symbol1, MDLevel.L1_BBO)).andReturn(counterMock1);
		expect(dfssMock.isNotAvailable(symbol1)).andReturn(true);
		control.replay();
		
		service.onUnsubscribe(symbol1, MDLevel.L1_BBO);
		
		control.verify();
	}

	@Test
	public void testOnUnsubscribe_NotChanged() {
		makeConnected();
		expect(ssrMock.unsubscribe(symbol4, MDLevel.L0)).andReturn(counterMock1);
		expect(dfssMock.isNotAvailable(symbol4)).andReturn(false);
		expect(insiderMock.syncSubscrState(symbol4, counterMock1, dfssMock)).andReturn(false);
		control.replay();
		
		service.onUnsubscribe(symbol4, MDLevel.L0);
		
		control.verify();
	}
	
	@Test
	public void testOnUnsubscribe_Applied() {
		makeConnected();
		expect(ssrMock.unsubscribe(symbol2, MDLevel.L2)).andReturn(counterMock1);
		expect(dfssMock.isNotAvailable(symbol2)).andReturn(false);
		expect(insiderMock.syncSubscrState(symbol2, counterMock1, dfssMock)).andReturn(true);
		insiderMock.applyPendingChanges(dsMock, terminal, dfssMock);
		control.replay();
		
		service.onUnsubscribe(symbol2, MDLevel.L2);
		
		control.verify();
	}
	
	@Test
	public void testOnUnsubscribe_ThrowsIfDataSourceNotDefined() {
		service.setDataSource(null);
		control.replay();
		eex.expect(NullPointerException.class);
		eex.expectMessage("Data source not defined");
		
		service.onUnsubscribe(symbol4, MDLevel.L0);
	}

	@Test
	public void testOnUnsubscribe_ThrowsIfTerminalNotDefined() {
		service.setTerminal(null);
		control.replay();
		eex.expect(NullPointerException.class);
		eex.expectMessage("Terminal not defined");
		
		service.onUnsubscribe(symbol4, MDLevel.L0);
	}

	@Test
	public void testOnConnectionStatusChange_Connected_NoEntities() {
		expect(ssrMock.getEntities()).andReturn(Arrays.asList());
		control.replay();
		
		service.onConnectionStatusChange(true);
		
		control.verify();
	}
	
	@Test
	public void testOnConnectionStatusChange_Connected_NoChanges() {
		expect(counterMock1.getSymbol()).andStubReturn(symbol1);
		expect(counterMock2.getSymbol()).andStubReturn(symbol2);
		expect(counterMock3.getSymbol()).andStubReturn(symbol3);
		expect(ssrMock.getEntities()).andReturn(Arrays.asList(counterMock1, counterMock2, counterMock3));
		expect(insiderMock.syncSubscrState(symbol1, counterMock1, dfssMock)).andReturn(false);
		expect(insiderMock.syncSubscrState(symbol2, counterMock2, dfssMock)).andReturn(false);
		expect(insiderMock.syncSubscrState(symbol3, counterMock3, dfssMock)).andReturn(false);
		control.replay();
		
		service.onConnectionStatusChange(true);
		
		control.verify();
		assertTrue(service.connected());
	}
	
	@Test
	public void testOnConnectionStatusChange_Connected_Applied() {
		expect(counterMock1.getSymbol()).andStubReturn(symbol1);
		expect(counterMock2.getSymbol()).andStubReturn(symbol2);
		expect(counterMock3.getSymbol()).andStubReturn(symbol3);
		expect(ssrMock.getEntities()).andReturn(Arrays.asList(counterMock1, counterMock2, counterMock3));
		expect(insiderMock.syncSubscrState(symbol1, counterMock1, dfssMock)).andReturn(false);
		expect(insiderMock.syncSubscrState(symbol2, counterMock2, dfssMock)).andReturn(true);
		expect(insiderMock.syncSubscrState(symbol3, counterMock3, dfssMock)).andReturn(false);
		insiderMock.applyPendingChanges(dsMock, terminal, dfssMock);
		control.replay();
		
		service.onConnectionStatusChange(true);
		
		control.verify();
		assertTrue(service.connected());
	}

	@Test
	public void testOnConnectionStatusChange_Disconnected_() {
		Variant<Collection<Symbol>>
			vL0 = new Variant<>(Arrays.asList(), Arrays.asList(symbol1, symbol2, symbol5)),
			vL1BBO = new Variant<>(vL0, Arrays.asList(), Arrays.asList(symbol3, symbol4, symbol1)),
			vL2 = new Variant<>(vL1BBO, Arrays.asList(), Arrays.asList(symbol5, symbol2, symbol3));
		Variant<?> iterator = vL2;
		int dont_apply_combs_found = 0;
		Collection<Symbol> _l0, _l1bbo, _l2;
		do {
			makeConnected();
			expect(dfssMock.haveToUnsubscribeAll(MDLevel.L0)).andReturn(_l0 = vL0.get());
			expect(dfssMock.haveToUnsubscribeAll(MDLevel.L1)).andReturn(_l1bbo = vL1BBO.get());
			expect(dfssMock.haveToUnsubscribeAll(MDLevel.L2)).andReturn(_l2 = vL2.get());
			if ( _l0.size() + _l1bbo.size() + _l2.size() > 1 ) {
				insiderMock.applyPendingChanges(dsMock, terminal, dfssMock);
			} else {
				dont_apply_combs_found ++;
			}
			control.replay();
			
			service.onConnectionStatusChange(false);
			
			control.verify();
			assertFalse(service.connected());
		} while ( iterator.next() );
		assertEquals(1, dont_apply_combs_found);
	}

}
