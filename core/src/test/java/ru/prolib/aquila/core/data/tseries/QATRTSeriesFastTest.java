package ru.prolib.aquila.core.data.tseries;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.of;
import static org.easymock.EasyMock.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleBuilder;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ValueOutOfRangeException;
import ru.prolib.aquila.core.data.ZTFrame;

@SuppressWarnings("unchecked")
public class QATRTSeriesFastTest {
	
	static final String _FIX_QATR5_RAW[][] = {
			// RIU5, 2015-08-06, h1
			// open, high, low, close, expected ATR
			{"82960","82960","82960","82960", null},
			{"82840","83700","82840","83380", null},
			{"83390","83490","83110","83310", null},
			{"83280","83310","83140","83220", null},
			{"83200","83200","82610","82660", "404.000000"},
			{"82640","83090","82480","82880", "445.200000"},
			{"82890","83140","82830","83040", "418.160000"},
			{"82970","82990","82550","82720", "432.528000"},
			{"82730","82950","82660","82790", "404.022400"},
			{"82760","82860","82610","82700", "373.217920"},
			{"82690","82920","82620","82770", "358.574336"},
			{"82780","82850","82640","82670", "328.859469"},
			{"82660","82740","82530","82700", "305.087575"},
			{"82680","82830","82590","82710", "292.070060"},
			{"82720","82780","82610","82700", "267.656048"},
			{"82690","82760","82080","82190", "350.124838"},
			{"82190","82250","81680","81860", "394.099870"},
			{"81830","81970","81630","81770", "383.279896"},
			{"81790","82120","81760","82090", "378.623917"},
			{"82060","82260","81830","82050", "388.899134"},
			{"82020","82190","81770","81810", "395.119307"},
		};
	static final List<FR> FIX_QATR5 = toRecords(_FIX_QATR5_RAW);
	
	public static class FR {
		private final Candle candle;
		private final CDecimal expected;
		
		public FR(Candle candle, CDecimal expected) {
			this.candle = candle;
			this.expected = expected;
		}
		
		public Candle getCandle() {
			return candle;
		}
		
		public Instant getTime() {
			return candle.getStartTime();
		}
		
		public CDecimal getExpected() {
			return expected;
		}
		
	}
	
	static List<FR> toRecords(String[][] fixture) {
		List<FR> result = new ArrayList<>();
		Instant time = T("2018-06-01T10:00:00Z");
		for ( int i = 0; i < fixture.length; i ++ ) {
			Candle candle = new CandleBuilder()
				.withTime(time)
				.withTimeFrame(ZTFrame.M5)
				.withOpenPrice(of(fixture[i][0]))
				.withHighPrice(of(fixture[i][1]))
				.withLowPrice(of(fixture[i][2]))
				.withClosePrice(of(fixture[i][3]))
				.withVolume(of(1L))
				.buildCandle();
			CDecimal expected = fixture[i][4] == null ? null : CDecimalBD.of(fixture[i][4]);
			result.add(new FR(candle, expected));

			time = time.plusSeconds(300);
		}
		return result;
	}
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() {
		
	}

	private IMocksControl control;
	private TSeries<Candle> sourceMock;
	private TSeriesImpl<Candle> sourceStub;
	private ArrayList<CDecimal> cacheStub;
	private QATRTSeriesFast service, serviceWithMocks;
	
	private void fillQATR5() {
		for ( int i = 0; i < FIX_QATR5.size(); i ++ ) {
			FR fr = FIX_QATR5.get(i);
			sourceStub.set(fr.getTime(), fr.getCandle());
		}
	}
	
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		sourceMock = control.createMock(TSeries.class);
		sourceStub = new TSeriesImpl<>("foo", ZTFrame.M5);
		cacheStub = new ArrayList<>();
		service = new QATRTSeriesFast("zulu", sourceStub, 5, 6, cacheStub);
		serviceWithMocks = new QATRTSeriesFast("zeta", sourceMock, 10, 4);
	}
	
	@Test
	public void testToIndex() {
		expect(sourceMock.toIndex(T("1956-05-20T15:35:00Z"))).andReturn(18);
		control.replay();
		
		assertEquals(18, serviceWithMocks.toIndex(T("1956-05-20T15:35:00Z")));
		
		control.verify();
	}
	
	@Test
	public void testGetId_Ctor4() {
		assertEquals("zeta", serviceWithMocks.getId());
	}

	@Test
	public void testGetLength() {
		expect(sourceMock.getLength()).andReturn(242);
		control.replay();
		
		assertEquals(242, serviceWithMocks.getLength());
		
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
		expect(sourceMock.getTimeFrame()).andReturn(ZTFrame.M1);
		control.replay();
		
		assertEquals(ZTFrame.M1, serviceWithMocks.getTimeFrame());
		
		control.verify();
	}
	
	@Test
	public void testGet_1I() throws Exception {
		fillQATR5();
		
		for ( int i = 0; i < FIX_QATR5.size(); i ++ ) {
			FR fr = FIX_QATR5.get(i);
			String msg = "At#" + i;
			assertEquals(msg, fr.getExpected(), service.get(i));
		}
	}
	
	@Test
	public void testGet_1I_NegativeIndex() throws Exception {
		fillQATR5();
		
		for ( int i = 0; i < FIX_QATR5.size() - 1; i ++ ) {
			FR fr = FIX_QATR5.get(i);
			int irev = -(FIX_QATR5.size() - 1 - i);
			String msg = "At#" + irev;
			assertEquals(msg, fr.getExpected(), service.get(irev));
		}
	}
	
	@Test (expected=ValueOutOfRangeException.class)
	public void testGet_1I_ThrowsOutOfRange_PositiveIndex() throws Exception {
		fillQATR5();
		
		service.get(FIX_QATR5.size());
	}
	
	@Test (expected=ValueOutOfRangeException.class)
	public void testGet_1I_ThrowsOutOfRange_NegativeIndex() throws Exception {
		fillQATR5();
		
		service.get(-FIX_QATR5.size());
	}
	
	@Test
	public void testGet_0() throws Exception {
		for ( int i = 0; i < FIX_QATR5.size(); i ++ ) {
			FR fr = FIX_QATR5.get(i);
			sourceStub.set(fr.getTime(), fr.getCandle());
			String msg = "At#" + i;
			assertEquals(msg, fr.getExpected(), service.get());
		}
	}
	
	@Test
	public void testGet_1T() throws Exception {
		fillQATR5();
		
		FR fr = FIX_QATR5.get(10);
		assertEquals(fr.getExpected(), service.get(fr.getTime()));
		fr = FIX_QATR5.get(0);
		assertNull(service.get(fr.getTime().minusSeconds(1)));
	}
	
	@Ignore
	@Test
	public void testSpecialCase_NullsInTheMiddle() throws Exception {
		fail("Not yet implemented");
	}
	
	@Test
	public void testSpecialCase_Invalidate1() throws Exception {
		fillQATR5();
		
		assertEquals(0, cacheStub.size());
		
		assertEquals(CDecimalBD.of("358.574336"), service.get(10));
		assertEquals(10, service.getLastValidIndex());
		assertEquals(11, cacheStub.size());
		
		service.invalidate(5);
		assertEquals(4, service.getLastValidIndex());
		assertEquals(CDecimalBD.of("404.000000"), service.get(4));
		for ( int i = 5; i < 11; i ++ ) {
			cacheStub.set(i, CDecimalBD.of((long)(Math.random() * 10000)));
		}
		
		for ( int i = 0; i < FIX_QATR5.size(); i ++ ) {
			FR fr = FIX_QATR5.get(i);
			String msg = "At#" + i;
			assertEquals(msg, fr.getExpected(), service.get(i));
		}
		
		service.invalidate(15);
		assertEquals(14, service.getLastValidIndex());
		for ( int i = 15; i < FIX_QATR5.size(); i ++ ) {
			cacheStub.set(i, CDecimalBD.of((long)(Math.random() * 10000)));
		}
		
		for ( int i = 0; i < FIX_QATR5.size(); i ++ ) {
			FR fr = FIX_QATR5.get(i);
			String msg = "At#" + i;
			assertEquals(msg, fr.getExpected(), service.get(i));
		}

		service.invalidate(0);
		assertEquals(-1, service.getLastValidIndex());
		for ( int i = 0; i < FIX_QATR5.size(); i ++ ) {
			cacheStub.set(i, CDecimalBD.of((long)(Math.random() * 10000)));
		}

		for ( int i = 0; i < FIX_QATR5.size(); i ++ ) {
			FR fr = FIX_QATR5.get(i);
			String msg = "At#" + i;
			assertEquals(msg, fr.getExpected(), service.get(i));
		}
	}
	
	@Test
	public void testShrink_ClearAllIfNoValidData() throws Exception {
		fillQATR5();
		service.get(); // force recalculate
		service.invalidate(0);
		
		service.shrink();
		
		assertEquals(-1, service.getLastValidIndex());
		assertEquals(new ArrayList<>(), cacheStub);
	}
	
	@Test
	public void testShrink_SkipIfNothingToDo() throws Exception {
		fillQATR5();
		service.get(); // force recalculate
		service.invalidate(10);

		service.shrink();
		
		assertEquals(9, service.getLastValidIndex());
		List<CDecimal> expected = new ArrayList<>();
		for ( int i = 0; i < 10; i ++ ) {
			expected.add(FIX_QATR5.get(i).getExpected());
		}
		assertEquals(expected, cacheStub);
	}
	
	@Test
	public void testShrink_PartiallyShrunkCase1() throws Exception {
		for ( int i = 0; i < 10; i ++ ) {
			FR fr = FIX_QATR5.get(i);
			sourceStub.set(fr.getTime(), fr.getCandle());
		}
		service.get(); // force recalculate
		service.invalidate(5);
		
		service.shrink();

		assertEquals(4, service.getLastValidIndex());
		List<CDecimal> expected = new ArrayList<>();
		for ( int i = 0; i < 5; i ++ ) {
			expected.add(FIX_QATR5.get(i).getExpected());
		}
		assertEquals(expected, cacheStub);
	}
	
	@Test
	public void testShrink_PartiallyShrunkCase2() throws Exception {
		for ( int i = 0; i < 10; i ++ ) {
			FR fr = FIX_QATR5.get(i);
			sourceStub.set(fr.getTime(), fr.getCandle());
		}
		service.get(); // force recalculate
		service.invalidate(1);
		
		service.shrink();

		assertEquals(0, service.getLastValidIndex());
		List<CDecimal> expected = new ArrayList<>();
		for ( int i = 0; i < 1; i ++ ) {
			expected.add(FIX_QATR5.get(i).getExpected());
		}
		assertEquals(expected, cacheStub);
	}

}
