package ru.prolib.aquila.core.data.tseries;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import static ru.prolib.aquila.core.data.tseries.SecurityChartDataHandler.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.core.data.EditableTSeries;
import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.tseries.SecurityChartDataHandler.FactoryImpl;
import ru.prolib.aquila.core.data.tseries.filler.CandleSeriesByLastTrade;

@SuppressWarnings("unchecked")
public class SecurityChartDataHandlerTest {
	private static Symbol symbol = new Symbol("BTC");
	private IMocksControl control;
	private HandlerSetup setupMock;
	private Factory factoryMock;
	private Terminal terminalMock;
	private EventQueue queueMock;
	private STSeries sourceMock;
	private TSeriesCacheControllerETS<Candle> cacheCtrlMock;
	private CandleSeriesByLastTrade ohlcProducerMock;
	private EditableTSeries<Candle> ohlcMock;
	private SecurityChartDataHandler service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		setupMock = control.createMock(HandlerSetup.class);
		factoryMock = control.createMock(Factory.class);
		terminalMock = control.createMock(Terminal.class);
		queueMock = control.createMock(EventQueue.class);
		sourceMock = control.createMock(STSeries.class);
		cacheCtrlMock = control.createMock(TSeriesCacheControllerETS.class);
		ohlcProducerMock = control.createMock(CandleSeriesByLastTrade.class);
		ohlcMock = control.createMock(EditableTSeries.class);
		service = new SecurityChartDataHandler(setupMock, factoryMock);
	}
	
	@Test
	public void testFactoryImpl_CreateSeries() {
		FactoryImpl service = new FactoryImpl(setupMock);
		expect(setupMock.getSharedSeriesID()).andStubReturn("zulu24");
		expect(setupMock.getEventQueue()).andStubReturn(queueMock);
		expect(setupMock.getTimeFrame()).andStubReturn(ZTFrame.D1MSK);
		control.replay();
		
		STSeries actual = service.createSeries();
		
		assertNotNull(actual);
		assertEquals("zulu24", actual.getId());
		assertEquals(ZTFrame.D1MSK, actual.getTimeFrame());
	}
	
	@Test
	public void testFactoryImpl_CreateCacheCtrl() {
		FactoryImpl service = new FactoryImpl(setupMock);
		EditableTSeries<Candle> ohlcMock = control.createMock(EditableTSeries.class);
		
		TSeriesCacheControllerETS<Candle> actual = service.createCacheCtrl(ohlcMock);
		
		assertNotNull(actual);
		// Ho to test that ohlcMock is actually wrapped?
		// just call it using delegate
		expect(ohlcMock.getId()).andReturn("charlie");
		control.replay();
		assertEquals("charlie", actual.getId());
	}
	
	@Test
	public void testFactoryImpl_CreateOhlcProducer() {
		FactoryImpl service = new FactoryImpl(setupMock);
		EditableTSeries<Candle> ohlcMock = control.createMock(EditableTSeries.class);
		expect(setupMock.getTerminal()).andReturn(terminalMock);
		expect(setupMock.getSymbol()).andReturn(symbol);
		control.replay();
		
		CandleSeriesByLastTrade actual = service.createOhlcProducer(ohlcMock);
		
		assertNotNull(actual);
	}
	
	@Test
	public void testCtor2() {
		assertSame(setupMock, service.getSetup());
		assertSame(factoryMock, service.getFactory());
	}
	
	@Test
	public void testCtor1() {
		service = new SecurityChartDataHandler(setupMock);
		assertSame(setupMock, service.getSetup());
		FactoryImpl actual = (FactoryImpl) service.getFactory();
		assertNotNull(actual);
	}
	
	private void setExpectationsForInitialize() {
		expect(setupMock.getOhlcSeriesID()).andStubReturn("source");
		expect(setupMock.getOhlcMutatorSeriesID()).andStubReturn("source.mut");
		
		expect(factoryMock.createSeries()).andReturn(sourceMock);
		sourceMock.createSeries("source", false);
		expectLastCall().andReturn(ohlcMock);
		expect(factoryMock.createCacheCtrl(ohlcMock)).andReturn(cacheCtrlMock);
		sourceMock.registerRawSeries(cacheCtrlMock, "source.mut");
		setupMock.loadInitialData(ohlcMock);
		setupMock.createDerivedSeries(sourceMock, cacheCtrlMock, ohlcMock);
		expect(factoryMock.createOhlcProducer(cacheCtrlMock)).andReturn(ohlcProducerMock);
	}
	
	@Test
	public void testInitialize_OK() {
		setExpectationsForInitialize();
		control.replay();
		
		service.initialize();
		
		control.verify();
	}
	
	@Test
	public void testInitialize_ThrowsIfAlreadyInitialized() {
		setExpectationsForInitialize();
		control.replay();
		service.initialize();
		control.resetToStrict();
		control.replay();
		
		try {
			service.initialize();
			fail("Expected: " + IllegalStateException.class.getSimpleName());
		} catch ( IllegalStateException e ) {
			assertEquals("Already initialized", e.getMessage());
		}
	}
	
	@Test
	public void testStartDataHandling_OK() {
		setExpectationsForInitialize();
		control.replay();
		service.initialize();
		control.resetToStrict();
		ohlcProducerMock.start();
		expectLastCall().andReturn(ohlcProducerMock);
		control.replay();
		
		service.startDataHandling();
		
		control.verify();
	}
	
	@Test
	public void testStartDataHandling_SkipIfStarted() {
		setExpectationsForInitialize();
		ohlcProducerMock.start();
		expectLastCall().andReturn(ohlcProducerMock);
		control.replay();
		service.initialize();
		service.startDataHandling();
		control.resetToStrict();
		control.replay();
		
		service.startDataHandling();
		
		control.verify();
	}
	
	@Test
	public void testStartDataHandling_ThrowsIfNotInitialized() {
		control.replay();
		
		try {
			service.startDataHandling();
			fail("Expected: " + IllegalStateException.class.getSimpleName());
		} catch ( IllegalStateException e ) {
			assertEquals("Handler is not initialized", e.getMessage());
		}
	}

	@Test
	public void testStartDataHandling_ThrowsIfClosed() {
		setExpectationsForInitialize();
		ohlcProducerMock.start();
		expectLastCall().andReturn(ohlcProducerMock);
		ohlcProducerMock.stop();
		expectLastCall().andReturn(ohlcProducerMock);
		control.replay();
		service.initialize();
		service.startDataHandling();
		service.stopDataHandling();
		service.close();
		control.resetToStrict();
		
		try {
			service.startDataHandling();
			fail("Expected: " + IllegalStateException.class.getSimpleName());
		} catch ( IllegalStateException e ) {
			assertEquals("Handler is closed", e.getMessage());
		}
	}
	
	@Test
	public void testStopDataHandling_OK() {
		setExpectationsForInitialize();
		ohlcProducerMock.start();
		expectLastCall().andReturn(ohlcProducerMock);
		control.replay();
		service.initialize();
		service.startDataHandling();
		control.resetToStrict();
		ohlcProducerMock.stop();
		expectLastCall().andReturn(ohlcProducerMock);
		control.replay();
		
		service.stopDataHandling();
		
		control.verify();
	}
	
	@Test
	public void testStopDataHandling_SkipIfNotStarted() {
		setExpectationsForInitialize();
		ohlcProducerMock.start();
		expectLastCall().andReturn(ohlcProducerMock);
		ohlcProducerMock.stop();
		expectLastCall().andReturn(ohlcProducerMock);
		control.replay();
		service.initialize();
		service.startDataHandling();
		service.stopDataHandling();
		control.resetToStrict();
		control.replay();

		service.stopDataHandling();
		
		control.verify();
	}
	
	@Test
	public void testClose_OK() {
		control.replay();
		
		service.close();
		
		control.verify();
	}
	
	@Test
	public void testClose_ThrowsIfStarted() {
		setExpectationsForInitialize();
		ohlcProducerMock.start();
		expectLastCall().andReturn(ohlcProducerMock);
		control.replay();
		service.initialize();
		service.startDataHandling();
		
		try {
			service.close();
			fail("Expected: " + IllegalStateException.class.getSimpleName());
		} catch ( IllegalStateException e ) {
			assertEquals("Handler is started", e.getMessage());
		}
	}
	
	@Ignore
	@Test
	public void testClose_OtherCases() {
		fail();
	}

}
