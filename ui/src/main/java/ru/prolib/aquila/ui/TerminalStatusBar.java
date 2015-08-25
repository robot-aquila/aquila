package ru.prolib.aquila.ui;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.ui.msg.CommonMsg;

/**
 * $Id: StatusBar.java 570 2013-03-12 00:03:15Z huan.kaktus $
 */
@SuppressWarnings("serial")
public class TerminalStatusBar extends JPanel implements TerminalStatusBarView {
	private final JLabel startedStatus = new JLabel();
	private final JLabel connectedStatus = new JLabel();
	private final ImageIcon red = new ImageIcon("shared/images/r.png");
	private final ImageIcon green = new ImageIcon("shared/images/g.png");
	private final IMessages messages;
	
	public TerminalStatusBar(IMessages messages) {
		super(new GridBagLayout());
		this.messages = messages; 
		setBorder(new EmptyBorder(1, 5, 1, 5));
		GridBagConstraints constr = new GridBagConstraints();
		constr.insets = new Insets(1, 5, 1, 0);
		constr.anchor = GridBagConstraints.WEST;
		add(startedStatus, constr);
		add(connectedStatus, constr);
		setDisconnected();
		setStopped();
	}

	private void setStarted() {
		startedStatus.setIcon(green);
		startedStatus.setToolTipText(messages.get(CommonMsg.STARTED));
	}
	
	private void setStopped() {
		startedStatus.setIcon(red);
		startedStatus.setToolTipText(messages.get(CommonMsg.STOPPED));
	}
	
	private void setDisconnected() {		
		connectedStatus.setIcon(red);
		connectedStatus.setToolTipText(messages.get(CommonMsg.DISCONNECTED));
	}
	
	private void setConnected() {
		connectedStatus.setIcon(green);
		connectedStatus.setToolTipText(messages.get(CommonMsg.CONNECTED));
	}

	@Override
	public void updateTerminalStatus(final Terminal terminal) {
		if ( SwingUtilities.isEventDispatchThread() ) {
			if ( terminal.connected() ) {
				setConnected();
			} else {
				setDisconnected();
			}
			if ( terminal.started() ) {
				setStarted();
			} else {
				setStopped();
			}			
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override public void run() {
					updateTerminalStatus(terminal);
				}
			});
		}
	}

}
