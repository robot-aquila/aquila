package ru.prolib.aquila.transaq.entity;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class SecurityUpdate1Test {
	private SecurityBoardParams board_params;
	private SecurityUpdate1 service;

	@Before
	public void setUp() throws Exception {
		board_params = new SecurityBoardParams(0, of("10"), of("1"), ofRUB5("12.90730"));
		service = new SecurityUpdate1(
				41190,
				true,
				"RIM9",
				"F",
				"FUT",
				4,
				"RTS-6.19",
				board_params, 
				0x11,
				SecType.FUT,
				"Russian Standard Time",
				1
			);
	}
	
	@Test
	public void testCtorX() {
		assertEquals(41190, service.getSecID());
		assertEquals(true, service.getActive());
		assertEquals("RIM9", service.getSecCode());
		assertEquals("F", service.getSecClass());
		assertEquals("FUT", service.getDefaultBoardCode());
		assertEquals(4, service.getMarketID());
		assertEquals("RTS-6.19", service.getShortName());
		assertEquals(board_params, service.getDefaultBoardParams());
		assertEquals(0x11, service.getOpMask());
		assertEquals(SecType.FUT, service.getSecType());
		assertEquals("Russian Standard Time", service.getSecTZ());
		assertEquals(1, service.getQuotesType());
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		Variant<Integer> vSecID = new Variant<>(41190, 72652);
		Variant<Boolean> vAct = new Variant<>(vSecID, true, false);
		Variant<String> vSecCod = new Variant<>(vAct, "RIM9", "ZEF");
		Variant<String> vSecCls = new Variant<>(vSecCod, "F", "P");
		Variant<String> vDBC = new Variant<>(vSecCls, "FUT", "NUT");
		Variant<Integer> vMktID = new Variant<>(vDBC, 4, 5);
		Variant<String> vSrtNam = new Variant<>(vMktID, "RTS-6.19", "GAZ-MAZ");
		Variant<SecurityBoardParams> vSBP = new Variant<SecurityBoardParams>(vSrtNam)
				.add(board_params)
				.add(new SecurityBoardParams(2, of("0.01"), of("100"), ofRUB5("0.51410")));
		Variant<Integer> vOpMsk = new Variant<>(vSBP, 0x11, 0x01);
		Variant<SecType> vSecTyp = new Variant<>(vOpMsk, SecType.FUT, SecType.BOND);
		Variant<String> vSecTZ = new Variant<>(vSecTyp, "Russian Standard Time", "XXX");
		Variant<Integer> vQuotTyp = new Variant<>(vSecTZ, 1, 12);
		Variant<?> iterator = vQuotTyp;
		int foundCnt = 0;
		SecurityUpdate1 x, found = null;
		do {
			x = new SecurityUpdate1(
					vSecID.get(),
					vAct.get(),
					vSecCod.get(),
					vSecCls.get(),
					vDBC.get(),
					vMktID.get(),
					vSrtNam.get(),
					vSBP.get(),
					vOpMsk.get(),
					vSecTyp.get(),
					vSecTZ.get(),
					vQuotTyp.get()
				);
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(41190, found.getSecID());
		assertEquals(true, found.getActive());
		assertEquals("RIM9", found.getSecCode());
		assertEquals("F", found.getSecClass());
		assertEquals("FUT", found.getDefaultBoardCode());
		assertEquals(4, found.getMarketID());
		assertEquals("RTS-6.19", found.getShortName());
		assertEquals(board_params, found.getDefaultBoardParams());
		assertEquals(0x11, found.getOpMask());
		assertEquals(SecType.FUT, found.getSecType());
		assertEquals("Russian Standard Time", found.getSecTZ());
		assertEquals(1, found.getQuotesType());
	}
	
	@Test
	public void testHashCode() {
		int expected = new HashCodeBuilder(10087251, 7129)
				.append(41190)
				.append(true)
				.append("RIM9")
				.append("F")
				.append("FUT")
				.append(4)
				.append("RTS-6.19")
				.append(board_params)
				.append(0x11)
				.append(SecType.FUT)
				.append("Russian Standard Time")
				.append(1)
				.build();
		
		assertEquals(expected, service.hashCode());
	}

	@Test
	public void testToString() {
		String expected = new StringBuilder()
				.append("Security[")
				.append("secID=41190,")
				.append("active=true,")
				.append("secCode=RIM9,")
				.append("secClass=F,")
				.append("defaultBoardCode=FUT,")
				.append("marketID=4,")
				.append("shortName=RTS-6.19,")
				.append("boardParams=").append(board_params).append(",")
				.append("opmask=17,")
				.append("secType=FUT,")
				.append("secTZ=Russian Standard Time,")
				.append("quotesType=1")
				.append("]")
				.toString();
		
		assertEquals(expected, service.toString());
	}

}
