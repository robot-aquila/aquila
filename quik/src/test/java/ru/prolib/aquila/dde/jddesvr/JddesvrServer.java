package ru.prolib.aquila.dde.jddesvr;

import java.util.HashMap;

import ru.prolib.aquila.dde.DDEException;
import ru.prolib.aquila.dde.DDEServer;
import ru.prolib.aquila.dde.DDEService;
import ru.prolib.aquila.jddesvr.Server;

/**
 * Реализация DDE на базе jddesvr.
 * <p>
 * Данная реализация адаптирует функционал пакета
 * {@link ru.prolib.aquila.jddesvr} под модель {@link ru.prolib.aquila.dde}. 
 * <p>
 * 2012-07-20<br>
 * $Id$
 */
public class JddesvrServer implements DDEServer {
	private static JddesvrServer instance = null;
	
	private final Server jddesvr;
	private final HashMap<String, JddesvrServiceHandler> map;
	
	private JddesvrServer() {
		super();
		jddesvr = new Server();
		map = new HashMap<String, JddesvrServiceHandler>();
	}
	
	/**
	 * Получить экземпляр сервера.
	 * <p>
	 * Реализация DDE позволяет иметь только один сервер в рамках одного
	 * процесса. Данный метод обеспечивает доступ к единственному экземпляру
	 * сервера. 
	 * <p>
	 * @return экземпляр сервера
	 */
	public static synchronized DDEServer getInstance() {
		if ( instance == null ) {
			instance = new JddesvrServer();
		}
		return instance;
	}

	@Override
	public void join() throws DDEException {
		try {
			jddesvr.join();
		} catch ( Exception e ) {
			throw new DDEException(e);
		}
	}

	@Override
	public void start() throws DDEException {
		try {
			jddesvr.start();
		} catch ( Exception e ) {
			throw new DDEException(e);
		}
	}

	@Override
	public void stop() throws DDEException {
		try {
			jddesvr.stop();
		} catch ( Exception e ) {
			throw new DDEException(e);
		}
	}

	@Override
	public synchronized void registerService(DDEService service)
		throws DDEException
	{
		String name = service.getName();
		if ( map.containsKey(name) ) {
			throw new DDEException("Service already exists: " + name);
		}
		JddesvrServiceHandler handler = new JddesvrServiceHandler(service);
		map.put(name, handler);
		try {
			jddesvr.registerService(handler);
		} catch ( Exception e ) {
			throw new DDEException(e);
		}
	}

	@Override
	public synchronized void unregisterService(String name)
		throws DDEException
	{
		JddesvrServiceHandler handler = map.get(name);
		if ( handler != null ) {
			try {
				jddesvr.unregisterService(handler);
			} catch ( Exception e ) {
				throw new DDEException(e);
			}
			map.remove(name);
		}
	}

}
