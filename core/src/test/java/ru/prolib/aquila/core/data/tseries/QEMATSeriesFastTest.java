package ru.prolib.aquila.core.data.tseries;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ValueOutOfRangeException;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.TSeries;

public class QEMATSeriesFastTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	public static class FR {
		private final Instant time;
		private final CDecimal value;
		private final CDecimal expected;
		
		public FR(String timeString, String value, String expected) {
			this.time = T(timeString);
			if ( value == null ) {
				this.value = null;
			} else {
				this.value = CDecimalBD.of(value);
			}
			if ( expected == null ) {
				this.expected = null;
			} else {
				this.expected = CDecimalBD.of(expected);
			}
		}
	}
	
	private static final FR
		fix_qema5[] = {
			// RIU5, 2015-07-31, m15, close
			new FR("2015-07-31T09:15:00Z", "85990", null), //09:15
			new FR("2015-07-31T10:00:00Z", "85190", null), //10:00
			new FR("2015-07-31T10:15:00Z", "85290", null), //10:15
			new FR("2015-07-31T10:30:00Z", "84980", null), //10:30
			new FR("2015-07-31T10:45:00Z", "85260", "85339.506173"), //10:45
			new FR("2015-07-31T11:00:00Z", "85120", "85266.337449"),
			new FR("2015-07-31T11:15:00Z", "84730", "85087.558299"),
			new FR("2015-07-31T11:30:00Z", "84890", "85021.705533"),
			new FR("2015-07-31T11:45:00Z", "84900", "84981.137022"),
			new FR("2015-07-31T12:00:00Z", "85120", "85027.424681"),
			new FR("2015-07-31T12:15:00Z", "85150", "85068.283121"),
			new FR("2015-07-31T12:30:00Z", "84950", "85028.855414"),
			new FR("2015-07-31T12:45:00Z", "85010", "85022.570276"),
			new FR("2015-07-31T13:00:00Z", "85150", "85065.046851"),//13:00
			new FR("2015-07-31T13:15:00Z", "85150", "85093.364567"),//13:15
			new FR("2015-07-31T13:30:00Z", "84940", "85042.243045"),
			new FR("2015-07-31T13:45:00Z", "84900", "84994.828697"),
			new FR("2015-07-31T14:00:00Z", "84440", "84809.885798"),
			new FR("2015-07-31T14:15:00Z", "84200", "84606.590532"),
			new FR("2015-07-31T14:30:00Z", "84230", "84481.060355"),
			new FR("2015-07-31T14:45:00Z", "84180", "84380.706903"),
			new FR("2015-07-31T15:00:00Z", "84430", "84397.137935"),
			new FR("2015-07-31T15:15:00Z", "84230", "84341.425290"),
			new FR("2015-07-31T15:30:00Z", "84850", "84510.950193"),
			new FR("2015-07-31T15:45:00Z", "84740", "84587.300129"),
			new FR("2015-07-31T16:00:00Z", "85230", "84801.533419"),
			new FR("2015-07-31T16:15:00Z", "85770", "85124.355613"),
			new FR("2015-07-31T16:30:00Z", "85480", "85242.903742"),
			new FR("2015-07-31T16:45:00Z", "86130", "85538.602495"),
			new FR("2015-07-31T17:00:00Z", "85720", "85599.068330"),
			new FR("2015-07-31T17:15:00Z", "85840", "85679.378887"),
			new FR("2015-07-31T17:30:00Z", "85830", "85729.585925"),
			new FR("2015-07-31T17:45:00Z", "85690", "85716.390617"),//17:45
			new FR("2015-07-31T18:00:00Z", "85670", "85700.927078"),//18:00
			new FR("2015-07-31T18:15:00Z", "85850", "85750.618052"),//18:15
		},
		fix_qema3[] = {
			//  value, expected MA
			new FR("2015-01-01T00:00:00Z", "40.27",           null),
			new FR("2015-01-01T00:15:00Z", null,          	  null),
			new FR("2015-01-01T00:30:00Z", "40.92",	          null), //  40.9200
			new FR("2015-01-01T00:45:00Z", "44.33",           null), // (40.9200 * 2 + 2 * 44.33) / 4 = 42.625
			new FR("2015-01-01T01:00:00Z", "53.50", "48.062500000"), // (42.6250 * 2 + 2 * 53.50) / 4 = 48.0625
			new FR("2015-01-01T01:15:00Z", "52.13", "50.096250000"), // (48.0625 * 2 + 2 * 52.13) / 4 = 50.09625
			new FR("2015-01-01T01:30:00Z", "45.02", "47.558125000"), // (50.09625 * 2 + 2 * 45.02) / 4 = 47.558125
			new FR("2015-01-01T01:45:00Z", null, 			  null),
			new FR("2015-01-01T02:00:00Z", null, 			  null),
			new FR("2015-01-01T02:15:00Z", "48.13", 		  null), //  48.13
			new FR("2015-01-01T02:30:00Z", "51.14", 		  null), // (48.13 * 2 + 2 * 51.14) / 4 = 49.635
			new FR("2015-01-01T02:45:00Z", "52.18", "50.907500000"), // (49.635 * 2 + 2 * 52.18) / 4 = 50.9075
			new FR("2015-01-01T03:00:00Z", "49.75", "50.328750000"), // (50.9075 * 2 + 2 * 49.75) / 4 = 50.32875
		};
	
	
	private IMocksControl control;
	private TSeries<CDecimal> sourceMock;
	private TSeriesImpl<CDecimal> sourceStub;
	private ArrayList<CDecimal> cacheStub;
	private QEMATSeriesFast service, serviceWithMocks;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		cacheStub = new ArrayList<>();
		control = createStrictControl();
		sourceMock = control.createMock(TSeries.class);
		sourceStub = new TSeriesImpl<>("foo", ZTFrame.M15);
		service = new QEMATSeriesFast("foo", sourceStub, 5, 6, cacheStub);
		serviceWithMocks = new QEMATSeriesFast("bar", sourceMock, 5, 6);
	}
	
	@Test
	public void testToIndex() {
		expect(sourceMock.toIndex(T("2018-04-05T11:30:35Z"))).andReturn(8);
		control.replay();
		
		assertEquals(8, serviceWithMocks.toIndex(T("2018-04-05T11:30:35Z")));
		
		control.verify();
	}
	
	@Test
	public void testGetId_Ctor4() {
		assertEquals("bar", serviceWithMocks.getId());
	}
	
	@Test
	public void testGetLength() {
		expect(sourceMock.getLength()).andReturn(100);
		control.replay();
		
		assertEquals(100, serviceWithMocks.getLength());
		
		control.verify();
	}
	
	@Test
	public void testGetLID() {
		LID lid = LID.createInstance();
		expect(sourceMock.getLID()).andReturn(lid);
		control.replay();
		
		assertSame(lid, serviceWithMocks.getLID());
		
		control.verify();
	}
	
	@Test
	public void testLock() {
		sourceMock.lock();
		control.replay();
		
		serviceWithMocks.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		sourceMock.unlock();
		control.replay();
		
		serviceWithMocks.unlock();
		
		control.verify();
	}
	
	@Test
	public void testGetTimeFrame() {
		expect(sourceMock.getTimeFrame()).andReturn(ZTFrame.M5);
		control.replay();
		
		assertEquals(ZTFrame.M5, serviceWithMocks.getTimeFrame());
		
		control.verify();
	}
	
	@Test
	public void testGet_0() throws Exception {
		for ( int i = 0; i < fix_qema5.length; i ++ ) {
			FR fr = fix_qema5[i];
			sourceStub.set(fr.time, fr.value);
			String msg = "At#" + i;
			assertEquals(msg, fr.expected, service.get());
		}
	}
	
	@Test
	public void testGet_1T() throws Exception {
		for ( int i = 0; i < fix_qema5.length; i ++ ) {
			FR fr = fix_qema5[i];
			sourceStub.set(fr.time, fr.value);
		}
		
		for ( int i = 0; i < fix_qema5.length; i ++ ) {
			FR fr = fix_qema5[i];
			String msg = "At#" + fr.time;
			assertEquals(msg, fr.expected, service.get(fr.time));
		}
	}
	
	@Test (expected=ValueOutOfRangeException.class)
	public void testGet_1I_ThrowsOutOfRange_PositiveIndex() throws Exception {
		for ( int i = 0; i < fix_qema5.length; i ++ ) {
			FR fr = fix_qema5[i];
			sourceStub.set(fr.time, fr.value);
		}
		
		service.get(fix_qema5.length);
	}
	
	@Test (expected=ValueOutOfRangeException.class)
	public void testGet_1I_ThrowsOutOfRange_NegativeIndex() throws Exception {
		for ( int i = 0; i < fix_qema5.length; i ++ ) {
			FR fr = fix_qema5[i];
			sourceStub.set(fr.time, fr.value);
		}

		service.get(-fix_qema5.length);
	}
	
	@Test
	public void testGet_1I() throws Exception {
		for ( int i = 0; i < fix_qema5.length; i ++ ) {
			FR fr = fix_qema5[i];
			sourceStub.set(fr.time, fr.value);
		}
		
		for ( int i = 0; i < fix_qema5.length; i ++ ) {
			FR fr = fix_qema5[i];
			String msg = "At#" + i;
			assertEquals(msg, fr.expected, service.get(i));
		}
	}
	
	@Test
	public void testGet_1I_NegativeIndex() throws Exception {
		for ( int i = 0; i < fix_qema5.length; i ++ ) {
			FR fr = fix_qema5[i];
			sourceStub.set(fr.time, fr.value);
		}

		for ( int i = 0; i < fix_qema5.length - 1; i ++ ) {
			FR fr = fix_qema5[i];
			int irev = -(fix_qema5.length - 1 - i);
			String msg = "At#" + irev;
			assertEquals(msg, fr.expected, service.get(irev));
		}
	}

	@Test
	public void testSpecialCase_NullsInTheMiddle() throws Exception {
		service = new QEMATSeriesFast("foo", sourceStub, 3, 9);
		for ( int i = 0; i < fix_qema3.length; i ++ ) {
			FR fr = fix_qema3[i];
			sourceStub.set(fr.time, fr.value);
		}

		for ( int i = 0; i < fix_qema3.length; i ++ ) {
			FR fr = fix_qema3[i];
			String msg = "At#" + i;
			assertEquals(msg, fr.expected, service.get(i));
		}
	}

	@Test
	public void testSpecialCase_Invalidate1() throws Exception {
		for ( int i = 0; i < fix_qema5.length; i ++ ) {
			FR fr = fix_qema5[i];
			sourceStub.set(fr.time, fr.value);
		}
		
		assertEquals(0, cacheStub.size());
		
		assertEquals(CDecimalBD.of("85068.283121"), service.get(10));
		assertEquals(10, service.getLastValidIndex());
		assertEquals(11, cacheStub.size());
		
		service.invalidate(5);
		assertEquals(4, service.getLastValidIndex());
		assertEquals(CDecimalBD.of("85339.506173"), service.get(4));
		for ( int i = 5; i < 11; i ++ ) {
			cacheStub.set(i, CDecimalBD.of((long)(Math.random() * 10000)));
		}
		for ( int i = 0; i < fix_qema5.length; i ++ ) {
			FR fr = fix_qema5[i];
			String msg = "At#" + i;
			assertEquals(msg, fr.expected, service.get(i));
		}
		
		service.invalidate(20);
		assertEquals(19, service.getLastValidIndex());
		for ( int i = 20; i < fix_qema5.length; i ++ ) {
			cacheStub.set(i, CDecimalBD.of((long)(Math.random() * 10000)));
		}
		for ( int i = 0; i < fix_qema5.length; i ++ ) {
			FR fr = fix_qema5[i];
			String msg = "At#" + i;
			assertEquals(msg, fr.expected, service.get(i));
		}
		
		service.invalidate(0);
		assertEquals(-1, service.getLastValidIndex());
		for ( int i = 0; i < fix_qema5.length; i ++ ) {
			cacheStub.set(i, CDecimalBD.of((long)(Math.random() * 10000)));
		}
		for ( int i = 0; i < fix_qema5.length; i ++ ) {
			FR fr = fix_qema5[i];
			String msg = "At#" + i;
			assertEquals(msg, fr.expected, service.get(i));
		}
	}
	
	@Test
	public void testShrink_ClearAllIfNoValidData() throws Exception {
		for ( int i = 0; i < 10; i ++ ) {
			FR fr = fix_qema5[i];
			sourceStub.set(fr.time, fr.value);
		}
		service.get(); // force recalculate
		service.invalidate(0);
		
		service.shrink();
		
		assertEquals(-1, service.getLastValidIndex());
		assertEquals(new ArrayList<>(), cacheStub);
	}
	
	@Test
	public void testShrink_SkipIfNothingToDo() throws Exception {
		for ( int i = 0; i < 10; i ++ ) {
			FR fr = fix_qema5[i];
			sourceStub.set(fr.time, fr.value);
		}
		service.get(); // force recalculate
		service.invalidate(10);

		service.shrink();
		
		assertEquals(9, service.getLastValidIndex());
		List<CDecimal> expected = new ArrayList<>();
		for ( int i = 0; i < 10; i ++ ) {
			expected.add(fix_qema5[i].expected);
		}
		assertEquals(expected, cacheStub);
	}

	@Test
	public void testShrink_PartiallyShrunkCase1() throws Exception {
		for ( int i = 0; i < 10; i ++ ) {
			FR fr = fix_qema5[i];
			sourceStub.set(fr.time, fr.value);
		}
		service.get(); // force recalculate
		service.invalidate(5);
		
		service.shrink();

		assertEquals(4, service.getLastValidIndex());
		List<CDecimal> expected = new ArrayList<>();
		for ( int i = 0; i < 5; i ++ ) {
			expected.add(fix_qema5[i].expected);
		}
		assertEquals(expected, cacheStub);
	}
	
	@Test
	public void testShrink_PartiallyShrunkCase2() throws Exception {
		for ( int i = 0; i < 10; i ++ ) {
			FR fr = fix_qema5[i];
			sourceStub.set(fr.time, fr.value);
		}
		service.get(); // force recalculate
		service.invalidate(1);
		
		service.shrink();

		assertEquals(0, service.getLastValidIndex());
		List<CDecimal> expected = new ArrayList<>();
		for ( int i = 0; i < 1; i ++ ) {
			expected.add(fix_qema5[i].expected);
		}
		assertEquals(expected, cacheStub);
	}
	
	@Test
	public void testToKey() throws Exception {
		expect(sourceMock.toKey(302)).andReturn(T("2018-12-15T12:09:53Z"));
		control.replay();
		
		assertEquals(T("2018-12-15T12:09:53Z"), serviceWithMocks.toKey(302));
		
		control.verify();
	}
	
	@Test
	public void testGetFirstIndexBefore() {
		expect(sourceMock.getFirstIndexBefore(T("1997-02-01T13:45:01Z"))).andReturn(123);
		control.replay();
		
		assertEquals(123, serviceWithMocks.getFirstIndexBefore(T("1997-02-01T13:45:01Z")));
		
		control.verify();
	}
	
	@Test
	public void testGetFirstBefore() {
		for ( int i = 0; i < fix_qema5.length; i ++ ) {
			FR fr = fix_qema5[i];
			sourceStub.set(fr.time, fr.value);
		}
		assertNull(service.getFirstBefore(T("2015-07-01T00:00:00Z")));
		assertNull(service.getFirstBefore(T("2015-07-31T09:15:00Z")));
		assertNull(service.getFirstBefore(T("2015-07-31T10:00:00Z")));
		assertNull(service.getFirstBefore(T("2015-07-31T10:15:00Z")));
		assertNull(service.getFirstBefore(T("2015-07-31T10:30:00Z")));
		assertNull(service.getFirstBefore(T("2015-07-31T10:45:00Z")));
		assertEquals(of("85339.506173"), service.getFirstBefore(T("2015-07-31T11:00:00Z")));
		assertEquals(of("85093.364567"), service.getFirstBefore(T("2015-07-31T13:33:12Z")));
		assertEquals(of("85716.390617"), service.getFirstBefore(T("2015-07-31T18:01:12Z")));
		assertEquals(of("85750.618052"), service.getFirstBefore(T("2015-07-31T23:19:45Z")));
	}

}
