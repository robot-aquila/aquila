package ru.prolib.aquila.ib.subsys;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import com.ib.client.EClientSocket;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.utils.*;
import ru.prolib.aquila.ib.subsys.IBServiceLocator;
import ru.prolib.aquila.ib.subsys.account.IBIsPortfolioAvailable;
import ru.prolib.aquila.ib.subsys.api.*;
import ru.prolib.aquila.ib.subsys.contract.*;
import ru.prolib.aquila.ib.subsys.order.IBIsEventOpenOrder;

/**
 * 2012-11-22<br>
 * $Id: IBCompFactoryImplTest.java 553 2013-03-01 13:37:31Z whirlwind $
 */
public class IBCompFactoryImplTest {
	private static IMocksControl control;
	private static IBServiceLocator locator;
	private static BMFactory bfactory;
	private static IBModifierFactory mfactory;
	private static IBCompFactoryImpl factory;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		locator = control.createMock(IBServiceLocator.class);
		bfactory = control.createMock(BMFactory.class);
		mfactory = control.createMock(IBModifierFactory.class);
		factory = new IBCompFactoryImpl(locator, bfactory, mfactory);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct3() throws Exception {
		assertSame(locator, factory.getServiceLocator());
		assertSame(bfactory, factory.getBusinessModelFactory());
		assertSame(mfactory, factory.getModifierFactory());
	}
	
	@Test
	public void testCreateOrderFactory() throws Exception {
		OrderFactory o = control.createMock(OrderFactory.class);
		expect(bfactory.createOrderFactory()).andReturn(o);
		control.replay();
		assertSame(o, factory.createOrderFactory());
		control.verify();
	}

	@Test
	public void testCreateOrders() throws Exception {
		EditableOrders o = control.createMock(EditableOrders.class);
		expect(bfactory.createOrders()).andReturn(o);
		control.replay();
		assertSame(o, factory.createOrders());
		control.verify();
	}
	
	@Test
	public void testCreatePortfolioFactory() throws Exception {
		PortfolioFactory o = control.createMock(PortfolioFactory.class);
		expect(bfactory.createPortfolioFactory()).andReturn(o);
		control.replay();
		assertSame(o, factory.createPortfolioFactory());
		control.verify();
	}
	
	@Test
	public void testCreatePortfolios() throws Exception {
		EditablePortfolios o = control.createMock(EditablePortfolios.class);
		expect(bfactory.createPortfolios()).andReturn(o);
		control.replay();
		assertSame(o, factory.createPortfolios());
		control.verify();
	}
	
	@Test
	public void testCreatePositionFactory() throws Exception {
		Account account = new Account("ASX1");
		PositionFactory o = control.createMock(PositionFactory.class);
		expect(bfactory.createPositionFactory(account)).andReturn(o);
		control.replay();
		assertSame(o, factory.createPositionFactory(account));
		control.verify();
	}
	
	@Test
	public void testCreateSecurities() throws Exception {
		EditableSecurities o = control.createMock(EditableSecurities.class);
		expect(bfactory.createSecurities(eq("EUR"), same(SecurityType.CASH)))
			.andReturn(o);
		control.replay();
		assertSame(o, factory.createSecurities("EUR", SecurityType.CASH));
		control.verify();
	}
	
	@Test
	public void testCreateSecurityFactory() throws Exception {
		SecurityFactory o = control.createMock(SecurityFactory.class);
		expect(bfactory.createSecurityFactory()).andReturn(o);
		control.replay();
		assertSame(o, factory.createSecurityFactory());
		control.verify();
	}
	
	@Test
	public void testCreateTradeFactory() throws Exception {
		TradeFactory o = control.createMock(TradeFactory.class);
		expect(bfactory.createTradeFactory()).andReturn(o);
		control.replay();
		assertSame(o, factory.createTradeFactory());
		control.verify();
	}
	
	@Test
	public void testCreateClient() throws Exception {
		EventSystem eSys = control.createMock(EventSystem.class);
		EventDispatcher dispatcher = control.createMock(EventDispatcher.class);
		EventType onConnClosed = control.createMock(EventType.class);
		EventType onError = control.createMock(EventType.class);
		EventType onNextValidId = control.createMock(EventType.class);
		EventType onContractDetails = control.createMock(EventType.class);
		EventType onManagedAccounts = control.createMock(EventType.class);
		EventType onUpdateAccount = control.createMock(EventType.class);
		EventType onUpdatePortfolio = control.createMock(EventType.class);
		EventType onOpenOrder = control.createMock(EventType.class);
		EventType onOrderStatus = control.createMock(EventType.class);
		EventType onTick = control.createMock(EventType.class);
		EventType onConnOpened2 = control.createMock(EventType.class);
		EventType onConnClosed2 = control.createMock(EventType.class);
		expect(locator.getApiEventSystem()).andReturn(eSys);
		expect(eSys.createEventDispatcher()).andReturn(dispatcher);
		expect(eSys.createGenericType(dispatcher)).andReturn(onConnClosed);
		expect(eSys.createGenericType(dispatcher)).andReturn(onError);
		expect(eSys.createGenericType(dispatcher)).andReturn(onNextValidId);
		expect(eSys.createGenericType(dispatcher)).andReturn(onContractDetails);
		expect(eSys.createGenericType(dispatcher)).andReturn(onManagedAccounts);
		expect(eSys.createGenericType(dispatcher)).andReturn(onUpdateAccount);
		expect(eSys.createGenericType(dispatcher)).andReturn(onUpdatePortfolio);
		expect(eSys.createGenericType(dispatcher)).andReturn(onOpenOrder);
		expect(eSys.createGenericType(dispatcher)).andReturn(onOrderStatus);
		expect(eSys.createGenericType(dispatcher)).andReturn(onTick);
		expect(eSys.createGenericType(dispatcher)).andReturn(onConnOpened2);
		expect(eSys.createGenericType(dispatcher)).andReturn(onConnClosed2);
		control.replay();
		IBClientImpl client = (IBClientImpl) factory.createClient();
		control.verify();
		IBWrapper expectedWrapper = new IBWrapper(dispatcher,
				onConnClosed, onError, onNextValidId, onContractDetails,
				onManagedAccounts, onUpdateAccount, onUpdatePortfolio,
				onOpenOrder, onOrderStatus, onTick);
		EClientSocket socket = client.getSocket();
		assertEquals(expectedWrapper, socket.wrapper());
		assertEquals(expectedWrapper, client.getApiEventDispatcher());
		assertSame(dispatcher, client.getEventDispatcher());
		control.resetToNice();
		assertSame(onConnOpened2, client.OnConnectionOpened());
		assertSame(onConnClosed2, client.OnConnectionClosed());
	}

	@Test
	public void testCreateRequestFactory() throws Exception {
		EventSystem eSys = control.createMock(EventSystem.class);
		expect(locator.getEventSystem()).andReturn(eSys);
		control.replay();
		IBRequestFactory actual = factory.createRequestFactory();
		control.verify();
		assertEquals(new IBRequestFactoryImpl(eSys, locator), actual);
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<IBServiceLocator> vSL = new Variant<IBServiceLocator>()
			.add(locator)
			.add(control.createMock(IBServiceLocator.class));
		Variant<BMFactory> vBm = new Variant<BMFactory>(vSL)
			.add(bfactory)
			.add(control.createMock(BMFactory.class));
		Variant<IBModifierFactory> vMF = new Variant<IBModifierFactory>(vBm)
			.add(control.createMock(IBModifierFactory.class))
			.add(mfactory);
		Variant<?> iterator = vMF;
		int foundCnt = 0;
		IBCompFactoryImpl x = null, found = null;
		do {
			x = new IBCompFactoryImpl(vSL.get(), vBm.get(), vMF.get());
			if ( factory.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(locator, found.getServiceLocator());
		assertSame(bfactory, found.getBusinessModelFactory());
		assertSame(mfactory, found.getModifierFactory());
	}

	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(factory.equals(factory));
		assertFalse(factory.equals(null));
		assertFalse(factory.equals(this));
	}

	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121207, 55233)
			.append(locator)
			.append(bfactory)
			.append(mfactory)
			.toHashCode();
		assertEquals(hashCode, factory.hashCode());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		EventSystem eSys = control.createMock(EventSystem.class);
		EditableTerminal terminal = control.createMock(EditableTerminal.class);
		expect(locator.getEventSystem()).andStubReturn(eSys);
		expect(locator.getTerminal()).andStubReturn(terminal);
		control.replay();
		
		Object expected = new IBCompFactoryImpl(locator,
				new BMFactoryImpl(eSys, terminal),
				new IBModifierFactoryImpl(locator));
		
		assertEquals(expected, new IBCompFactoryImpl(locator));
		control.verify();
	}
	
	@Test
	public void testCreateOrderBuilder0() throws Exception {
		OrderBuilder builder = control.createMock(OrderBuilder.class);
		expect(bfactory.createOrderBuilder())
			.andReturn(builder);
		control.replay();
		assertSame(builder, factory.createOrderBuilder());
		control.verify();
	}
	
	@Test
	public void testCreateOrderBuilder1() throws Exception {
		OrderBuilder builder = control.createMock(OrderBuilder.class);
		Counter transId = control.createMock(Counter.class);
		expect(bfactory.createOrderBuilder(transId)).andReturn(builder);
		control.replay();
		assertSame(builder, factory.createOrderBuilder(transId));
		control.verify();
	}
	
	@Test
	public void testCreateOrderBuilder2() throws Exception {
		OrderBuilder builder = control.createMock(OrderBuilder.class);
		Counter transId = control.createMock(Counter.class);
		OrderFactory ofactory = control.createMock(OrderFactory.class);
		expect(bfactory.createOrderBuilder(transId, ofactory))
			.andReturn(builder);
		control.replay();
		assertSame(builder, factory.createOrderBuilder(transId, ofactory));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	public S<EditableOrder> getMOpenOrder() throws Exception {
		MList<EditableOrder> expected = new MListImpl<EditableOrder>();
		S<EditableOrder> modifier[] = new S[7];
		for ( int i = 0; i < modifier.length; i ++ ) {
			modifier[i] = control.createMock(S.class);
			expected.add(modifier[i]);
		}
		expect(mfactory.orderOoAccount()).andReturn(modifier[0]);
		expect(mfactory.orderOoDir()).andReturn(modifier[1]);
		expect(mfactory.orderOoQty()).andReturn(modifier[2]);
		expect(mfactory.orderOoSecurityDescr()).andReturn(modifier[3]);
		expect(mfactory.orderOoStatus()).andReturn(modifier[4]);
		expect(mfactory.orderOoType()).andReturn(modifier[5]);
		expect(bfactory.createOrderEG()).andReturn(modifier[6]);
		return expected;
	}
	
	@SuppressWarnings("unchecked")
	public S<EditableOrder> getMOrderStatus() throws Exception {
		MList<EditableOrder> expected = new MListImpl<EditableOrder>();
		S<EditableOrder> modifier[] = new S[4];
		for ( int i = 0; i < modifier.length; i ++ ) {
			modifier[i] = control.createMock(S.class);
			expected.add(modifier[i]);
		}
		expect(mfactory.orderOsStatus()).andReturn(modifier[0]);
		expect(mfactory.orderOsQtyRest()).andReturn(modifier[1]);
		expect(mfactory.orderOsExecutedVolume()).andReturn(modifier[2]);
		expect(bfactory.createOrderEG()).andReturn(modifier[3]);
		return expected;
	}
	
	@Test
	public void testMOrder() throws Exception {
		SSwitch<EditableOrder> expected = new SSwitch<EditableOrder>(
				new IBIsEventOpenOrder(), getMOpenOrder(), getMOrderStatus()); 
		control.replay();
		assertEquals(expected, factory.mOrder());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testMPortfolio() throws Exception {
		MList<EditablePortfolio> expected = new MListImpl<EditablePortfolio>();
		S<EditablePortfolio> modifier[] = new S[3];
		for ( int i = 0; i < modifier.length; i ++ ) {
			modifier[i] = control.createMock(S.class);
			expected.add(modifier[i]);
		}
		expect(mfactory.portCash()).andReturn(modifier[0]);
		expect(mfactory.portBalance()).andReturn(modifier[1]);
		expect(bfactory.createPortfolioEG(new IBIsPortfolioAvailable()))
				.andReturn(modifier[2]);
		control.replay();
		assertEquals(expected, factory.mPortfolio());
		control.verify();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMSecurity() throws Exception {
		MList<EditableSecurity> expected = new MListImpl<EditableSecurity>();
		S<EditableSecurity> modifier[] = new S[3];
		for ( int i = 0; i < modifier.length; i ++ ) {
			modifier[i] = control.createMock(S.class);
			expected.add(modifier[i]);
		}
		expect(mfactory.securityContract()).andReturn(modifier[0]);
		expect(mfactory.securityTick()).andReturn(modifier[1]);
		expect(bfactory.createSecurityEG()).andReturn(modifier[2]);
		control.replay();
		assertEquals(expected, factory.mSecurity());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testMPosition() throws Exception {
		MList<EditablePosition> expected = new MListImpl<EditablePosition>();
		S<EditablePosition> modifier[] = new S[5];
		for ( int i = 0; i < modifier.length; i ++ ) {
			modifier[i] = control.createMock(S.class);
			expected.add(modifier[i]);
		}
		expect(mfactory.posCurrQty()).andReturn(modifier[0]);
		expect(mfactory.posMarketValue()).andReturn(modifier[1]);
		expect(mfactory.posBookValue()).andReturn(modifier[2]);
		expect(mfactory.posVarMargin()).andReturn(modifier[3]);
		expect(bfactory.createPositionEG()).andReturn(modifier[4]);
		control.replay();
		assertEquals(expected, factory.mPosition());
		control.verify();
	}
	
	@Test
	public void testCreateContracts() throws Exception {
		EventDispatcher dispatcher = control.createMock(EventDispatcher.class);
		EventSystem eSys = control.createMock(EventSystem.class);
		expect(locator.getEventSystem()).andStubReturn(eSys);
		expect(eSys.createEventDispatcher()).andReturn(dispatcher);
		control.replay();
		
		IBContracts contracts = factory.createContracts();
		
		control.verify();
		IBContracts expected = new IBContractsImpl(
				new IBContractsStorageImpl(locator, dispatcher),
				new IBContractUtilsImpl());
		assertEquals(expected, contracts);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateOrderEG0() throws Exception {
		S<EditableOrder> modifier = control.createMock(S.class);
		expect(bfactory.createOrderEG()).andReturn(modifier);
		control.replay();
		assertSame(modifier, factory.createOrderEG());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateOrderEG1() throws Exception {
		S<EditableOrder> modifier = control.createMock(S.class);
		Validator isAvailable = control.createMock(Validator.class);
		expect(bfactory.createOrderEG(isAvailable)).andReturn(modifier);
		control.replay();
		assertSame(modifier, factory.createOrderEG(isAvailable));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateStopOrderEG0() throws Exception {
		S<EditableOrder> modifier = control.createMock(S.class);
		expect(bfactory.createStopOrderEG()).andReturn(modifier);
		control.replay();
		assertSame(modifier, factory.createStopOrderEG());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateStopOrderEG1() throws Exception {
		S<EditableOrder> modifier = control.createMock(S.class);
		Validator isAvailable = control.createMock(Validator.class);
		expect(bfactory.createStopOrderEG(isAvailable)).andReturn(modifier);
		control.replay();
		assertSame(modifier, factory.createStopOrderEG(isAvailable));
		control.verify();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreatePortfolioEG0() throws Exception {
		S<EditablePortfolio> modifier = control.createMock(S.class);
		expect(bfactory.createPortfolioEG()).andReturn(modifier);
		control.replay();
		assertSame(modifier, factory.createPortfolioEG());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreatePortfolioEG1() throws Exception {
		S<EditablePortfolio> modifier = control.createMock(S.class);
		Validator isAvailable = control.createMock(Validator.class);
		expect(bfactory.createPortfolioEG(isAvailable)).andReturn(modifier);
		control.replay();
		assertSame(modifier, factory.createPortfolioEG(isAvailable));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreatePositionEG0() throws Exception {
		S<EditablePosition> modifier = control.createMock(S.class);
		expect(bfactory.createPositionEG()).andReturn(modifier);
		control.replay();
		assertSame(modifier, factory.createPositionEG());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreatePositionEG1() throws Exception {
		S<EditablePosition> modifier = control.createMock(S.class);
		Validator isAvailable = control.createMock(Validator.class);
		expect(bfactory.createPositionEG(isAvailable)).andReturn(modifier);
		control.replay();
		assertSame(modifier, factory.createPositionEG(isAvailable));
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateSecurityEG0() throws Exception {
		S<EditableSecurity> modifier = control.createMock(S.class);
		expect(bfactory.createSecurityEG()).andReturn(modifier);
		control.replay();
		assertSame(modifier, factory.createSecurityEG());
		control.verify();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateSecurityEG1() throws Exception {
		S<EditableSecurity> modifier = control.createMock(S.class);
		Validator isAvailable = control.createMock(Validator.class);
		expect(bfactory.createSecurityEG(isAvailable)).andReturn(modifier);
		control.replay();
		assertSame(modifier, factory.createSecurityEG(isAvailable));
		control.verify();
	}

}
