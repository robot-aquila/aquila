package ru.prolib.aquila.core.data;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.*;

import ru.prolib.aquila.core.BusinessEntities.CDecimal;
import ru.prolib.aquila.core.BusinessEntities.CDecimalBD;
import ru.prolib.aquila.core.concurrency.LID;

/**
 * 2013-03-12<br>
 * $Id: TRTest.java 571 2013-03-12 00:53:34Z whirlwind $
 */
public class TrueRangeTest {
	private static final String fixture[][] = {
		// hi, lo, close, tr expected
		{ "48.70", "47.79", "48.16", "0.91" },
		{ "49.35", "48.86", "49.32", "1.19" },// HL=0.49 HCp=1.19 LCp=0.70: TR=1.19
		{ "49.92", "49.50", "49.91", "0.60" },
		{ "50.19", "49.87", "50.13", "0.32" },// HL=0.32 HCp=0.28 LCp=0.04: TR=0.32
		{ "50.12", "49.20", "49.53", "0.93" },
	};
	
	private IMocksControl control;
	private Series<Candle> candlesMock;
	private SeriesImpl<Candle> source;
	private TrueRange indicator;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		candlesMock = control.createMock(Series.class);
		source = new SeriesImpl<>();
		indicator = new TrueRange("foo", source);
	}
	
	@Test
	public void testConstruct2() throws Exception {
		assertEquals("foo", indicator.getId());
	}
	
	@Test
	public void testConstruct1() throws Exception {
		indicator = new TrueRange(source);
		assertEquals(Series.DEFAULT_ID, indicator.getId());
	}
	
	@Test
	public void testGet() throws Exception {
		Instant time = Instant.parse("2013-10-11T11:12:34Z");
		for ( int i = 0; i < fixture.length; i ++ ) {
			source.add(new CandleBuilder()
					.withTime(time.plusSeconds(i * 60))
					.withTimeFrame(ZTFrame.M1)
					.withOpenPrice(CDecimalBD.ZERO)
					.withHighPrice(CDecimalBD.of(fixture[i][0]))
					.withLowPrice(CDecimalBD.of(fixture[i][1]))
					.withClosePrice(CDecimalBD.of(fixture[i][2]))
					.buildCandle());
			CDecimal expected = CDecimalBD.of(fixture[i][3]);
			String msg = "At #" + i;
			assertEquals(msg, expected, indicator.get());
			assertEquals(msg, expected, indicator.get(i));
		}
		assertEquals(fixture.length, indicator.getLength());
	}
	
	@Test
	public void testGetLID() {
		LID lidStub = LID.createInstance();
		expect(candlesMock.getLID()).andReturn(lidStub);
		control.replay();
		
		indicator = new TrueRange(candlesMock);
		assertSame(lidStub, indicator.getLID());
		
		control.verify();
	}
	
	@Test
	public void testLock() {
		candlesMock.lock();
		control.replay();
		
		indicator = new TrueRange(candlesMock);
		indicator.lock();
		
		control.verify();
	}
	
	@Test
	public void testUnlock() {
		candlesMock.unlock();
		control.replay();
		
		indicator = new TrueRange(candlesMock);
		indicator.unlock();
		
		control.verify();
	}	

}
