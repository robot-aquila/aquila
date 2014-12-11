package ru.prolib.aquila.probe.ui;

import java.awt.BorderLayout;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.probe.PROBEFactory;
import ru.prolib.aquila.probe.PROBETerminal;
import ru.prolib.aquila.ui.*;
import ru.prolib.aquila.ui.wrapper.MenuException;

public class PROBEPluginTerminal implements AquilaPluginTerminal, EventListener {
	private static final Logger logger;
	private static final String TEXT_SECTION = "Probe";
	
	static {
		logger = LoggerFactory.getLogger(PROBEPluginTerminal.class);
	}
	
	private PROBETerminal terminal;
	private ClassLabels labels;
	private AquilaUI facade;
	private PROBEToolBar simCtrlToolBar;
	
	public PROBEPluginTerminal() {
		super();
	}

	@Override
	public void createUI(AquilaUI facade) throws Exception {
		if ( terminal == null ) {
			logger.error("Terminal instance not available.");
			logger.error("Additional functionality has been disabled.");
			return;
		}
		this.facade = facade;
		labels = facade.getTexts().get(TEXT_SECTION);
		simCtrlToolBar = new PROBEToolBar(terminal, labels);
		facade.getMainFrame().getContentPane()
			.add(simCtrlToolBar, BorderLayout.PAGE_START);
	}

	@Override
	public void initialize(ServiceLocator locator, Terminal terminal, String arg)
			throws Exception
	{
		if ( terminal.getClass() == PROBETerminal.class ) {
			this.terminal = (PROBETerminal) terminal;
		} else {
			logger.error("Unexpected terminal type: " + terminal.getClass());
		}
	}

	@Override
	public void start() throws StarterException {

	}

	@Override
	public void stop() throws StarterException {

	}

	@Override
	public Terminal createTerminal(Properties props) throws Exception {
		return new PROBEFactory().createTerminal(props);
	}

	@Override
	public void onEvent(Event event) {
		// TODO Auto-generated method stub
		
	}

}
