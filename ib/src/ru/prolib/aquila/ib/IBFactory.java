package ru.prolib.aquila.ib;

import java.util.Properties;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.ib.api.IBConfig;
import ru.prolib.aquila.ib.assembler.Assembler;
import ru.prolib.aquila.ib.assembler.IBMainHandler;
import ru.prolib.aquila.ib.assembler.IBOrderProcessor;

/**
 * Фабрика IB-терминала.
 * <p>
 * 2012-11-24<br>
 * $Id: IBFactory.java 527 2013-02-14 15:14:09Z whirlwind $
 */
public class IBFactory implements TerminalFactory {

	public IBFactory() {
		super();
	}
	
	public Terminal createTerminal(Properties cfg) {
		return createTerminal(createConfig(cfg));
	}
	
	public Terminal createTerminal() {
		return createTerminal(new IBConfig());
	}
	
	public Terminal createTerminal(IBConfig config) {
		IBEditableTerminal term = (IBEditableTerminal)new IBTerminalBuilder()
			.createTerminal("IB");
		StarterQueue starter = (StarterQueue) term.getStarter();
		starter.add(new ConnectionHandler(term, config));
		
		term.getClient()
			.setMainHandler(new IBMainHandler(term, new Assembler(term)));
		
		term.setOrderProcessorInstance(new IBOrderProcessor(term));
		
		return term;
	}
	
	private IBConfig createConfig(Properties cfg) {
		return new IBConfig(cfg.getProperty("host"),
				Integer.parseInt(cfg.getProperty("port", "4001")),
				Integer.parseInt(cfg.getProperty("clientId", "0")));
	}

}
