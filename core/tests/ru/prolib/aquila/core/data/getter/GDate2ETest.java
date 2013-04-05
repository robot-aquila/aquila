package ru.prolib.aquila.core.data.getter;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.data.getter.GDate2E;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.core.utils.Variant;

/**
 * 2013-02-14<br>
 * $Id$
 */
public class GDate2ETest {
	private IMocksControl control;
	private FirePanicEvent firePanic;
	private G<String> gDate, gTime;
	private GDate2E getter_strict, getter_nice;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		firePanic = control.createMock(FirePanicEvent.class);
		gDate = control.createMock(G.class);
		gTime = control.createMock(G.class);
		getter_strict = new GDate2E(firePanic, true, gDate, gTime,
				"y-M-d", "H:m:s", "STRICT: ");
		getter_nice = new GDate2E(firePanic, false, gDate, gTime,
				"d.M.y", "H:m:s.S", "NICE: ");
	}
	
	@Test
	public void testConstruct() throws Exception {
		assertSame(firePanic, getter_strict.getFirePanicEvent());
		assertTrue(getter_strict.isStrict());
		assertSame(gDate, getter_strict.getDateGetter());
		assertSame(gTime, getter_strict.getTimeGetter());
		assertEquals("y-M-d H:m:s", getter_strict.getFormatString());
		assertEquals("STRICT: ", getter_strict.getMessagePrefix());
		
		assertSame(firePanic, getter_strict.getFirePanicEvent());
		assertFalse(getter_nice.isStrict());
		assertSame(gDate, getter_nice.getDateGetter());
		assertSame(gTime, getter_nice.getTimeGetter());
		assertEquals("d.M.y H:m:s.S", getter_nice.getFormatString());
		assertEquals("NICE: ", getter_nice.getMessagePrefix());
	}
	
	@Test
	public void testGet_ForStrict() throws Exception {
		String msg1, msg2;
		msg1 = "STRICT: Incorrect date & time combination: {}='{}', {}='{}'";
		msg2 = "STRICT: Date format '{}' mismatch for '{}'";
		SimpleDateFormat f = new SimpleDateFormat("y-M-d H:m:s");
		Object fix[][] = {
			// date str, time str, msg?, msg args? result
			{ "2013-01-01", "23:59:00", null, null,
				f.parse("2013-01-01 23:59:00") },
				
			{ "2013-01-01", "", msg1,
				new Object[] { gDate, "2013-01-01", gTime, "" }, null },
				
			{ "2013-01-01", null, msg1,
				new Object[] { gDate, "2013-01-01", gTime, null }, null },
				
			{ "", "23:59:00", msg1,
				new Object[] { gDate, "", gTime, "23:59:00" }, null },
				
			{ null, "23:59:00", msg1,
				new Object[] { gDate, null, gTime, "23:59:00" }, null },
				
			{ null, null, msg1,
				new Object[] { gDate, null, gTime, null }, null },
				
			{ "", "", msg1, new Object[] { gDate, "", gTime, "" }, null },
			
			{ "foo", "23:59:00", msg2,
				new Object[] { "y-M-d H:m:s", "foo 23:59:00" }, null },
				
			{ "2013-01-01", "bar", msg2,
				new Object[] { "y-M-d H:m:s", "2013-01-01 bar" }, null },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			control.resetToStrict();
			expect(gDate.get(this)).andReturn((String) fix[i][0]);
			expect(gTime.get(this)).andReturn((String) fix[i][1]);
			String msg = (String) fix[i][2]; 
			if ( msg != null ) {
				Object[] args = (Object[]) fix[i][3];
				firePanic.firePanicEvent(eq(1), eq(msg), aryEq(args));
			}
			control.replay();
			Date expected = (Date) fix[i][4]; 
			Date actual = getter_strict.get(this);
			assertEquals("At #" + i, expected, actual);
			control.verify();
		}
	}

	@Test
	public void testGet_ForNice() throws Exception {
		String msg1, msg2;
		msg1 = "NICE: Incorrect date & time combination: {}='{}', {}='{}'";
		msg2 = "NICE: Date format '{}' mismatch for '{}'";
		SimpleDateFormat f = new SimpleDateFormat("d.M.y H:m:s.S");
		Object fix[][] = {
			// date str, time str, msg?, msg args? result
			{ "02.12.2013", "23:59:00.100", null, null,
				f.parse("02.12.2013 23:59:00.100") },
				
			{ "02.12.2013", "", msg1,
				new Object[] { gDate, "02.12.2013", gTime, "" }, null },
				
			{ "02.12.2013", null, msg1,
				new Object[] { gDate, "02.12.2013", gTime, null}, null },
				
			{ "", "23:59:00.100", msg1,
				new Object[] { gDate, "", gTime, "23:59:00.100" }, null },
				
			{ null, "23:59:00.100", msg1,
				new Object[] { gDate, null, gTime, "23:59:00.100" }, null },
				
			{ null, null, null, null, null },
			
			{ "", "", null, null, null },
			
			{ "foo", "23:59:00.100", msg2,
				new Object[] { "d.M.y H:m:s.S", "foo 23:59:00.100" }, null },
				
			{ "02.12.2013", "bar", msg2,
				new Object[] { "d.M.y H:m:s.S", "02.12.2013 bar" }, null },
		};
		for ( int i = 0; i < fix.length; i ++ ) {
			control.resetToStrict();
			expect(gDate.get(this)).andReturn((String) fix[i][0]);
			expect(gTime.get(this)).andReturn((String) fix[i][1]);
			String msg = (String) fix[i][2]; 
			if ( msg != null ) {
				Object[] args = (Object[]) fix[i][3];
				firePanic.firePanicEvent(eq(1), eq(msg), aryEq(args));
			}
			control.replay();
			Date expected = (Date) fix[i][4]; 
			Date actual = getter_nice.get(this);
			assertEquals("At #" + i, expected, actual);
			control.verify();
		}
	}
	
	@Test
	public void testEquals_SpecialCases() throws Exception {
		assertTrue(getter_strict.equals(getter_strict));
		assertFalse(getter_strict.equals(this));
		assertFalse(getter_strict.equals(null));
	}
	
	@Test
	public void testEquals() throws Exception {
		Variant<FirePanicEvent> vFire = new Variant<FirePanicEvent>()
			.add(firePanic)
			.add(control.createMock(FirePanicEvent.class));
		Variant<Boolean> vStrict = new Variant<Boolean>(vFire)
			.add(true)
			.add(false);
		Variant<G<String>> vDateGtr = new Variant<G<String>>(vStrict)
			.add(gDate)
			.add(gTime);
		Variant<G<String>> vTimeGtr = new Variant<G<String>>(vDateGtr)
			.add(gTime)
			.add(gDate);
		Variant<String> vDateFmt = new Variant<String>(vTimeGtr)
			.add("d.M.y")
			.add("y-M-d");
		Variant<String> vTimeFmt = new Variant<String>(vDateFmt)
			.add("H.m.s.S")
			.add("H:m:s");
		Variant<String> vPfx = new Variant<String>(vTimeFmt)
			.add("NICE: ")
			.add("STRICT: ");
		Variant<?> iterator = vPfx;
		int foundCnt = 0;
		GDate2E x = null, found = null;
		do {
			x = new GDate2E(vFire.get(), vStrict.get(), vDateGtr.get(),
					vTimeGtr.get(), vDateFmt.get(), vTimeFmt.get(),
					vPfx.get());
			if ( getter_strict.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertSame(firePanic, found.getFirePanicEvent());
		assertTrue(found.isStrict());
		assertSame(gDate, found.getDateGetter());
		assertSame(gTime, found.getTimeGetter());
		assertEquals("y-M-d H:m:s", found.getFormatString());
		assertEquals("STRICT: ", found.getMessagePrefix());
	}

	@Test
	public void testToString() throws Exception {
		String expected1 = "GDate2E[date=" + gDate
			+ ", time=" + gTime + ", strict=true"
			+ ", fmt='y-M-d H:m:s', msgPfx='STRICT: ']";
		assertEquals(expected1, getter_strict.toString());
		
		String expected2 = "GDate2E[date=" + gDate
			+ ", time=" + gTime + ", strict=false"
			+ ", fmt='d.M.y H:m:s.S', msgPfx='NICE: ']";
		assertEquals(expected2, getter_nice.toString());
	}

}
