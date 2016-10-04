package ru.prolib.aquila.data.replay;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.CloseableIterator;
import ru.prolib.aquila.core.BusinessEntities.L1Update;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateBuilder;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumerStub;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.TStamped;
import ru.prolib.aquila.data.TimeConverter;

public class L1AbstractReplayServiceTest {
	
	static class TestService extends L1AbstractReplayService {
		
		TestService(TimeConverter timeConverter) {
			super(timeConverter);
		}
		
		@Override
		public CloseableIterator<? extends TStamped> createReader() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	protected static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	protected static final Symbol symbol1 = new Symbol("AAPL");
	protected static final Symbol symbol2 = new Symbol("MSFT");
	protected IMocksControl control;
	protected TimeConverter timeConverterMock;
	protected L1AbstractReplayService abstractService;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		timeConverterMock = control.createMock(TimeConverter.class);
		abstractService = createService(timeConverterMock);
	}

	protected L1AbstractReplayService createService(TimeConverter timeConverter) {
		return new TestService(timeConverter);
	}
	
	@Test
	public void testConsumptionTime() {
		L1Update object = new L1UpdateBuilder(symbol1)
			.withAsk()
			.withPrice(12.48d)
			.withSize(120L)
			.withTime("2016-10-04T07:44:39Z")
			.buildL1Update();
		expect(timeConverterMock.convert(T("2016-09-01T00:00:00Z"), T("2016-10-04T07:44:39Z")))
			.andReturn(T("1998-08-01T00:00:00Z"));
		control.replay();
		
		Instant actual = abstractService.consumptionTime(T("2016-09-01T00:00:00Z"), object);
		
		control.verify();
		assertEquals(T("1998-08-01T00:00:00Z"), actual);
	}
	
	@Test
	public void testMutate() {
		L1Update object = new L1UpdateBuilder(symbol1)
			.withAsk()
			.withPrice(12.48d)
			.withSize(120L)
			.withTime("2016-10-04T07:44:39Z")
			.buildL1Update();
		control.replay();
		
		L1Update actual = (L1Update) abstractService.mutate(object, T("1978-06-15T13:49:26Z"));
		
		control.verify();
		L1Update expected = new L1UpdateBuilder(symbol1)
			.withAsk()
			.withPrice(12.48d)
			.withSize(120L)
			.withTime("1978-06-15T13:49:26Z")
			.buildL1Update();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testConsume() throws Exception {
		L1UpdateConsumerStub consumer1 = new L1UpdateConsumerStub(),
				consumer2 = new L1UpdateConsumerStub(),
				consumer3 = new L1UpdateConsumerStub();
		abstractService.subscribeL1(symbol1, consumer1);
		abstractService.subscribeL1(symbol2, consumer2);
		abstractService.subscribeL1(symbol1, consumer3);
		L1Update object = new L1UpdateBuilder(symbol1)
			.withAsk()
			.withPrice(12.48d)
			.withSize(120L)
			.withTime("2016-10-04T07:44:39Z")
			.buildL1Update();
		control.replay();
		
		abstractService.consume(object);
		
		control.verify();
		assertTrue(consumer1.isConsumed(object));
		assertFalse(consumer2.isConsumed(object));
		assertTrue(consumer3.isConsumed(object));
	}

	@Test
	public void testUnsubscribeL1() throws Exception {
		L1UpdateConsumerStub consumer1 = new L1UpdateConsumerStub(),
				consumer2 = new L1UpdateConsumerStub();
		abstractService.subscribeL1(symbol1, consumer1);
		abstractService.subscribeL1(symbol1, consumer2);
		L1Update object = new L1UpdateBuilder(symbol1)
			.withAsk()
			.withPrice(12.48d)
			.withSize(120L)
			.withTime("2016-10-04T07:44:39Z")
			.buildL1Update();
		control.replay();
		
		abstractService.unsubscribeL1(symbol1, consumer2);

		control.verify();
		abstractService.consume(object);
		assertTrue(consumer1.isConsumed(object));
		assertFalse(consumer2.isConsumed(object));
	}
	
	@Test
	public void testClose() throws Exception {
		L1UpdateConsumerStub consumer1 = new L1UpdateConsumerStub(),
				consumer2 = new L1UpdateConsumerStub();
		abstractService.subscribeL1(symbol1, consumer1);
		abstractService.subscribeL1(symbol1, consumer2);
		L1Update object = new L1UpdateBuilder(symbol1)
			.withAsk()
			.withPrice(12.48d)
			.withSize(120L)
			.withTime("2016-10-04T07:44:39Z")
			.buildL1Update();
		control.replay();

		abstractService.close();
		
		control.verify();
		abstractService.consume(object);
		assertFalse(consumer1.isConsumed(object));
		assertFalse(consumer2.isConsumed(object));
	}
	
}
