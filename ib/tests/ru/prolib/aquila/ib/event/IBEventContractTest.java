package ru.prolib.aquila.ib.event;

import static org.easymock.EasyMock.createStrictControl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ib.client.ContractDetails;

import ru.prolib.aquila.core.EventType;
import ru.prolib.aquila.core.utils.Variant;
import ru.prolib.aquila.ib.event.IBEventContract;

/**
 * 2012-11-18<br>
 * $Id: IBEventContractTest.java 433 2013-01-14 22:37:52Z whirlwind $
 */
public class IBEventContractTest {
	private static IMocksControl control;
	private static EventType type;
	private static ContractDetails details;
	private static IBEventContract event;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		type = control.createMock(EventType.class);
		details = new ContractDetails();
		details.m_longName = "Apple Inc";
		event = new IBEventContract(type, 123,
				IBEventContract.SUBTYPE_NORM, details);
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct2() throws Exception {
		EventType type2 = control.createMock(EventType.class);
		IBEventContract event2 = new IBEventContract(type2, event);
		assertSame(type2, event2.getType());
		assertEquals(123, event2.getReqId());
		assertEquals(IBEventContract.SUBTYPE_NORM, event2.getSubType());
		assertSame(details, event2.getContractDetails());
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<EventType> vTyp = new Variant<EventType>()
			.add(type)
			.add(control.createMock(EventType.class));
		Variant<Integer> vId = new Variant<Integer>(vTyp)
			.add(123)
			.add(5);
		Variant<Integer> vSub = new Variant<Integer>(vId)
			.add(IBEventContract.SUBTYPE_BOND)
			.add(IBEventContract.SUBTYPE_NORM);
		ContractDetails d1 = new ContractDetails();
		d1.m_longName = "Cucaracha Corp";
		Variant<ContractDetails> vDet = new Variant<ContractDetails>(vSub)
			.add(d1)
			.add(details);
		int foundCnt = 0;
		IBEventContract found = null, x = null;
		do {
			x = new IBEventContract(vTyp.get(),vId.get(),vSub.get(),vDet.get());
			if ( event.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( vDet.next() );
		assertEquals(1, foundCnt);
		assertSame(type, found.getType());
		assertEquals(123, found.getReqId());
		assertEquals(IBEventContract.SUBTYPE_NORM, found.getSubType());
		assertSame(details, found.getContractDetails());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(event.equals(event));
		assertFalse(event.equals(null));
		assertFalse(event.equals(this));
	}

}
