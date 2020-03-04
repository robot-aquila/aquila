package ru.prolib.aquila.qforts.impl;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.data.DataSource;
import ru.prolib.aquila.qforts.impl.QFSymbolAutosubscr.FeedStatus;

public class QFSymbolAutosubscrActionDSTest {
	static Symbol symbol = new Symbol("FOOB");
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
	public void testChange_ToMaxDetails() {
		dsMock.subscribeL1(symbol, consumerMock);
		control.replay();
		
		service.change(symbol,  null, FeedStatus.MAX_DETAILS);
		
		control.verify();
	}

	@Test
	public void testChange_ToLessDetails() {
		control.replay();
		
		service.change(symbol,  null, FeedStatus.LESS_DETAILS);
		
		control.verify();
	}

}
