package ru.prolib.aquila.core.BusinessEntities;

import static org.junit.Assert.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.*;

import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-09-05<br>
 * $Id$
 */
public class AccountTest {
	private Account account;

	@Test
	public void testConstruct1() throws Exception {
		account = new Account("L01-XXX");
		assertEquals("L01-XXX", account.getCode());
		assertNull(account.getSubCode());
		assertNull(account.getSubCode2());
	}
	
	@Test
	public void testConstruct2() throws Exception {
		account = new Account("L02-YYY", "C500");
		assertEquals("L02-YYY", account.getCode());
		assertEquals("C500", account.getSubCode());
		assertNull(account.getSubCode2());
	}
	
	@Test
	public void testConstruct3() throws Exception {
		account = new Account("L003-ZZZ", "D200", "ZX5");
		assertEquals("L003-ZZZ", account.getCode());
		assertEquals("D200", account.getSubCode());
		assertEquals("ZX5", account.getSubCode2());
	}
	
	@Test
	public void testToString() throws Exception {
		String fix[][] = {
				// code, subCode, subCode2, expected
				{ "L01", "C500", "ZX5", "L01#C500@ZX5" },
				{ null,  "C500", "ZX5", "null#C500@ZX5" },
				{ null,  null, "ZX5", "null#null@ZX5" },
				{ "L01", "C500", null, "L01#C500" },
				{ "L01", null, null, "L01" },
				{ null,  null, null, "null" },
				{ "L01", null, "ZX5", "L01#null@ZX5" },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			String msg = "At #" + i;
			account = new Account(fix[i][0], fix[i][1], fix[i][2]);
			assertEquals(msg, fix[i][3], account.toString());
		}
	}
	
	@Test
	public void testDefaultAccount() throws Exception {
		assertEquals("DEFAULT", Account.DEFAULT.getCode());
		assertNull(Account.DEFAULT.getSubCode());
		assertNull(Account.DEFAULT.getSubCode2());
	}
	
	private void helpTestEquality(Account expected) {
		Variant<String> vCode = new Variant<String>()
			.add("L01-XXX")
			.add(null);
		Variant<String> vSubCode = new Variant<String>(vCode)
			.add("C500")
			.add(null);
		Variant<String> vSubCode2 = new Variant<String>(vSubCode)
			.add("ZX5")
			.add(null);
		Variant<?> iterator = vSubCode2;
		int foundCnt = 0;
		Account x = null, found = null;
		do {
			x = new Account(vCode.get(), vSubCode.get(), vSubCode2.get());
			if ( account.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(expected.getCode(), found.getCode());
		assertEquals(expected.getSubCode(), found.getSubCode());
		assertEquals(expected.getSubCode2(), found.getSubCode2());
	}
	
	@Test
	public void testEquals() throws Exception {
		account = new Account("L01-XXX", null, null);
		helpTestEquality(new Account("L01-XXX"));
		
		account = new Account("L01-XXX", "C500", null);
		helpTestEquality(new Account("L01-XXX", "C500"));
		
		account = new Account("L01-XXX", "C500", "ZX5");
		helpTestEquality(new Account("L01-XXX", "C500", "ZX5"));
	}
	
	@Test
	public void testHashCode() throws Exception {
		account = new Account("L01-XXX");
		assertEquals(new HashCodeBuilder()
			.append("L01-XXX")
			.append((Object) null)
			.append((Object) null)
			.toHashCode(), account.hashCode());
		
		account = new Account("L01-XXX", "C500");
		assertEquals(new HashCodeBuilder()
			.append("L01-XXX")
			.append("C500")
			.append((Object) null)
			.toHashCode(), account.hashCode());
		
		account = new Account("L01-XXX", "C500", "ZX5");
		assertEquals(new HashCodeBuilder()
			.append("L01-XXX")
			.append("C500")
			.append("ZX5")
			.toHashCode(), account.hashCode());
	}

}
