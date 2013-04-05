package ru.prolib.aquila.ib.subsys.run;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.rule.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.data.S;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.event.IBEventOpenOrder;
import ru.prolib.aquila.ib.event.IBEventOrder;
import ru.prolib.aquila.ib.event.IBEventOrderStatus;
import ru.prolib.aquila.ib.event.IBEventUpdateAccount;
import ru.prolib.aquila.ib.event.IBEventUpdatePortfolio;
import ru.prolib.aquila.ib.subsys.contract.IBContracts;
import ru.prolib.aquila.ib.subsys.run.IBRunnableFactoryImpl;
import ru.prolib.aquila.ib.subsys.run.IBRunnableUpdateAccount;
import ru.prolib.aquila.ib.subsys.run.IBRunnableUpdateOrder;
import ru.prolib.aquila.ib.subsys.run.IBRunnableUpdatePosition;

/**
 * 2013-01-07<br>
 * $Id: IBRunnableFactoryImplTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class IBRunnableFactoryImplTest {
	private static IMocksControl control;
	private static EditableTerminal terminal;
	private static PortfolioFactory fport;
	private static IBContracts contracts;
	private static OrderResolver resolver;
	private static S<EditablePortfolio> mPortfolio;
	private static S<EditableOrder> mOrder;
	private static S<EditablePosition> mPosition;
	private static IBRunnableFactoryImpl factory;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		terminal = control.createMock(EditableTerminal.class);
		fport = control.createMock(PortfolioFactory.class);
		contracts = control.createMock(IBContracts.class);
		resolver = control.createMock(OrderResolver.class);
		mPortfolio = control.createMock(S.class);
		mOrder = control.createMock(S.class);
		mPosition = control.createMock(S.class);
		factory = new IBRunnableFactoryImpl(terminal, fport, contracts,
				resolver, mPortfolio, mOrder, mPosition);
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(terminal, factory.getTerminal());
		assertSame(fport, factory.getPortfolioFactory());
		assertSame(contracts, factory.getContracts());
		assertSame(resolver, factory.getOrderResolver());
		assertSame(mPortfolio, factory.getPortfolioModifier());
		assertSame(mOrder, factory.getOrderModifier());
		assertSame(mPosition, factory.getPositionModifier());
	}
	
	@Test
	public void testCreateUpdateOrder_ForOrderStatus() throws Exception {
		IBEventOrder event = control.createMock(IBEventOrderStatus.class);
		assertEquals(new IBRunnableUpdateOrder(resolver, mOrder, event),
				factory.createUpdateOrder(event));
	}

	@Test
	public void testCreateUpdateOrder_ForOpenOrderAndLoaded() throws Exception {
		IBEventOpenOrder event = control.createMock(IBEventOpenOrder.class);
		expect(event.getContractId()).andStubReturn(728);
		expect(contracts.isContractAvailable(eq(728))).andReturn(true);
		control.replay();
		assertEquals(new IBRunnableUpdateOrder(resolver, mOrder, event),
				factory.createUpdateOrder(event));
		control.verify();
	}
	
	@Test
	public void testCreateUpdateOrder_ForOpenOrderAndNotLoaded()
			throws Exception
	{
		IBEventOpenOrder event = control.createMock(IBEventOpenOrder.class);
		EventType type = control.createMock(EventType.class);
		expect(event.getContractId()).andStubReturn(728);
		expect(contracts.isContractAvailable(eq(728))).andReturn(false);
		contracts.loadContract(eq(728));
		expect(contracts.OnContractLoadedOnce(eq(728))).andReturn(type);

		control.replay();
		assertEquals(new RunOnceOnEvent(type,
				new IBRunnableUpdateOrder(resolver, mOrder, event)),
				factory.createUpdateOrder(event));
		control.verify();
	}
	
	@Test
	public void testCreateUpdatePosition_ForLoaded() throws Exception {
		IBEventUpdatePortfolio event =
				control.createMock(IBEventUpdatePortfolio.class);
		expect(event.getContractId()).andReturn(182);
		expect(contracts.isContractAvailable(eq(182))).andReturn(true);
		control.replay();
		assertEquals(new IBRunnableUpdatePosition(terminal, contracts,
				mPosition, event), factory.createUpdatePosition(event));
		control.verify();
	}
	
	@Test
	public void testCreateUpdatePosition_ForNotLoaded() throws Exception {
		IBEventUpdatePortfolio event =
				control.createMock(IBEventUpdatePortfolio.class);
		EventType type = control.createMock(EventType.class);
		expect(event.getContractId()).andReturn(182);
		expect(contracts.isContractAvailable(eq(182))).andReturn(false);
		contracts.loadContract(eq(182));
		expect(contracts.OnContractLoadedOnce(eq(182))).andReturn(type);
		control.replay();
		assertEquals(new RunOnceOnEvent(type, new IBRunnableUpdatePosition(
				terminal, contracts, mPosition, event)),
				factory.createUpdatePosition(event));
		control.verify();
	}
	
	@Test
	public void testCreateUpdateAccount() throws Exception {
		IBEventUpdateAccount event =
				control.createMock(IBEventUpdateAccount.class);
		control.replay();
		assertEquals(new IBRunnableUpdateAccount(terminal, fport,
				mPortfolio, event), factory.createUpdateAccount(event));
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(factory.equals(factory));
		assertFalse(factory.equals(null));
		assertFalse(factory.equals(this));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<EditableTerminal> vTerm = new Variant<EditableTerminal>()
			.add(control.createMock(EditableTerminal.class))
			.add(terminal);
		Variant<PortfolioFactory> vFport = new Variant<PortfolioFactory>(vTerm)
			.add(control.createMock(PortfolioFactory.class))
			.add(fport);
		Variant<IBContracts> vCont = new Variant<IBContracts>(vFport)
			.add(control.createMock(IBContracts.class))
			.add(contracts);
		Variant<OrderResolver> vRes = new Variant<OrderResolver>(vCont)
			.add(control.createMock(OrderResolver.class))
			.add(resolver);
		Variant<S<EditablePortfolio>> vmPort =
				new Variant<S<EditablePortfolio>>(vRes)
			.add(control.createMock(S.class))
			.add(mPortfolio);
		Variant<S<EditableOrder>> vmOrd = new Variant<S<EditableOrder>>(vmPort)
			.add(control.createMock(S.class))
			.add(mOrder);
		Variant<S<EditablePosition>> vmPos =
				new Variant<S<EditablePosition>>(vmOrd)
			.add(control.createMock(S.class))
			.add(mPosition);
		Variant<?> iterator = vmPos;
		int foundCnt = 0;
		IBRunnableFactoryImpl x = null, found = null;
		do {
			x = new IBRunnableFactoryImpl(vTerm.get(), vFport.get(),
					vCont.get(), vRes.get(), vmPort.get(), vmOrd.get(),
					vmPos.get());
			if ( factory.equals(x) ) {
				found = x;
				foundCnt ++;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(terminal, found.getTerminal());
		assertSame(fport, found.getPortfolioFactory());
		assertSame(contracts, found.getContracts());
		assertSame(resolver, found.getOrderResolver());
		assertSame(mPortfolio, found.getPortfolioModifier());
		assertSame(mOrder, found.getOrderModifier());
		assertSame(mPosition, found.getPositionModifier());
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20130107, 82347)
			.append(terminal)
			.append(fport)
			.append(contracts)
			.append(resolver)
			.append(mPortfolio)
			.append(mOrder)
			.append(mPosition)
			.toHashCode(), factory.hashCode());
	}

}
