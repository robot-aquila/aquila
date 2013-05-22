package ru.prolib.aquila.ib.utils;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;

import com.csvreader.CsvWriter;
import com.ib.client.Contract;
import com.ib.client.EClientSocket;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventSystemImpl;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.StarterQueue;
import ru.prolib.aquila.ib.subsys.api.IBClient;
import ru.prolib.aquila.ib.subsys.api.IBClientStarter;
import ru.prolib.aquila.ib.subsys.api.IBConfig;

/**
 * $Id$
 */
public class Main implements Runnable, EventListener {
	
	private final StarterQueue starter = new StarterQueue();
	private EventSystem es = new EventSystemImpl(new EventQueueImpl("IB-IMPORTER"));
	private IBClientStarter clientStarter;
	private IBUtilsWrapper wrapper;
	
	static {
		BasicConfigurator.configure();
	}
	
	/**
	 * Symbol date filepath	
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String[] params = {"DGAZ", "20130230 16:00:00", "G:/javawork/temp/Tests/DGAZ.csv"};
			new Main().run(params);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void run(String[] args) throws IOException {
		EventDispatcher dispatcher = es.createEventDispatcher();
		starter.add(es.getEventQueue());
		
		wrapper = new IBUtilsWrapper(dispatcher, es.createGenericType(dispatcher),
				 es.createGenericType(dispatcher), es.createGenericType(dispatcher),
				 es.createGenericType(dispatcher), es.createGenericType(dispatcher),
				 es.createGenericType(dispatcher), es.createGenericType(dispatcher),
				 es.createGenericType(dispatcher), es.createGenericType(dispatcher),
				 es.createGenericType(dispatcher), es.createGenericType(dispatcher));
		IBClient client = new IBClientHistoricalRq(new EClientSocket(wrapper), wrapper, dispatcher,
				es.createGenericType(dispatcher), es.createGenericType(dispatcher));
		clientStarter = new IBClientStarter(client, new IBConfig());
		
		starter.add(clientStarter);
		try {
			starter.start();			
		} catch (StarterException e) {
			e.printStackTrace();
			run();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			starter.stop();
		} catch (StarterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}

	/* (non-Javadoc)
	 * @see ru.prolib.aquila.core.EventListener#onEvent(ru.prolib.aquila.core.Event)
	 */
	@Override
	public void onEvent(Event event) {
		if(event.isType(clientStarter.getClient().OnConnectionOpened())) {
			RespHandler handler;
			handler = new RespHandler(new CsvWriter(
					"G:/javawork/temp/Tests/DGAZ.csv"));
			wrapper.OnHistoricalData().addListener(handler);
			
			Contract contract = new Contract();
			contract.m_exchange = "SMART";
			contract.m_symbol = "DGAZ";
			
			IBClientHistoricalRq c = (IBClientHistoricalRq) clientStarter.getClient();
			c.reqHistoricalData(222, contract, "20130230 16:00:00", "2 D", "1 min", "TRADES", 1, 1);			
		}
		
	}
}
