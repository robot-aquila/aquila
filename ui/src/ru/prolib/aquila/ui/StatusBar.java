package ru.prolib.aquila.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.Starter;
import ru.prolib.aquila.core.StarterException;
//import ru.prolib.aquila.core.StarterException;
import ru.prolib.aquila.core.BusinessEntities.Terminal;

public class StatusBar extends JPanel implements EventListener, Starter {

	/**
	 * $Id: StatusBar.java 570 2013-03-12 00:03:15Z huan.kaktus $
	 */
	private static final long serialVersionUID = -6703751456559551519L;
	//private static Logger logger = LoggerFactory.getLogger(StatusBar.class);
	
	private JLabel tConn = new JLabel();
	private String disconnected = "LABEL_DISCONNECTED";	
	private String connected = "LABEL_CONNECTED";
	
	private JLabel tStart = new JLabel();
	private String started = "LABEL_STARTED";
	private String stopped = "LABEL_STOPPED";
	
	private ImageIcon red = new ImageIcon("shared/images/r.png");
	private ImageIcon green = new ImageIcon("shared/images/g.png");
	
	private Terminal terminal;
	private ClassLabels uiLabels;
	private PortfolioDataPanel prtPanel;
	
	public StatusBar(PortfolioDataPanel prtPanel, Terminal terminal, UiTexts texts) {
		super(new GridBagLayout());
		uiLabels = texts.get("StatusBar");
		
		setBorder(new EmptyBorder(1, 5, 1, 5));
		this.terminal = terminal;
		this.prtPanel = prtPanel;
		/*
		tStart.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				startStopTerminal();
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		*/
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(1, 5, 1, 0);
		c.anchor = GridBagConstraints.WEST;
		add(tStart, c);
		add(tConn, c);
		c = new GridBagConstraints();
		c.weightx = 1.0f;
		c.anchor = GridBagConstraints.EAST;
		add(this.prtPanel, c);
		setDisconnected();
		setStopped();
	}
	
	public PortfolioDataPanel getPortfolioPanel() {
		return prtPanel;
	}
	
	ImageIcon getRedIcon() {
		return red;
	}
	
	ImageIcon getGreenIcon() {
		return green;
	}
	
	JLabel getTStart() {
		return tStart;
	}
	
	
	JLabel getTConn() {
		return tConn;
	}
	Terminal getTerminal() {
		return terminal;
	}
	
	ClassLabels getUiLabels() {
		return uiLabels;
	}

	@Override
	public void onEvent(Event event) {
		if( event.isType(terminal.OnConnected())) {
			setConnected();
		}else if( event.isType(terminal.OnDisconnected())) {
			setDisconnected();
		}else if( event.isType(terminal.OnStarted())) {
			setStarted();
		}else if( event.isType(terminal.OnStopped())) {
			setStopped();
		}
	}
	/*
	private void startStopTerminal()  {
		try {
			if(terminal.started()) {
				terminal.stop();
			} else if(terminal.stopped()) {
				terminal.start();
			}
		}catch (StarterException e) {
			logger.error(" Terminal start/stop failed: ",e);
		}
	}
	*/
	private void setStarted() {
		tStart.setIcon(green);
		tStart.setToolTipText(uiLabels.get(started));
	}
	
	private void setStopped() {
		tStart.setIcon(red);
		tStart.setToolTipText(uiLabels.get(stopped));
	}
	
	private void setDisconnected() {		
		tConn.setIcon(red);
		tConn.setToolTipText(uiLabels.get(disconnected));
	}
	
	private void setConnected() {
		tConn.setIcon(green);
		tConn.setToolTipText(uiLabels.get(connected));
	}

	@Override
	public void start() throws StarterException {
		prtPanel.start();
		terminal.OnConnected().addListener(this);
        terminal.OnDisconnected().addListener(this);
        terminal.OnStarted().addListener(this);
        terminal.OnStopped().addListener(this);
	}

	@Override
	public void stop() throws StarterException {
		terminal.OnConnected().removeListener(this);
        terminal.OnDisconnected().removeListener(this);
        terminal.OnStarted().removeListener(this);
        terminal.OnStopped().removeListener(this);
        prtPanel.stop();
		
	}

}
