package ru.prolib.aquila.ib.utils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;

import javax.swing.JFrame;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ib.client.EClientSocket;

import ru.prolib.aquila.core.EventDispatcher;
import ru.prolib.aquila.core.EventQueueImpl;
import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.core.EventSystemImpl;
import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.StarterQueue;
import ru.prolib.aquila.ib.api.IBConfig;
import ru.prolib.aquila.ib.subsys.api.IBClient;
import ru.prolib.aquila.ib.subsys.api.IBClientStarter;

/**
 * $Id$
 */
public class Main extends JFrame implements Runnable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6270812093791237441L;
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	private final StarterQueue starter = new StarterQueue();
	private EventSystem es = new EventSystemImpl(new EventQueueImpl("IB-IMPORTER"));
	private IBClientStarter clientStarter;
	private IBUtilsWrapper wrapper;
	private EventDispatcher dispatcher;
	
	static {
		BasicConfigurator.configure();
	}
	
	/**
	 * Symbol date filepath	
	 * @param args
	 */
	public static void main(String[] args) {
		new Main().run();
	}
	
	public void run() {
		addWindowListener(new WindowAdapter() {
			@Override 
			public void windowClosing(WindowEvent e) { 
				try {
					exit();
				} catch (StarterException e1) {					
					e1.printStackTrace();
					System.exit(ERROR);
				} 
			}
		});
		dispatcher = es.createEventDispatcher();
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
			System.exit(ERROR);
		}
		MainDlg main = new MainDlg(this);
		main.createUI();
		add(main);
		setSize(300, 500);
		setVisible(true);
		
	}
	
	public void exit() throws StarterException {
		starter.stop();
		System.exit(0);
	}
	
	public void doImport(IBHistoricalRequestParams params, String destFile)
	{
		try {
			IBClientHistoricalRq c = (IBClientHistoricalRq) clientStarter.getClient();		
			IBHistoricalRequest request = new IBHistoricalRequest(c, params);
			RespHandler handler = new RespHandler(destFile, dispatcher, 
					es.createGenericType(dispatcher));
			wrapper.OnHistoricalData().addListener(handler);
		
			handler.OnHandled().addListener(request);
			request.execute();
		} catch(ParseException e) {
			logger.error(e.getMessage());
		}
	}
}
