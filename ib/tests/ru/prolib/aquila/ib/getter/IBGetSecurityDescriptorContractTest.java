package ru.prolib.aquila.ib.getter;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;
import com.ib.client.Contract;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.ib.getter.IBGetSecurityDescriptorContract;

/**
 * 2012-12-19<br>
 * $Id: IBGetSecurityDescriptorContractTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBGetSecurityDescriptorContractTest {
	private static SecurityDescriptor descr;
	private static IBGetSecurityDescriptorContract getter;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		descr = new SecurityDescriptor("AAPL", "SMRT", "EUR", SecurityType.STK);
		getter = new IBGetSecurityDescriptorContract();
	}
	
	@Before
	public void setUp() throws Exception {

	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(getter.equals(getter));
		assertTrue(getter.equals(new IBGetSecurityDescriptorContract()));
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
	}
	
	@Test
	public void testHashCode() throws Exception {
		assertEquals(new HashCodeBuilder(20121219, 5615)
			.toHashCode(), getter.hashCode());
	}
	
	@Test
	public void testGet_Ok() throws Exception {
		Contract contract = getter.get(descr);
		assertEquals("AAPL", contract.m_symbol);
		assertEquals("SMRT", contract.m_exchange);
		assertEquals("EUR", contract.m_currency);
		assertEquals("STK", contract.m_secType);
	}
	
	@Test
	public void testGet_IfNotASecurity() throws Exception {
		assertNull(getter.get(this));
	}
	
	@Test
	public void testGet_TypeTransform() throws Exception {
		Object fix[][] = {
				// local type, IB type
				{ SecurityType.BOND, "STK"  },
				{ SecurityType.CASH, "CASH" },
				{ SecurityType.FUT,  "FUT"  },
				{ SecurityType.OPT,  "OPT"  },
				{ SecurityType.STK,  "STK"  },
				{ SecurityType.UNK,  "STK"  },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			descr = new SecurityDescriptor("AAPL", "SMRT", "EUR",
					(SecurityType) fix[i][0]);
			Contract contract = getter.get(descr);
			assertEquals("At #" + i, fix[i][1], contract.m_secType);
		}
	}

}
