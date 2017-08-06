package ru.prolib.aquila.data;

import static org.easymock.EasyMock.*;

import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.DeltaUpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.MDUpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class DataSourceImplTest {
	private IMocksControl control;
	private L1UpdateSource l1UpdateSourceMock;
	private MDUpdateSource mdUpdateSourceMock;
	private SymbolUpdateSource symbolUpdateSourceMock;
	private L1UpdateConsumer l1UpdateConsumerMock;
	private MDUpdateConsumer mdUpdateConsumerMock;
	private DeltaUpdateConsumer symbolUpdateConsumerMock;
	private DataSourceImpl dataSource;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		l1UpdateSourceMock = control.createMock(L1UpdateSource.class);
		mdUpdateSourceMock = control.createMock(MDUpdateSource.class);
		symbolUpdateSourceMock = control.createMock(SymbolUpdateSource.class);
		l1UpdateConsumerMock = control.createMock(L1UpdateConsumer.class);
		mdUpdateConsumerMock = control.createMock(MDUpdateConsumer.class);
		symbolUpdateConsumerMock = control.createMock(DeltaUpdateConsumer.class);
		dataSource = new DataSourceImpl();
	}
	
	@Test
	public void testSubscribeL1() {
		l1UpdateSourceMock.subscribeL1(new Symbol("foobar"), l1UpdateConsumerMock);
		control.replay();
		dataSource.setL1UpdateSource(l1UpdateSourceMock);
		
		dataSource.subscribeL1(new Symbol("foobar"), l1UpdateConsumerMock);
		
		control.verify();
	}

	@Test
	public void testUnsubscribeL1() {
		l1UpdateSourceMock.unsubscribeL1(new Symbol("GAZP"), l1UpdateConsumerMock);
		control.replay();
		dataSource.setL1UpdateSource(l1UpdateSourceMock);
		
		dataSource.unsubscribeL1(new Symbol("GAZP"), l1UpdateConsumerMock);
		
		control.verify();
	}

	@Test
	public void testSubscribeMD() {
		mdUpdateSourceMock.subscribeMD(new Symbol("AAPL"), mdUpdateConsumerMock);
		control.replay();
		dataSource.setMDUpdateSource(mdUpdateSourceMock);
		
		dataSource.subscribeMD(new Symbol("AAPL"), mdUpdateConsumerMock);
		
		control.verify();
	}
	
	@Test
	public void testUnsubscribeMD() {
		mdUpdateSourceMock.unsubscribeMD(new Symbol("AAPL"), mdUpdateConsumerMock);
		control.replay();
		dataSource.setMDUpdateSource(mdUpdateSourceMock);
		
		dataSource.unsubscribeMD(new Symbol("AAPL"), mdUpdateConsumerMock);
		
		control.verify();
	}
	
	@Test
	public void testSubscribeSymbol() {
		symbolUpdateSourceMock.subscribeSymbol(new Symbol("MSFT"), symbolUpdateConsumerMock);
		control.replay();
		dataSource.setSymbolUpdateSource(symbolUpdateSourceMock);
		
		dataSource.subscribeSymbol(new Symbol("MSFT"), symbolUpdateConsumerMock);
		
		control.verify();
	}
	
	@Test
	public void testUnsubscribeSymbol() {
		symbolUpdateSourceMock.unsubscribeSymbol(new Symbol("SBER"), symbolUpdateConsumerMock);
		control.replay();
		dataSource.setSymbolUpdateSource(symbolUpdateSourceMock);
		
		dataSource.unsubscribeSymbol(new Symbol("SBER"), symbolUpdateConsumerMock);
		
		control.verify();
	}
	
	@Test
	public void testClose() throws Exception {
		l1UpdateSourceMock.close();
		mdUpdateSourceMock.close();
		symbolUpdateSourceMock.close();
		control.replay();
		dataSource.setL1UpdateSource(l1UpdateSourceMock);
		dataSource.setMDUpdateSource(mdUpdateSourceMock);
		dataSource.setSymbolUpdateSource(symbolUpdateSourceMock);
		
		dataSource.close();
		
		control.verify();
	}
	
	@Test
	public void testSetStartTimeL1() {
		Instant t = Instant.parse("2017-08-06T18:55:00Z");
		l1UpdateSourceMock.setStartTimeL1(new Symbol("GAZP"), t);
		control.replay();
		dataSource.setL1UpdateSource(l1UpdateSourceMock);
		
		dataSource.setStartTimeL1(new Symbol("GAZP"), t);
		
		control.verify();
	}

}
