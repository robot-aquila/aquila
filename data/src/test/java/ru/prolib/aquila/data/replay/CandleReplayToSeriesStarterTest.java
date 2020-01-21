package ru.prolib.aquila.data.replay;

import static org.junit.Assert.*;

import java.util.concurrent.CompletableFuture;

import static org.easymock.EasyMock.*;

import org.easymock.Capture;
import org.easymock.IMocksControl;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.TFSymbol;
import ru.prolib.aquila.core.data.ZTFrame;

public class CandleReplayToSeriesStarterTest {
	private static Symbol symbol = new Symbol("GAZP");
	private IMocksControl control;
	private CandleReplayService crsMock;
	private EditableTSeries<Candle> targetMock;
	private SubscrHandler shMock;
	private CandleReplayToSeriesStarter service;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		crsMock = control.createMock(CandleReplayService.class);
		targetMock = control.createMock(EditableTSeries.class);
		shMock = control.createMock(SubscrHandler.class);
		service = new CandleReplayToSeriesStarter(crsMock, symbol, targetMock);
	}
	
	private CompletableFuture<Boolean> confirm(boolean r) {
		CompletableFuture<Boolean> f = new CompletableFuture<>();
		f.complete(r);
		return f;
	}
	@Test
	public void testStart() throws Exception {
		expect(targetMock.getTimeFrame()).andStubReturn(ZTFrame.M5MSK);
		Capture<CandleListener> cap = Capture.newInstance();
		expect(crsMock.subscribe(eq(new TFSymbol(symbol, ZTFrame.M5MSK)), capture(cap))).andReturn(shMock);
		expect(shMock.getConfirmation()).andReturn(confirm(true));
		control.replay();
		
		service.start();
		service.start(); // subsequent calls has no effect
		service.start();
		
		control.verify();
		assertThat(cap.getValue(), IsInstanceOf.instanceOf(CandleReplayToSeries.class));
		assertEquals(targetMock, ((CandleReplayToSeries)cap.getValue()).getTargetSeries());
	}
	
	@Test
	public void testStop_HasNoEffectIfNotStarted() throws Exception {
		control.replay();
		
		service.stop();
		service.stop();
		
		control.verify();
	}

	@Test
	public void testStop() throws Exception {
		expect(targetMock.getTimeFrame()).andStubReturn(ZTFrame.M5MSK);
		Capture<CandleListener> cap = Capture.newInstance();
		expect(crsMock.subscribe(eq(new TFSymbol(symbol, ZTFrame.M5MSK)), capture(cap))).andReturn(shMock);
		expect(shMock.getConfirmation()).andReturn(confirm(true));
		control.replay();
		service.start();
		control.resetToStrict();
		shMock.close();
		control.replay();
		
		service.stop();
		service.stop();
		service.stop();
		
		control.verify();
	}

}
