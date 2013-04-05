package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.data.row.Row;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2012-08-28<br>
 * $Id: GDate2ETest.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class GDate2ETest {
	private static IMocksControl control;
	private static Row row;
	private static G<String> gDate,gTime;
	private static GDate2E getter;
	private static SimpleDateFormat df;
	
	@SuppressWarnings("unchecked")
	@BeforeClass
	static public void setUpBeforeClass() throws Exception {
		control = createStrictControl();
		row = control.createMock(Row.class);
		gDate = control.createMock(G.class);
		gTime = control.createMock(G.class);
		df = new SimpleDateFormat("y-M-d H:m:s");
		getter = new GDate2E(gDate, gTime, "y-M-d", "H:m:s");
	}
	
	@Before
	public void setUp() throws Exception {
		control.resetToStrict();
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertEquals(gDate, getter.getDateGetter());
		assertEquals(gTime, getter.getTimeGetter());
		assertEquals("y-M-d", getter.getDateFormat());
		assertEquals("H:m:s", getter.getTimeFormat());
	}
	
	@Test
	public void testGet() throws Exception {
		Object fixture[][] = {
				// date val, time val, expected val
				{ "2012-02-10", "22:15:35", "2012-02-10 22:15:35" },
				{ "2012-02-10", null,       null },
				{ null,         "22:15:35", null },
				{ null,         null,       null },
				{ "bad",		null,       null },
				{ null,         "bad",		null },
		};
		for ( int i = 0; i < fixture.length; i ++ ) {
			String msg = "At #" + i;
			Date expected = (fixture[i][2] == null ?
					null : df.parse((String) fixture[i][2]));
			control.resetToStrict();
			expect(gDate.get(row)).andReturn((String) fixture[i][0]);
			expect(gTime.get(row)).andReturn((String) fixture[i][1]);
			control.replay();
			
			assertEquals(msg, expected, getter.get(row));
			
			control.verify();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testEquals() throws Exception {
		Variant<G<String>> vDate = new Variant<G<String>>()
			.add(gDate)
			.add(null)
			.add(control.createMock(G.class));
		Variant<G<String>> vTime = new Variant<G<String>>(vDate)
			.add(gTime)
			.add(null)
			.add(control.createMock(G.class));
		Variant<String> vDateFormat = new Variant<String>(vTime)
			.add("y-M-d")
			.add("y");
		Variant<String> vTimeFormat = new Variant<String>(vDateFormat)
			.add("H:m:s")
			.add("H");
		int foundCnt = 0;
		GDate2E found = null;
		do {
			GDate2E actual = new GDate2E(vDate.get(), vTime.get(),
					vDateFormat.get(), vTimeFormat.get());
			if ( getter.equals(actual) ) {
				foundCnt ++;
				found = actual;
			}
		} while ( vTimeFormat.next() );
		assertEquals(1, foundCnt);
		assertEquals(gDate, found.getDateGetter());
		assertEquals(gTime, found.getTimeGetter());
		assertEquals("y-M-d", found.getDateFormat());
		assertEquals("H:m:s", found.getTimeFormat());
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertFalse(getter.equals(null));
		assertFalse(getter.equals(this));
		assertTrue(getter.equals(getter));
	}
	
	@Test
	public void testHashCode() throws Exception {
		int hashCode = new HashCodeBuilder(20121103, /*0*/73343)
			.append(gDate)
			.append(gTime)
			.append("y-M-d")
			.append("H:m:s")
			.toHashCode();
		assertEquals(hashCode, getter.hashCode());
	}

}
