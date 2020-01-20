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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.concurrency.LID;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleBuilder;
import ru.prolib.aquila.core.data.TAMath;
import ru.prolib.aquila.core.data.TSeries;
import ru.prolib.aquila.core.data.TSeriesImpl;
import ru.prolib.aquila.core.data.ValueOutOfRangeException;
import ru.prolib.aquila.core.data.ZTFrame;

@SuppressWarnings("unchecked")
public class QATRTSeriesFastTest {
	
	static final String _FIX_QATR5_RAW_1[][] = {
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
	static final List<FR> FIX_QATR5_1 = toRecords(_FIX_QATR5_RAW_1);
	
	static final String _FIX_QATR10_RAW_2[][] = {
			// RIZ8, 2018-08-21 16:10, m5, atr10
			// open, high, low, close, expected ATR
			{"106410","106430","106290","106310",null}, // 16:10
			{"106300","106370","106280","106370",null},
			{"106380","106470","106360","106430",null}, // 16:20
			{"106430","106490","106380","106460",null},
			{"106460","106680","106460","106660",null},
			{"106660","106840","106640","106760",null},
			{"106760","106930","106760","106900",null}, // 16:40
			{"106890","106950","106630","106770",null},
			{"106760","106850","106700","106720",null},
			{"106730","106740","106630","106740","162.000000"}, // 16:55
			{"106740","106750","106570","106600","163.800000"},
			{"106620","106670","106580","106650","156.420000"},
			{"106650","106770","106590","106740","158.778000"},
			{"106760","106890","106740","106890","157.900200"},
			{"106870","107070","106830","107050","166.110180"},
			{"107050","107100","106940","107030","165.499162"},
			{"107030","107150","106980","107100","165.949246"},
			{"107100","107140","106810","106950","182.354321"},
			{"106950","107010","106790","106860","186.118889"},
			{"106850","106890","106680","106760","188.507000"},
			{"106750","106840","106640","106840","189.656300"},
			{"106840","106950","106830","106930","182.690670"},
			{"106930","107040","106830","106870","185.421603"},
			{"106870","106920","106710","106790","187.879443"},
			{"106790","106920","106780","106850","183.091499"},
			{"106860","106890","106810","106830","172.782349"},
			{"106840","106890","106770","106880","167.504114"},
			{"106870","107000","106870","106920","163.753702"},
			{"106940","107240","106930","107190","179.378332"},
			{"107190","107330","107180","107260","176.440499"},
			{"107280","107470","107230","107400","182.796449"},
			{"107480","107830","107320","107770","215.516804"},
			{"107780","107950","107770","107780","211.965124"},
			{"107780","107830","107620","107630","211.768611"},
			{"107640","107700","107620","107700","198.591750"},
			{"107700","107710","107620","107630","187.732575"},
			{"107630","107710","107630","107690","176.959318"},
			{"107680","107700","107590","107680","170.263386"},
			{"107680","107750","107560","107600","172.237047"},
			{"107600","107600","107530","107550","162.013343"},
			{"107540","107580","107530","107570","150.812008"},
			{"107570","107700","107530","107630","152.730808"},
			{"107630","107810","107610","107780","157.457727"},
			{"107780","107940","107760","107860","159.711954"},
			{"107860","107880","107730","107790","158.740759"},
			{"107800","107830","107740","107790","151.866683"},
			{"107800","107830","107760","107760","143.680015"},
			{"107760","107900","107740","107860","145.312013"},
			{"107860","107900","107790","107850","141.780812"},
			{"107860","107890","107800","107810","136.602731"},
			{"107800","107860","107730","107840","135.942458"},
			{"107860","108180","107850","108120","156.348212"},
			{"108120","108160","108030","108090","153.713391"},
			{"108100","108100","107980","108020","150.342052"},
			{"108020","108080","107980","108050","145.307846"},
			{"108060","108070","107940","107970","143.777062"},
			{"107970","108030","107950","108000","137.399356"},
			{"108010","108060","107930","108030","136.659420"},
			{"108030","108090","108000","108070","131.993478"},
			{"108070","108090","107970","107970","130.794130"},
			{"107990","108000","107900","107900","127.714717"},
			{"107910","107930","107750","107810","132.943246"}, // +- 0.000001
			{"107810","107810","107740","107790","126.648921"},
			{"107780","107930","107780","107850","128.984029"},
			{"107840","107850","107710","107730","130.085626"},
			{"107730","107770","107700","107730","124.077063"},
			{"107740","107750","107670","107670","119.669357"},
			{"107670","107730","107620","107650","118.702421"},
			{"107670","107720","107640","107650","114.832179"},
			{"107640","107790","107640","107700","118.348961"},
			{"107690","107690","107620","107670","114.514065"},
			{"107670","107730","107640","107700","112.062659"},
			{"107690","107790","107670","107700","112.856393"},
			{"107690","107740","107650","107690","110.570754"}, // +- 0.000001
			{"107690","107740","107690","107730","104.513678"},
			{"107720","107720","107600","107690","107.062310"},
			{"107670","107720","107610","107680","107.356079"},
			{"107680","107780","107660","107760","108.620472"}, // +- 0.000001
			{"107770","107860","107720","107720","111.758424"},
			{"107710","107720","107640","107650","108.582582"},
			{"107660","107670","107630","107660","101.724324"},
			{"107660","107660","107610","107620", "96.551891"},
			{"107640","107650","107610","107650", "90.896702"},
			{"107650","107660","107620","107630", "85.807032"},
			{"107650","107660","107620","107650", "81.226329"},
			{"107650","107710","107610","107650", "83.103696"},
			{"107640","107730","107640","107700", "83.793326"},
			{"107710","107800","107670","107800", "88.413994"},
			{"107780","107910","107750","107870", "95.572594"},
			{"107800","107910","107180","107450","159.015335"},
			{"107440","107520","107240","107290","171.113802"}, // +- 0.000001
			{"107290","107490","107280","107490","175.002421"},
	};
	static final List<FR> FIX_QATR10_2 = toRecords(_FIX_QATR10_RAW_2, "2018-08-21T13:10:00Z", ZTFrame.M5MSK);
	
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
	
	static List<FR> toRecords(String[][] fixture, String startTime, ZTFrame tframe) {
		List<FR> result = new ArrayList<>();
		Instant currTime = T(startTime);
		for ( int i = 0; i < fixture.length; i ++ ) {
			Candle candle = new CandleBuilder()
				.withTime(currTime)
				.withTimeFrame(tframe)
				.withOpenPrice(of(fixture[i][0]))
				.withHighPrice(of(fixture[i][1]))
				.withLowPrice(of(fixture[i][2]))
				.withClosePrice(of(fixture[i][3]))
				.withVolume(of(1L))
				.buildCandle();
			CDecimal expected = fixture[i][4] == null ? null : CDecimalBD.of(fixture[i][4]);
			result.add(new FR(candle, expected));
			
			currTime = candle.getEndTime();
		}
		return result;
	}
	
	static List<FR> toRecords(String[][] fixture) {
		return toRecords(fixture, "2018-06-01T10:00:00Z", ZTFrame.M5);
	}
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() {
		
	}

	@Rule public ExpectedException eex = ExpectedException.none();
	private IMocksControl control;
	private TSeries<Candle> sourceMock;
	private TSeriesImpl<Candle> sourceStub;
	private ArrayList<CDecimal> cacheStub;
	private QATRTSeriesFast service, serviceWithMocks;
	
	private void fillSeries(List<FR> fixture) {
		for ( int i = 0; i < fixture.size(); i ++ ) {
			FR fr = fixture.get(i);
			sourceStub.set(fr.getTime(), fr.getCandle());
		}
	}
	
	private void fillQATR5_1() {
		fillSeries(FIX_QATR5_1);
	}
	
	private void fillQATR10_2() {
		fillSeries(FIX_QATR10_2);
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
		fillQATR5_1();
		
		for ( int i = 0; i < FIX_QATR5_1.size(); i ++ ) {
			FR fr = FIX_QATR5_1.get(i);
			String msg = "At#" + i;
			assertEquals(msg, fr.getExpected(), service.get(i));
		}
	}
	
	@Test
	public void testGet_1I_NegativeIndex() throws Exception {
		fillQATR5_1();
		
		for ( int i = 0; i < FIX_QATR5_1.size() - 1; i ++ ) {
			FR fr = FIX_QATR5_1.get(i);
			int irev = -(FIX_QATR5_1.size() - 1 - i);
			String msg = "At#" + irev;
			assertEquals(msg, fr.getExpected(), service.get(irev));
		}
	}
	
	@Test (expected=ValueOutOfRangeException.class)
	public void testGet_1I_ThrowsOutOfRange_PositiveIndex() throws Exception {
		fillQATR5_1();
		
		service.get(FIX_QATR5_1.size());
	}
	
	@Test (expected=ValueOutOfRangeException.class)
	public void testGet_1I_ThrowsOutOfRange_NegativeIndex() throws Exception {
		fillQATR5_1();
		
		service.get(-FIX_QATR5_1.size());
	}
	
	@Test
	public void testGet_0() throws Exception {
		for ( int i = 0; i < FIX_QATR5_1.size(); i ++ ) {
			FR fr = FIX_QATR5_1.get(i);
			sourceStub.set(fr.getTime(), fr.getCandle());
			String msg = "At#" + i;
			assertEquals(msg, fr.getExpected(), service.get());
		}
	}
	
	@Test
	public void testGet_1T() throws Exception {
		fillQATR5_1();
		
		FR fr = FIX_QATR5_1.get(10);
		assertEquals(fr.getExpected(), service.get(fr.getTime()));
		fr = FIX_QATR5_1.get(0);
		assertNull(service.get(fr.getTime().minusSeconds(1)));
	}
	
	@Ignore
	@Test
	public void testSpecialCase_NullsInTheMiddle() throws Exception {
		fail("Not yet implemented");
	}
	
	@Test
	public void testSpecialCase_Invalidate1() throws Exception {
		fillQATR5_1();
		
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
		
		for ( int i = 0; i < FIX_QATR5_1.size(); i ++ ) {
			FR fr = FIX_QATR5_1.get(i);
			String msg = "At#" + i;
			assertEquals(msg, fr.getExpected(), service.get(i));
		}
		
		service.invalidate(15);
		assertEquals(14, service.getLastValidIndex());
		for ( int i = 15; i < FIX_QATR5_1.size(); i ++ ) {
			cacheStub.set(i, CDecimalBD.of((long)(Math.random() * 10000)));
		}
		
		for ( int i = 0; i < FIX_QATR5_1.size(); i ++ ) {
			FR fr = FIX_QATR5_1.get(i);
			String msg = "At#" + i;
			assertEquals(msg, fr.getExpected(), service.get(i));
		}

		service.invalidate(0);
		assertEquals(-1, service.getLastValidIndex());
		for ( int i = 0; i < FIX_QATR5_1.size(); i ++ ) {
			cacheStub.set(i, CDecimalBD.of((long)(Math.random() * 10000)));
		}

		for ( int i = 0; i < FIX_QATR5_1.size(); i ++ ) {
			FR fr = FIX_QATR5_1.get(i);
			String msg = "At#" + i;
			assertEquals(msg, fr.getExpected(), service.get(i));
		}
	}
	
	@Test
	public void testShrink_ClearAllIfNoValidData() throws Exception {
		fillQATR5_1();
		service.get(); // force recalculate
		service.invalidate(0);
		
		service.shrink();
		
		assertEquals(-1, service.getLastValidIndex());
		assertEquals(new ArrayList<>(), cacheStub);
	}
	
	@Test
	public void testShrink_SkipIfNothingToDo() throws Exception {
		fillQATR5_1();
		service.get(); // force recalculate
		service.invalidate(10);

		service.shrink();
		
		assertEquals(9, service.getLastValidIndex());
		List<CDecimal> expected = new ArrayList<>();
		for ( int i = 0; i < 10; i ++ ) {
			expected.add(FIX_QATR5_1.get(i).getExpected());
		}
		assertEquals(expected, cacheStub);
	}
	
	@Test
	public void testShrink_PartiallyShrunkCase1() throws Exception {
		for ( int i = 0; i < 10; i ++ ) {
			FR fr = FIX_QATR5_1.get(i);
			sourceStub.set(fr.getTime(), fr.getCandle());
		}
		service.get(); // force recalculate
		service.invalidate(5);
		
		service.shrink();

		assertEquals(4, service.getLastValidIndex());
		List<CDecimal> expected = new ArrayList<>();
		for ( int i = 0; i < 5; i ++ ) {
			expected.add(FIX_QATR5_1.get(i).getExpected());
		}
		assertEquals(expected, cacheStub);
	}
	
	@Test
	public void testShrink_PartiallyShrunkCase2() throws Exception {
		for ( int i = 0; i < 10; i ++ ) {
			FR fr = FIX_QATR5_1.get(i);
			sourceStub.set(fr.getTime(), fr.getCandle());
		}
		service.get(); // force recalculate
		service.invalidate(1);
		
		service.shrink();

		assertEquals(0, service.getLastValidIndex());
		List<CDecimal> expected = new ArrayList<>();
		for ( int i = 0; i < 1; i ++ ) {
			expected.add(FIX_QATR5_1.get(i).getExpected());
		}
		assertEquals(expected, cacheStub);
	}
	
	@Test
	public void testEnsureATR_IS_Correct_() throws Exception {
		TAMath math = TAMath.getInstance();
		service = new QATRTSeriesFast("zulu", sourceStub, 10, 7, cacheStub);
		fillQATR10_2();
		List<FR> fix = FIX_QATR10_2;
		
		for ( int i = 0; i < fix.size(); i ++ ) {
			FR fr = fix.get(i);
			CDecimal b = math.qatr(sourceStub, i, 10), a = service.get(i);
			if ( b != null ) b = b.withScale(6);
			if ( a != null ) a = a.withScale(6);
			String msg = "At#" + i;
			assertEquals(msg + "[TAMath]", fr.expected, b);
			assertEquals(msg + "[Fast]", fr.expected, a);
		}
	}
	
	@Test
	public void testGetFirstIndexBefore() throws Exception {
		fillQATR5_1();
		assertEquals(-1, service.getFirstIndexBefore(T("2018-05-01T00:00:00Z")));
		assertEquals(-1, service.getFirstIndexBefore(T("2018-06-01T10:00:00Z")));
		assertEquals( 0, service.getFirstIndexBefore(T("2018-06-01T10:05:00Z")));
		assertEquals( 1, service.getFirstIndexBefore(T("2018-06-01T10:10:00Z")));
		assertEquals( 2, service.getFirstIndexBefore(T("2018-06-01T10:15:00Z")));
		assertEquals( 3, service.getFirstIndexBefore(T("2018-06-01T10:20:00Z")));
		assertEquals( 4, service.getFirstIndexBefore(T("2018-06-01T10:25:00Z")));
		assertEquals( 5, service.getFirstIndexBefore(T("2018-06-01T10:30:00Z")));
		assertEquals( 6, service.getFirstIndexBefore(T("2018-06-01T10:35:00Z")));
		assertEquals( 7, service.getFirstIndexBefore(T("2018-06-01T10:40:00Z")));
		assertEquals( 8, service.getFirstIndexBefore(T("2018-06-01T10:45:00Z")));
		assertEquals( 9, service.getFirstIndexBefore(T("2018-06-01T10:50:00Z")));
		assertEquals(10, service.getFirstIndexBefore(T("2018-06-01T10:55:00Z")));
		assertEquals(11, service.getFirstIndexBefore(T("2018-06-01T11:00:00Z")));
		assertEquals(12, service.getFirstIndexBefore(T("2018-06-01T11:05:00Z")));
		assertEquals(13, service.getFirstIndexBefore(T("2018-06-01T11:10:00Z")));
		assertEquals(14, service.getFirstIndexBefore(T("2018-06-01T11:15:00Z")));
		assertEquals(15, service.getFirstIndexBefore(T("2018-06-01T11:20:00Z")));
		assertEquals(16, service.getFirstIndexBefore(T("2018-06-01T11:25:00Z")));
		assertEquals(17, service.getFirstIndexBefore(T("2018-06-01T11:30:00Z")));
		assertEquals(18, service.getFirstIndexBefore(T("2018-06-01T11:35:00Z")));
		assertEquals(19, service.getFirstIndexBefore(T("2018-06-01T11:40:00Z")));
		assertEquals(20, service.getFirstIndexBefore(T("2018-06-01T11:45:00Z")));
		assertEquals(20, service.getFirstIndexBefore(T("2018-06-01T11:50:00Z")));
	}
	
	@Test
	public void testGetFirstBefore() throws Exception {
		fillQATR5_1();
		assertNull(service.getFirstBefore(T("2018-05-01T00:00:00Z")));
		assertNull(service.getFirstBefore(T("2018-06-01T10:00:00Z")));
		assertNull(service.getFirstBefore(T("2018-06-01T10:05:00Z")));
		assertNull(service.getFirstBefore(T("2018-06-01T10:15:00Z")));
		assertNull(service.getFirstBefore(T("2018-06-01T10:20:00Z")));
		assertEquals(of("404.000000"), service.getFirstBefore(T("2018-06-01T10:25:00Z")));
		assertEquals(of("445.200000"), service.getFirstBefore(T("2018-06-01T10:30:00Z")));
		assertEquals(of("418.160000"), service.getFirstBefore(T("2018-06-01T10:35:00Z")));
		assertEquals(of("358.574336"), service.getFirstBefore(T("2018-06-01T10:55:00Z")));
		assertEquals(of("395.119307"), service.getFirstBefore(T("2018-06-01T11:45:00Z")));
		assertEquals(of("395.119307"), service.getFirstBefore(T("2018-12-31T00:00:00Z")));
	}

	@Test
	public void testBugFix_SpecialCase_IndexOutOfRange() throws Exception {
		eex.expect(ValueOutOfRangeException.class);
		eex.expectMessage("For index: 0");
		
		service.get(0);
	}
	
	@Test
	public void testBugFix_SpecialCase_SourceEmptyAndRefreshRangeEqPeriodMinus1() throws Exception {
		eex.expect(ValueOutOfRangeException.class);
		eex.expectMessage("For index: 4");

		service.get(4); // period is 5 -> starting from 4
	}

	@Test
	public void testBugFix_SpecialCase_SourceEmptyAndRefreshRangeGtPeriodMinus1() throws Exception {
		eex.expect(ValueOutOfRangeException.class);
		eex.expectMessage("For index: 10");

		service.get(10);
	}

}
