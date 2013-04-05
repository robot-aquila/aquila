package ru.prolib.aquila.ib.subsys.contract;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.utils.Variant;

import com.ib.client.ContractDetails;

/**
 * 2013-01-07<br>
 * $Id: IBContractsImplTest.java 499 2013-02-07 10:43:25Z whirlwind $
 */
public class IBContractsImplTest {
	private static IMocksControl control;
	private static IBContractsStorage storage;
	private static IBContractUtils utils;
	private static IBContractsImpl contracts;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		storage = control.createMock(IBContractsStorage.class);
		utils = control.createMock(IBContractUtils.class);
		contracts = new IBContractsImpl(storage, utils);
	}

	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(storage, contracts.getContractsStorage());
		assertSame(utils, contracts.getContractUtils());
	}
	
	@Test
	public void testGetContract() throws Exception {
		ContractDetails contract = new ContractDetails();
		expect(storage.getContract(eq(726))).andReturn(contract);
		control.replay();
		assertSame(contract, contracts.getContract(726));
		control.verify();
	}
	
	@Test
	public void testIsContractAvailable() throws Exception {
		expect(storage.isContractAvailable(eq(225))).andReturn(true);
		expect(storage.isContractAvailable(eq(225))).andReturn(false);
		control.replay();
		assertTrue(contracts.isContractAvailable(225));
		assertFalse(contracts.isContractAvailable(225));
		control.verify();
	}
	
	@Test
	public void testLoadContract() throws Exception {
		storage.loadContract(eq(627));
		control.replay();
		contracts.loadContract(627);
		control.verify();
	}
	
	@Test
	public void testOnContractLoadedOnce() throws Exception {
		EventType type = control.createMock(EventType.class);
		expect(storage.OnContractLoadedOnce(eq(156))).andReturn(type);
		control.replay();
		assertSame(type, contracts.OnContractLoadedOnce(156));
		control.verify();
	}
	
	@Test
	public void testStart() throws Exception {
		storage.start();
		control.replay();
		contracts.start();
		control.verify();
	}
	
	@Test
	public void testGetAppropriateSecurityDescriptor_ByCont() throws Exception {
		ContractDetails details = new ContractDetails();
		SecurityDescriptor descr =
				new SecurityDescriptor("SBER", "EQBR", "RUB", SecurityType.STK);
		expect(utils.getAppropriateSecurityDescriptor(same(details)))
				.andReturn(descr);
		control.replay();
		contracts.getAppropriateSecurityDescriptor(details);
		control.verify();
	}
	
	@Test
	public void testGetAppropriateSecurityDescriptor_ById() throws Exception {
		ContractDetails details = new ContractDetails();
		SecurityDescriptor descr =
				new SecurityDescriptor("SBER", "EQBR", "RUB", SecurityType.STK);
		expect(storage.getContract(eq(827))).andReturn(details);
		expect(utils.getAppropriateSecurityDescriptor(same(details)))
				.andReturn(descr);
		control.replay();
		contracts.getAppropriateSecurityDescriptor(827);
		control.verify();
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(contracts.equals(contracts));
		assertFalse(contracts.equals(null));
		assertFalse(contracts.equals(this));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<IBContractsStorage> vStor = new Variant<IBContractsStorage>()
			.add(storage)
			.add(control.createMock(IBContractsStorage.class));
		Variant<IBContractUtils> vUtil = new Variant<IBContractUtils>(vStor)
			.add(control.createMock(IBContractUtils.class))
			.add(utils);
		Variant<?> iterator = vUtil;
		int foundCnt = 0;
		IBContractsImpl x = null, found = null;
		do {
			x = new IBContractsImpl(vStor.get(), vUtil.get());
			if ( contracts.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(storage, found.getContractsStorage());
		assertSame(utils, found.getContractUtils());
	}

}
