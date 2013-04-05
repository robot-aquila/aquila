package ru.prolib.aquila.ib.subsys.security;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;
import com.ib.client.ContractDetails;
import ru.prolib.aquila.core.EventSystemImpl;
import ru.prolib.aquila.core.BusinessEntities.utils.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.ib.subsys.security.IBSecurityModifierOfContract;

/**
 * 2012-11-22<br>
 * $Id: IBSecurityModifierOfContractTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class IBSecurityModifierOfContractTest {
	private static IBSecurityModifierOfContract modifier;
	private EditableSecurity security;
	private ContractDetails details;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		modifier = new IBSecurityModifierOfContract();
	}
	
	@Before
	public void setUp() throws Exception {
		security = new SecurityFactoryImpl(new EventSystemImpl(),
				new TerminalDecorator()).createSecurity(
			new SecurityDescriptor("SBER", "EQBR", "EUR", SecurityType.FUT));
		details = new ContractDetails();
		details.m_summary.m_localSymbol = "Name";
	}

	@Test
	public void testSet_IgnoreInvalidInstance() throws Exception {
		modifier.set(security, this);
	}
	
	@Test
	public void testSet() throws Exception {
		Object fix[][] = {
				// min tick, expected perc
				{ 0.010011d, 	6 },
				{ 1.0d,			0 },
				{ 5.0d,			0 },
				{ 5.000000001d,	0 },
				{ 100.50d,		1 },
				{ 0.01d,		2 },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			Double minTick = (Double) fix[i][0];
			details.m_minTick = minTick;
			modifier.set(security, details);
			assertEquals(msg, minTick, security.getMinStepPrice(), 0.0000001d);
			assertEquals(msg, minTick, security.getMinStepSize(), 0.0000001d);
			assertEquals(msg, 1, security.getLotSize());
			assertEquals(msg, fix[i][1], security.getPrecision());
			assertEquals(msg, "Name", security.getDisplayName());
		}
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(modifier.equals(modifier));
		assertTrue(modifier.equals(new IBSecurityModifierOfContract()));
		assertFalse(modifier.equals(null));
		assertFalse(modifier.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121123, 133255).toHashCode();
		assertEquals(hashCode, modifier.hashCode());
	}

}
