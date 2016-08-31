package ru.prolib.aquila.probe;

import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.BusinessEntities.SymbolType;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.ui.AquilaPlugin;
import ru.prolib.aquila.ui.AquilaUI;
import ru.prolib.aquila.ui.ServiceLocator;

@Deprecated
public class TestStrategyPlugin implements AquilaPlugin {
	private TestStrategy strategy;
	
	public TestStrategyPlugin() {
		super();
	}

	@Override
	public void start() throws StarterException {
		
	}

	@Override
	public void stop() throws StarterException {
		
	}

	@Override
	public void createUI(AquilaUI ui) throws Exception {
		
	}

	@Override
	public void initialize(ServiceLocator locator, Terminal terminal, String arg)
			throws Exception
	{
		Symbol symbol = new Symbol("Si-3.15", "SPBFUT", "RUR", SymbolType.FUTURE);
		strategy = new TestStrategy(terminal, symbol);
		strategy.start();
	}

}
