package ru.prolib.aquila.t2q.jqt;

import ru.prolib.aquila.JQTrans.JQTransServer;
import ru.prolib.aquila.t2q.T2QHandler;
import ru.prolib.aquila.t2q.T2QService;
import ru.prolib.aquila.t2q.T2QServiceFactory;

/**
 * Фабрика сервисов транзакций.
 * <p>
 * 2013-01-31<br>
 * $Id: JQTServiceFactory.java 531 2013-02-19 15:26:34Z whirlwind $
 */
public class JQTServiceFactory implements T2QServiceFactory {
	
	public JQTServiceFactory() {
		super();
	}

	@Override
	public T2QService createService(T2QHandler handler) {
		JQTHandler hLocal = new JQTHandler(handler);
		try {
			JQTransServer server = new JQTransServer(hLocal);
			hLocal.setServer(server);
			return new JQTService(hLocal, server);
		} catch ( Exception e ) {
			throw new RuntimeException("Error instantiate JQTransServer: ", e);
		}
	}

}
