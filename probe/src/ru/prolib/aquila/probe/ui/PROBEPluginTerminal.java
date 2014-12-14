package ru.prolib.aquila.probe.ui;

import java.awt.BorderLayout;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.probe.PROBEFactory;
import ru.prolib.aquila.probe.PROBETerminal;
import ru.prolib.aquila.ui.*;
import ru.prolib.aquila.ui.wrapper.*;

public class PROBEPluginTerminal implements AquilaPluginTerminal, EventListener {
	private static final Logger logger;
	private static final String TEXT_SECTION = "Probe";
	private static final String CONNECT = "MENU_CONNECT";
	private static final String DISCONNECT = "MENU_DISCONNECT";
	
	static {
		logger = LoggerFactory.getLogger(PROBEPluginTerminal.class);
	}
	
	private PROBETerminal terminal;
	private ClassLabels labels;
	private PROBEToolBar simCtrlToolBar;
	private MenuItem cmdConnect, cmdDisconnect;
	
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
		labels = facade.getTexts().get(TEXT_SECTION);
		simCtrlToolBar = new PROBEToolBar(terminal, labels);
		facade.getMainFrame().getContentPane()
			.add(simCtrlToolBar, BorderLayout.PAGE_START);
		Menu menu = facade.getMainMenu().getMenu(MainFrame.MENU_TERM);
		menu.addBottomSeparator();
		cmdConnect = menu.addBottomItem(CONNECT, labels.get(CONNECT));
		cmdDisconnect = menu.addBottomItem(DISCONNECT, labels.get(DISCONNECT));
		setControlsToDisabled();
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
		terminal.OnStarted().addListener(this);
		terminal.OnConnected().addListener(this);
		terminal.OnDisconnected().addListener(this);
		terminal.OnStopped().addListener(this);
		cmdConnect.OnCommand().addListener(this);
		cmdDisconnect.OnCommand().addListener(this);
	}

	@Override
	public void stop() throws StarterException {
		terminal.OnStarted().removeListener(this);
		terminal.OnConnected().removeListener(this);
		terminal.OnDisconnected().removeListener(this);
		terminal.OnStopped().removeListener(this);
		cmdConnect.OnCommand().removeListener(this);
		cmdDisconnect.OnCommand().removeListener(this);
	}

	@Override
	public Terminal createTerminal(Properties props) throws Exception {
		return new PROBEFactory().createTerminal(props);
	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(cmdConnect.OnCommand()) ) {
			terminal.markTerminalConnected();
			
		} else if ( event.isType(cmdDisconnect.OnCommand()) ) {
			terminal.markTerminalDisconnected();
			
		} else if ( event.isType(terminal.OnStarted())
			     || event.isType(terminal.OnDisconnected()) )
		{
			setControlsToDisconnected();
			
		} else if ( event.isType(terminal.OnConnected()) ) {
			setControlsToConnected();

		} else if ( event.isType(terminal.OnStopped()) ) {
			setControlsToDisabled();
			
		}
	}
	
	private void setControlsToConnected() {
		cmdConnect.setEnabled(false);
		cmdDisconnect.setEnabled(true);
	}
	
	private void setControlsToDisconnected() {
		cmdConnect.setEnabled(! terminal.finished());
		cmdDisconnect.setEnabled(false);
	}
	
	private void setControlsToDisabled() {
		cmdConnect.setEnabled(false);
		cmdDisconnect.setEnabled(false);
	}

}
