package ru.prolib.aquila.ib.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.ib.getter.IBGetSecurityDescriptor;
import ru.prolib.aquila.ib.subsys.contract.IBContractUnavailableException;
import ru.prolib.aquila.ib.subsys.contract.IBContracts;

import com.ib.client.Contract;

/**
 * 2012-12-15<br>
 * $Id: IBGetSecurityDescriptorTest.java 490 2013-02-05 19:42:02Z whirlwind $
 */
public class IBGetSecurityDescriptorTest {
	private static IMocksControl control;
	private static IBContracts contracts;
	private static IBGetSecurityDescriptor getter;
	private static Contract contract;
	private static SecurityDescriptor descr;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		control = createStrictControl();
		contracts = control.createMock(IBContracts.class);
		descr = new SecurityDescriptor("SBER", "EQBR", "RUB", SecurityType.STK);
		getter = new IBGetSecurityDescriptor(contracts);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
		contract = new Contract();
		contract.m_conId = 1852;
	}

	@Test
	public void testGet_Ok() throws Exception {
		expect(contracts.getAppropriateSecurityDescriptor(eq(1852)))
				.andReturn(descr);
		control.replay();
		assertEquals(descr, getter.get(contract));
		control.verify();
	}
	
	@Test
	public void testGet_IfNotAContract() throws Exception {
		control.replay();
		assertNull(getter.get(null));
		assertNull(getter.get(this));
		assertNull(getter.get(getter));
		control.verify();
	}
	
	@Test
	public void testGet_IfNoContractLoaded() throws Exception {
		expect(contracts.getAppropriateSecurityDescriptor(eq(1852)))
			.andThrow(new IBContractUnavailableException(1825));
		control.replay();
		assertNull(getter.get(contract));
		control.verify();
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new IBGetSecurityDescriptor(contracts)));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
		IBContracts contracts2 = control.createMock(IBContracts.class);
		assertFalse(getter.equals(new IBGetSecurityDescriptor(contracts2)));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121215, 173649)
			.append(IBGetSecurityDescriptor.class)
			.toHashCode(), getter.hashCode());
	}

}
