package ru.prolib.aquila.probe;

import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.BusinessEntities.SecurityDescriptor;
import ru.prolib.aquila.core.BusinessEntities.SecurityType;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.ui.AquilaPlugin;
import ru.prolib.aquila.ui.AquilaUI;
import ru.prolib.aquila.ui.ServiceLocator;

public class TestStrategyPlugin implements AquilaPlugin {
	private final TestStrategy strategy;
	
	public TestStrategyPlugin() {
		super();
		SecurityDescriptor descr = new SecurityDescriptor("Si-12.14",
				"SPBFUT", "USD", SecurityType.FUT);
		strategy = new TestStrategy(descr);
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
		terminal.subscribe(strategy);
	}

}
