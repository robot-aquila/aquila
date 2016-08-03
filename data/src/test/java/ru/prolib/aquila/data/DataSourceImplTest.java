package ru.prolib.aquila.data;

import static org.easymock.EasyMock.*;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.L1UpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.MDUpdateConsumer;
import ru.prolib.aquila.core.BusinessEntities.Symbol;

public class DataSourceImplTest {
	private IMocksControl control;
	private L1UpdateSource l1UpdateSourceMock;
	private MDUpdateSource mdUpdateSourceMock;
	private L1UpdateConsumer l1UpdateConsumerMock;
	private MDUpdateConsumer mdUpdateConsumerMock;
	private DataSourceImpl dataSource;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		l1UpdateSourceMock = control.createMock(L1UpdateSource.class);
		mdUpdateSourceMock = control.createMock(MDUpdateSource.class);
		l1UpdateConsumerMock = control.createMock(L1UpdateConsumer.class);
		mdUpdateConsumerMock = control.createMock(MDUpdateConsumer.class);
		dataSource = new DataSourceImpl();
	}
	
	@Test
	public void testSubscribe_L1UpdateConsumer() {
		l1UpdateSourceMock.subscribe(new Symbol("foobar"), l1UpdateConsumerMock);
		control.replay();
		dataSource.setL1UpdateSource(l1UpdateSourceMock);
		
		dataSource.subscribe(new Symbol("foobar"), l1UpdateConsumerMock);
		
		control.verify();
	}

	@Test
	public void testUnsubscribe_L1UpdateConsumer() {
		l1UpdateSourceMock.unsubscribe(new Symbol("GAZP"), l1UpdateConsumerMock);
		control.replay();
		dataSource.setL1UpdateSource(l1UpdateSourceMock);
		
		dataSource.unsubscribe(new Symbol("GAZP"), l1UpdateConsumerMock);
		
		control.verify();
	}

	@Test
	public void testSubscribe_MDUpdateConsumer() {
		mdUpdateSourceMock.subscribe(new Symbol("AAPL"), mdUpdateConsumerMock);
		control.replay();
		dataSource.setMDUpdateSource(mdUpdateSourceMock);
		
		dataSource.subscribe(new Symbol("AAPL"), mdUpdateConsumerMock);
		
		control.verify();
	}
	
	@Test
	public void testUnsubscribe_MDUpdateConsumer() {
		mdUpdateSourceMock.unsubscribe(new Symbol("AAPL"), mdUpdateConsumerMock);
		control.replay();
		dataSource.setMDUpdateSource(mdUpdateSourceMock);
		
		dataSource.unsubscribe(new Symbol("AAPL"), mdUpdateConsumerMock);
		
		control.verify();
	}
	
	@Test
	public void testClose() throws Exception {
		l1UpdateSourceMock.close();
		mdUpdateSourceMock.close();
		control.replay();
		dataSource.setL1UpdateSource(l1UpdateSourceMock);
		dataSource.setMDUpdateSource(mdUpdateSourceMock);
		
		dataSource.close();
		
		control.verify();
	}

}
