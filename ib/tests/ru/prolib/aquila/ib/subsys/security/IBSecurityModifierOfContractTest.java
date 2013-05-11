package ru.prolib.aquila.ib.subsys.security;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;
import com.ib.client.ContractDetails;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.ib.subsys.security.IBSecurityModifierOfContract;

/**
 * 2012-11-22<br>
 * $Id: IBSecurityModifierOfContractTest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class IBSecurityModifierOfContractTest {
	private static IBSecurityModifierOfContract modifier;
	private IMocksControl control;
	private EditableSecurity security;
	private ContractDetails details;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		modifier = new IBSecurityModifierOfContract();
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		security = control.createMock(EditableSecurity.class);
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
			setUp();
			Double minTick = (Double) fix[i][0];
			int prec = (Integer) fix[i][1];
			details.m_minTick = minTick;
			security.setMinStepPrice(eq(minTick));
			security.setMinStepSize(eq((double) minTick));
			security.setLotSize(eq(1));
			security.setPrecision(eq(prec));
			security.setDisplayName(eq("Name"));
			control.replay();
			modifier.set(security, details);
			control.verify();
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
