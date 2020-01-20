package ru.prolib.aquila.data.replay;

import java.time.Instant;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.CandleBuilder;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.ZTFrame;

public class CandleReplayToSeriesTest {
	
	static Instant T(String time_string) {
		return Instant.parse(time_string);
	}
	
	private IMocksControl control;
	private EditableTSeries<Candle> targetMock;
	private CandleReplayToSeries service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		targetMock = control.createMock(EditableTSeries.class);
		service = new CandleReplayToSeries(targetMock);
	}
	
	@Test
	public void testGetters() {
		assertSame(targetMock, service.getTargetSeries());
	}

	@Test
	public void testOnCandle() {
		Candle x = new CandleBuilder(ZTFrame.M15MSK)
				.withTime("2020-01-19T19:45:00Z")
				.withOpenPrice(120L)
				.withHighPrice(123L)
				.withLowPrice(119L)
				.withClosePrice(119L)
				.withVolume(1000L)
				.buildCandle();
		expect(targetMock.set(T("2020-01-19T19:45:00Z"), x)).andReturn(null);
		control.replay();
		
		service.onCandle(T("2020-01-19T19:45:00Z"), new Symbol("GAZP"), x);
		
		control.verify();
	}

}
