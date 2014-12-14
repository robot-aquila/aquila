package ru.prolib.aquila.probe.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.probe.PROBETerminal;

@SuppressWarnings("serial")
public class PROBEToolBar extends JToolBar
	implements ActionListener, EventListener
{
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(PROBEToolBar.class);
	}
	
	private static final String ICON_PREFIX = "shared/images/probe_";
	private static final String ICON_SUFFIX = ".png";
	private static final String NOT_IMPLEMENTED = "NOT_IMPLEMENTED";
	private static final String NAME = "TOOLBAR_NAME";
	private static final String OPTIONS = "options";
	private static final String RUN_TO = "runto";
	private static final String STEP = "step";
	private static final String RUN_RT = "runrt";
	private static final String RUN_ALL = "runall";
	private static final String PAUSE = "pause";
	private static final String FINISH = "finish";
	private static final String TTIP_OPTIONS = "TTIP_OPTIONS";
	private static final String TTIP_RUN_TO = "TTIP_RUNTO";
	private static final String TTIP_STEP = "TTIP_STEP";
	private static final String TTIP_RUN_RT = "TTIP_RUNRT";
	private static final String TTIP_RUN_ALL = "TTIP_RUNALL";
	private static final String TTIP_PAUSE = "TTIP_PAUSE";
	private static final String TTIP_FINISH = "TTIP_FINISH";

	private final PROBETerminal terminal;
	private final IMessages messages;
	private final JButton btnOptions;
	private final JButton btnRunTo;
	private final JButton btnStep;
	private final JButton btnPauseRunRt;
	private final JButton btnRunAll;
	private final JButton btnFinish;
	private final Icon iconPause, iconRunRt;
	private final SelectTargetTimeDialogView targetTimeDialog;
	
	public PROBEToolBar(PROBETerminal terminal, IMessages messages) {
		super();
		this.terminal = terminal;
		this.messages = messages;
		targetTimeDialog = new SelectTargetTimeDialog(messages);
		setName(messages.get(NAME));
		btnOptions = makeButton(OPTIONS, TTIP_OPTIONS);
		btnRunTo = makeButton(RUN_TO, TTIP_RUN_TO);
		btnStep = makeButton(STEP, TTIP_STEP);
		btnPauseRunRt = makeButton(RUN_RT, TTIP_RUN_RT);
		btnRunAll = makeButton(RUN_ALL, TTIP_RUN_ALL);
		btnFinish = makeButton(FINISH, TTIP_FINISH);
		iconPause = getIcon(PAUSE);
		iconRunRt = btnPauseRunRt.getIcon();
		disableAllButtons();
		terminal.OnFinish().addListener(this);
		terminal.OnPause().addListener(this);
		terminal.OnRun().addListener(this);
		terminal.OnConnected().addListener(this);
		terminal.OnDisconnected().addListener(this);
	}
	
	private JButton makeButton(String actionId,
			String toolTipId)
	{
		JButton button = new JButton();
		button.setActionCommand(actionId);
		button.setToolTipText(messages.get(toolTipId));
		button.setIcon(getIcon(actionId));
		button.addActionListener(this);
		add(button);
		return button;
	}
	
	private Icon getIcon(String actionId) {
		return new ImageIcon(ICON_PREFIX + actionId + ICON_SUFFIX);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand(); 
		if ( cmd.equals(RUN_RT) ) {
			popUpNotImplemented();
			
		} else if ( cmd.equals(PAUSE) ) {
			terminal.pause();
			
		} else if ( cmd.equals(RUN_TO) ) {
			DateTime x = targetTimeDialog.showDialog(terminal.getCurrentTime());
			if ( x != null ) {
				terminal.runTo(x);
			}
			
		} else if ( cmd.equals(STEP) ) {
			popUpNotImplemented();
			
		} else if ( cmd.equals(RUN_ALL) ) {
			terminal.run();
			
		} else if ( cmd.equals(FINISH) ) {
			// TODO: 
			// Не срабатывает. После этого вызова не приходит событие OnFinish.
			terminal.finish();
			
		}
	}

	@Override
	public void onEvent(Event event) {
		if ( event.isType(terminal.OnFinish()) ) {
			terminal.OnFinish().removeListener(this);
			terminal.OnPause().removeListener(this);
			terminal.OnRun().removeListener(this);
			terminal.OnConnected().removeListener(this);
			terminal.OnDisconnected().removeListener(this);
			disableAllButtons();
			logger.debug("The timeline ended and controls has been permanently disabled.");
			
		} else if ( event.isType(terminal.OnPause())
				 || event.isType(terminal.OnConnected()))
		{
			switchToPausedMode();
			
		} else if ( event.isType(terminal.OnRun()) ) {
			switchToRunningMode();
			
		} else if ( event.isType(terminal.OnDisconnected()) ) {
			disableAllButtons();

		}
	}
	
	private void switchToPausedMode() {
		enableAllButtons();
		switchToRunRTButton();
	}
	
	private void switchToRunRTButton() {
		btnPauseRunRt.setIcon(iconRunRt);
		btnPauseRunRt.setToolTipText(messages.get(TTIP_RUN_RT));
		btnPauseRunRt.setActionCommand(RUN_RT);
	}
	
	private void switchToPauseButton() {
		btnPauseRunRt.setIcon(iconPause);
		btnPauseRunRt.setToolTipText(messages.get(TTIP_PAUSE));
		btnPauseRunRt.setActionCommand(PAUSE);
	}
	
	private void switchToRunningMode() {
		enableAllButtons();
		btnRunTo.setEnabled(false);
		btnStep.setEnabled(false);
		btnRunAll.setEnabled(false);
		switchToPauseButton();
	}
	
	private void popUpNotImplemented() {
		JOptionPane.showMessageDialog(this, messages.get(NOT_IMPLEMENTED));
	}
	
	private void setEnabledAllButtons(boolean enable) {
		btnOptions.setEnabled(enable);
		btnRunTo.setEnabled(enable);
		btnStep.setEnabled(enable);
		btnPauseRunRt.setEnabled(enable);
		btnRunAll.setEnabled(enable);
		btnFinish.setEnabled(enable);
	}

	public void enableAllButtons() {
		setEnabledAllButtons(true);
	}

	public void disableAllButtons() {
		setEnabledAllButtons(false);
	}

}
