package ru.prolib.aquila.ib.assembler.cache;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Vector;

import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.Contract;
import com.ib.client.ContractDetails;
import com.ib.client.Execution;
import com.ib.client.Order;
import com.ib.client.OrderState;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.assembler.cache.Cache;
import ru.prolib.aquila.ib.assembler.cache.ContractEntry;
import ru.prolib.aquila.ib.assembler.cache.ExecEntry;
import ru.prolib.aquila.ib.assembler.cache.OrderEntry;
import ru.prolib.aquila.ib.assembler.cache.OrderStatusEntry;
import ru.prolib.aquila.ib.assembler.cache.PositionEntry;

public class CacheTest {
	private IMocksControl control;
	private EventSystem es;
	private EventDispatcher dispatcher, mockDispatcher;
	private EventType onContractUpdated, onOrderUpdated, onOrderStatusUpdated,
		onPositionUpdated, onExecUpdated; 
	private Cache cache;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		mockDispatcher = control.createMock(EventDispatcher.class);
		es = new EventSystemImpl();
		dispatcher = es.createEventDispatcher("disp");
		onContractUpdated = dispatcher.createType("contract");
		onOrderUpdated = dispatcher.createType("order");
		onOrderStatusUpdated = dispatcher.createType("orderStatus");
		onPositionUpdated = dispatcher.createType("position");
		onExecUpdated = dispatcher.createType("exec");
		cache = new Cache(mockDispatcher, onContractUpdated,
				onOrderUpdated, onOrderStatusUpdated,
				onPositionUpdated, onExecUpdated);
	}
	
	/**
	 * Создать экземпляр состояния заявки.
	 * <p>
	 * Конструктор класса статуса защищенный. Данный метод создает экземпляр
	 * используя рефлекшн API.
	 * <p>
	 * @return новый экземпляр состояния
	 * @throws Exception
	 */
	private OrderState createOrderState() throws Exception {
		Constructor<OrderState> con = OrderState.class.getDeclaredConstructor();
		con.setAccessible(true);
		con.newInstance();
		return con.newInstance();		
	}
	
	/**
	 * Создать тестовую кэш-запись контракта.
	 * <p>
	 * @param id идентификатор контракта
	 * @param symbol тикер
	 * @param exchange биржа
	 * @param currency валюта
	 * @param type тип инструмента
	 * @return кэш-запись контракта
	 */
	private ContractEntry createContractEntry(int id, String symbol,
			String exchange, String currency, String type)
	{
		ContractDetails details1 = new ContractDetails();
		details1.m_summary.m_conId = id;
		details1.m_summary.m_symbol = symbol;
		details1.m_summary.m_exchange = exchange;
		details1.m_summary.m_currency = currency;
		details1.m_summary.m_secType = type;
		return new ContractEntry(details1);
	}
	
	/**
	 * Создать тестовую кэш-запись заявки.
	 * <p>
	 * @param id идентификатор (используется везде, где это необходимо)
	 * @return кэш-запись заявки
	 * @throws Exception
	 */
	private OrderEntry createOrderEntry(int id) throws Exception {
		Contract contract = new Contract();
		contract.m_conId = id;
		Order order = new Order();
		order.m_permId = id;
		order.m_orderId = id;
		OrderState state = createOrderState();
		state.m_status = "foo";
		return new OrderEntry(id, contract, order, state);
	}
	
	/**
	 * Создать тестовую кэш-запись статуса заявки.
	 * <p>
	 * @param id идентификатор заявки
	 * @return кэш-запись статуса заявки
	 */
	private OrderStatusEntry createOrderStatusEntry(int id) {
		return new OrderStatusEntry(id, "Submitted", 5, 24.93d);
	}
	
	/**
	 * Создать тестовую кэш-запись сделки.
	 * <p>
	 * @param orderId номер заявки
	 * @param execId IB-идентификатор сделки
	 * @return кэш-запись сделки
	 */
	private ExecEntry createExecEntry(int orderId, String execId) {
		Contract contract = new Contract();
		Execution execution = new Execution();
		execution.m_orderId = orderId;
		execution.m_execId = execId;
		return new ExecEntry(contract, execution);
	}
	
	/**
	 * Создать тестовую кэш-запись позиции.
	 * <p>
	 * @param id идентификатор контракта
	 * @param acnt код торгового счета
	 * @return кэш-запись позиции
	 */
	private PositionEntry createPositionEntry(int id, String acnt) {
		Contract contract = new Contract();
		contract.m_conId = id;
		return new PositionEntry(contract, -5, -1000.0d, 180.5d, 130d, acnt);
	}
	
	@Test
	public void testGetEventTypes() throws Exception {
		assertSame(onContractUpdated, cache.OnContractUpdated());
		assertSame(onOrderUpdated, cache.OnOrderUpdated());
		assertSame(onOrderStatusUpdated, cache.OnOrderStatusUpdated());
		assertSame(onPositionUpdated, cache.OnPositionUpdated());
		assertSame(onExecUpdated, cache.OnExecUpdated());
	}
	
	@Test
	public void testUpdate_Contract() throws Exception {
		mockDispatcher.dispatch(eq(new EventImpl(onContractUpdated)));
		expectLastCall().times(3);
		ContractEntry
			entry1 = createContractEntry(15, "SBH", "ARCA", "USD", "FUT"),
			entry2 = createContractEntry(11, "ZZT", "LSE", "EUR", "OPT"),
			entry3 = createContractEntry(18, "BBQ", "EQBR", "SUR", "STK");
		control.replay();
		
		cache.update(entry1);
		cache.update(entry2);
		cache.update(entry3);
		
		control.verify();
		assertSame(entry1, cache.getContract(15));
		assertSame(entry2, cache.getContract(11));
		assertSame(entry3, cache.getContract(18));
		List<ContractEntry> expected = new Vector<ContractEntry>();
		expected.add(entry1);
		expected.add(entry2);
		expected.add(entry3);
		assertEquals(expected, cache.getContractEntries());
	}
	
	
	@Test
	public void testUpdate_Order() throws Exception {
		mockDispatcher.dispatch(eq(new EventImpl(onOrderUpdated)));
		expectLastCall().times(2);
		OrderEntry entry1 = createOrderEntry(125);
		OrderEntry entry2 = createOrderEntry(84);
		control.replay();
		
		cache.update(entry1);
		cache.update(entry2);
		
		control.verify();
		assertSame(entry1, cache.getOrder(125));
		assertSame(entry2, cache.getOrder(84));
		List<OrderEntry> expected = new Vector<OrderEntry>();
		expected.add(entry1);
		expected.add(entry2);
		assertEquals(expected, cache.getOrderEntries());
	}
	
	@Test
	public void testUpdate_OrderStatus() throws Exception {
		mockDispatcher.dispatch(eq(new EventImpl(onOrderStatusUpdated)));
		expectLastCall().times(3);
		OrderStatusEntry entry1 = createOrderStatusEntry(824);
		OrderStatusEntry entry2 = createOrderStatusEntry(127);
		OrderStatusEntry entry3 = createOrderStatusEntry(512);
		control.replay();
		
		cache.update(entry1);
		cache.update(entry2);
		cache.update(entry3);
		
		control.verify();
		assertSame(entry1, cache.getOrderStatus(824));
		assertSame(entry2, cache.getOrderStatus(127));
		assertSame(entry3, cache.getOrderStatus(512));
		List<OrderStatusEntry> expected = new Vector<OrderStatusEntry>();
		expected.add(entry1);
		expected.add(entry2);
		expected.add(entry3);
		assertEquals(expected, cache.getOrderStatusEntries());
	}
	
	@Test
	public void testUpdate_Position() throws Exception {
		mockDispatcher.dispatch(eq(new EventImpl(onPositionUpdated)));
		expectLastCall().times(3);
		PositionEntry entry1 = createPositionEntry(812, "TEST");
		PositionEntry entry2 = createPositionEntry(812, "BEST");
		PositionEntry entry3 = createPositionEntry(117, "TEST");
		control.replay();
		
		cache.update(entry1);
		cache.update(entry2);
		cache.update(entry3);
		
		control.verify();
		assertSame(entry1, cache.getPosition(new Account("TEST"), 812));
		assertSame(entry2, cache.getPosition(new Account("BEST"), 812));
		assertSame(entry3, cache.getPosition(new Account("TEST"), 117));
		List<PositionEntry> expected = new Vector<PositionEntry>();
		expected.add(entry1);
		expected.add(entry2);
		expected.add(entry3);
		assertEquals(expected, cache.getPositionEntries());
	}
	
	@Test
	public void testUpdate_Exec() throws Exception {
		mockDispatcher.dispatch(eq(new EventImpl(onExecUpdated)));
		expectLastCall().times(4);
		ExecEntry entry1 = createExecEntry(8, "foo-1");
		ExecEntry entry2 = createExecEntry(11, "foo-2");
		ExecEntry entry3 = createExecEntry(11, "bar-4");
		ExecEntry entry4 = createExecEntry(8, "foo-5");
		control.replay();
		
		cache.update(entry1);
		cache.update(entry2);
		cache.update(entry3);
		cache.update(entry4);
		
		control.verify();
		List<ExecEntry> expected = new Vector<ExecEntry>();
		expected.add(entry1);
		expected.add(entry4);
		assertEquals(expected, cache.getOrderExecutions(8));
		
		expected.clear();
		expected.add(entry2);
		expected.add(entry3);
		assertEquals(expected, cache.getOrderExecutions(11));
		
		expected.clear();
		expected.add(entry1);
		expected.add(entry4);
		expected.add(entry2);
		expected.add(entry3);
		assertEquals(expected, cache.getExecEntries());
	}
	
	@Test
	public void testPurgeOrder() throws Exception {
		mockDispatcher.dispatch(eq(new EventImpl(onOrderUpdated)));
		OrderEntry entry1 = createOrderEntry(84);
		control.replay();
		cache.update(entry1);
		control.verify();
		
		cache.purgeOrder(84);
		
		assertNull(cache.getOrder(84));
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(cache.equals(cache));
		assertFalse(cache.equals(null));
		assertFalse(cache.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		List<ContractEntry> conts1 = new Vector<ContractEntry>();
		conts1.add(createContractEntry(80, "QQQ", "TST", "USD", "STK"));
		conts1.add(createContractEntry(15, "AAA", "GGG", "JPY", "FUT"));
		conts1.add(createContractEntry(11, "GAZP", "EQBR", "SUR", "STK"));
		List<ContractEntry> conts2 = new Vector<ContractEntry>();
		conts2.add(conts1.get(0));
		conts2.add(conts1.get(2));
		conts2.add(createContractEntry(42, "IBKR", "ARCA", "USD", "STK"));
		
		List<OrderEntry> ords1 = new Vector<OrderEntry>();
		ords1.add(createOrderEntry(112));
		ords1.add(createOrderEntry(221));
		List<OrderEntry> ords2 = new Vector<OrderEntry>();
		ords2.add(createOrderEntry(824));
		ords2.add(ords1.get(1));
		ords2.add(createOrderEntry(815));
		
		List<OrderStatusEntry> stats1 = new Vector<OrderStatusEntry>();
		stats1.add(createOrderStatusEntry(14));
		List<OrderStatusEntry> stats2 = new Vector<OrderStatusEntry>();
		stats2.add(createOrderStatusEntry(52));
		stats2.add(stats1.get(0));
		
		List<PositionEntry> posits1 = new Vector<PositionEntry>();
		posits1.add(createPositionEntry(85, "TEST"));
		posits1.add(createPositionEntry(85, "BEST"));
		List<PositionEntry> posits2 = new Vector<PositionEntry>();
		posits2.add(posits1.get(1));
		
		List<ExecEntry> execs1 = new Vector<ExecEntry>();
		execs1.add(createExecEntry(18, "xxl-1"));
		execs1.add(createExecEntry(18, "xxl-2"));
		List<ExecEntry> execs2 = new Vector<ExecEntry>();
		execs2.add(createExecEntry(24, "zoo-5"));

		cache = new Cache(dispatcher, onContractUpdated, onOrderUpdated,
				onOrderStatusUpdated, onPositionUpdated, onExecUpdated);
		for ( ContractEntry entry : conts1 ) cache.update(entry);
		for ( OrderEntry entry : ords1 ) cache.update(entry);
		for ( OrderStatusEntry entry : stats1 ) cache.update(entry);
		for ( PositionEntry entry : posits1 ) cache.update(entry);
		for ( ExecEntry entry : execs1 ) cache.update(entry);
		
		Variant<String> vDispId = new Variant<String>()
			.add("disp")
			.add("dispX");
		Variant<String> vContId = new Variant<String>(vDispId)
			.add("contract")
			.add("contractX");
		Variant<String> vOrdId = new Variant<String>(vContId)
			.add("order")
			.add("orderX");
		Variant<String> vStatId = new Variant<String>(vOrdId)
			.add("orderStatus")
			.add("orderStatusX");
		Variant<String> vPosId = new Variant<String>(vStatId)
			.add("position")
			.add("positionX");
		Variant<String> vExecId = new Variant<String>(vPosId)
			.add("exec")
			.add("execX");
		Variant<List<ContractEntry>> vCont =
				new Variant<List<ContractEntry>>(vExecId)
			.add(conts1)
			.add(conts2);
		Variant<List<OrderEntry>> vOrd = new Variant<List<OrderEntry>>(vCont)
			.add(ords1)
			.add(ords2);
		Variant<List<OrderStatusEntry>> vStat =
				new Variant<List<OrderStatusEntry>>(vOrd)
			.add(stats1)
			.add(stats2);
		Variant<List<PositionEntry>> vPos =
				new Variant<List<PositionEntry>>(vStat)
			.add(posits1)
			.add(posits2);
		Variant<List<ExecEntry>> vExec = new Variant<List<ExecEntry>>(vPos)
			.add(execs1)
			.add(execs2);
		Variant<?> iterator = vExec;
		int foundCnt = 0;
		Cache x = null, found = null;
		do {
			EventDispatcher d = es.createEventDispatcher(vDispId.get());
			x = new Cache(d, d.createType(vContId.get()),
					d.createType(vOrdId.get()), d.createType(vStatId.get()),
					d.createType(vPosId.get()), d.createType(vExecId.get()));
			for ( ContractEntry entry : vCont.get() ) x.update(entry);
			for ( OrderEntry entry : vOrd.get() ) x.update(entry);
			for ( OrderStatusEntry entry : vStat.get() ) x.update(entry);
			for ( PositionEntry entry : vPos.get() ) x.update(entry);
			for ( ExecEntry entry : vExec.get() ) x.update(entry);
			if ( cache.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(dispatcher, found.getEventDispatcher());
		assertEquals(onContractUpdated, found.OnContractUpdated());
		assertEquals(onOrderUpdated, found.OnOrderUpdated());
		assertEquals(onOrderStatusUpdated, found.OnOrderStatusUpdated());
		assertEquals(onPositionUpdated, found.OnPositionUpdated());
		assertEquals(onExecUpdated, found.OnExecUpdated());
		assertEquals(conts1, found.getContractEntries());
		assertEquals(ords1, found.getOrderEntries());
		assertEquals(stats1, found.getOrderStatusEntries());
		assertEquals(posits1, found.getPositionEntries());
		assertEquals(execs1, found.getExecEntries());
	}
	
	@Test
	public void testGetContract_ByDescr() throws Exception {
		mockDispatcher.dispatch(eq(new EventImpl(onContractUpdated)));
		expectLastCall().anyTimes();
		
		ContractEntry
			entry1 = createContractEntry(15, "AAPL", "NASDAQ", "USD", "STK"),
			entry2 = createContractEntry(11, "SPXS", "ARCA", "USD", "STK"),
			entry3 = createContractEntry(18, "SBER", "LSE", "EUR", "OPT");
		control.replay();
		
		cache.update(entry1);
		cache.update(entry2);
		cache.update(entry3);
		
		control.verify();
		SecurityDescriptor d1,d2,d3;
		d1 = new SecurityDescriptor("AAPL", "NASDAQ", "USD", SecurityType.STK);
		d2 = new SecurityDescriptor("SPXS", "ARCA", "USD", SecurityType.STK);
		d3 = new SecurityDescriptor("SBER", "LSE", "EUR", SecurityType.OPT);
		assertSame(entry1, cache.getContract(d1));
		assertSame(entry2, cache.getContract(d2));
		assertSame(entry3, cache.getContract(d3));
	}

}
