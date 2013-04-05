package ru.prolib.aquila.quik;

import java.util.Properties;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.ui.*;

public class QUIKPluginTerminal implements AquilaPluginTerminal {

	@Override
	public void createUI(AquilaUI facade) {
		
	}

	@Override
	public void initialize(ServiceLocator locator, Terminal terminal) {
		
	}

	@Override
	public void start() {
		
	}

	@Override
	public void stop() {
		
	}

	@Override
	public Terminal createTerminal(Properties props) throws Exception {
		return new QUIKFactory().createTerminal(props);
	}

}
