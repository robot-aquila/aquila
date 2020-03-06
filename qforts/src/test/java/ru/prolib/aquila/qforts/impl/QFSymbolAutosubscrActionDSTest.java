package ru.prolib.aquila.qforts.impl;

import static org.easymock.EasyMock.*;

import org.apache.log4j.BasicConfigurator;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.DataSource;
import ru.prolib.aquila.qforts.impl.QFSymbolAutosubscr.FeedStatus;

public class QFSymbolAutosubscrActionDSTest {
	static Symbol symbol = new Symbol("FOOB");
	
	@BeforeClass
	public static void setUpBeforeClass() {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
	}
	
	IMocksControl control;
	DataSource dsMock;
	L1UpdateConsumer consumerMock;
	QFSymbolAutosubscrActionDS service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		dsMock = control.createMock(DataSource.class);
		consumerMock = control.createMock(L1UpdateConsumer.class);
		service = new QFSymbolAutosubscrActionDS(dsMock, consumerMock);
	}
	
	@Test
	public void testChange_ToNotRequired() {
		dsMock.unsubscribeL1(symbol, consumerMock);
		control.replay();
		
		service.change(symbol, null, FeedStatus.NOT_REQUIRED);
		
		control.verify();
	}
	
	@Test
	public void testChange_ToMaxDetails_FromNotRequired() {
		dsMock.subscribeL1(symbol, consumerMock);
		control.replay();
		
		service.change(symbol, FeedStatus.NOT_REQUIRED, FeedStatus.MAX_DETAILS);
		
		control.verify();
	}
	
	@Test
	public void testChange_ToMaxDetails_FromLessDetails() {
		control.replay();
		
		service.change(symbol, FeedStatus.LESS_DETAILS, FeedStatus.MAX_DETAILS);
		
		control.verify();
	}

	@Test
	public void testChange_ToLessDetails() {
		control.replay();
		
		service.change(symbol,  null, FeedStatus.LESS_DETAILS);
		
		control.verify();
	}

}
