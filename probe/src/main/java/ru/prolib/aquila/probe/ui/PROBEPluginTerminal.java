package ru.prolib.aquila.probe.ui;

import java.awt.BorderLayout;
import java.util.*;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.ui.*;
import ru.prolib.aquila.ui.wrapper.*;

@Deprecated // TODO: Have to be rewritten due to probe concept change 
public class PROBEPluginTerminal implements AquilaPluginTerminal,
	EventListener, Runnable
{
	@SuppressWarnings("unused")
	private static final Logger logger;
	private static final String CONNECT = "MENU_CONNECT";
	private static final String DISCONNECT = "MENU_DISCONNECT";
	
	static {
		logger = LoggerFactory.getLogger(PROBEPluginTerminal.class);
	}
	
	private Terminal terminal;
	private IMessages texts;
	private PROBEToolBar toolBar;
	private MenuItem cmdConnect, cmdDisconnect;
	
	public PROBEPluginTerminal() {
		super();
	}
	
	public static MsgID msgID(String messageId) {
		return ProbeMsg.msgID(messageId);
	}

	@Override
	public void createUI(AquilaUI facade) throws Exception {
		texts = facade.getTexts();
		toolBar = new PROBEToolBar(terminal, texts);
		facade.getMainFrame().getContentPane()
			.add(toolBar, BorderLayout.PAGE_START);
		Menu menu = facade.getMainMenu().getMenu(MainFrame.MENU_TERM);
		menu.addBottomSeparator();
		cmdConnect = menu.addBottomItem(CONNECT, texts.get(msgID(CONNECT)));
		cmdDisconnect = menu.addBottomItem(DISCONNECT, texts.get(msgID(DISCONNECT)));
		terminal.OnStarted().addListener(this);
		terminal.OnConnected().addListener(this);
		terminal.OnDisconnected().addListener(this);
		terminal.OnStopped().addListener(this);
		cmdConnect.OnCommand().addListener(this);
		cmdDisconnect.OnCommand().addListener(this);
		refreshControls();
	}

	@Override
	public void initialize(ServiceLocator locator, Terminal terminal, String arg)
			throws Exception
	{
		this.terminal = /*(PROBETerminal)*/ terminal;
	}

	@Override
	public void start() throws StarterException {

	}

	@Override
	public void stop() throws StarterException {

	}

	@Override
	public Terminal createTerminal(Properties props) throws Exception {
		return null;/*new PROBEFactory().createTerminal(props);*/
	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(cmdConnect.OnCommand()) ) {
			//terminal.markTerminalConnected();
			
		} else if ( event.isType(cmdDisconnect.OnCommand()) ) {
			//terminal.markTerminalDisconnected();
			
		} else {
			SwingUtilities.invokeLater(this);
			
		}
	}
	
	@Override
	public void run() {
		refreshControls();
	}
	
	private void setControlsToConnected() {
		//cmdConnect.setEnabled(false);
		//cmdDisconnect.setEnabled(true);
	}
	
	private void setControlsToDisconnected() {
		//cmdConnect.setEnabled(! terminal.finished());
		//cmdDisconnect.setEnabled(false);
	}
	
	private void setControlsToDisabled() {
		//cmdConnect.setEnabled(false);
		//cmdDisconnect.setEnabled(false);
	}
	
	private void refreshControls() {
		if ( terminal.connected() ) {
			setControlsToConnected();
		} else if ( terminal.started() ) {
			// started, but not connected = disconnected
			setControlsToDisconnected();
		} else {
			// else - stopped
			setControlsToDisabled();	
		}
	}
	
}
