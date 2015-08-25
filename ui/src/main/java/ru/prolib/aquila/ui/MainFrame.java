package ru.prolib.aquila.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.ui.TerminalStatusBar;
import ru.prolib.aquila.ui.FastOrder.FastOrderPanel;
import ru.prolib.aquila.ui.msg.CommonMsg;
import ru.prolib.aquila.ui.wrapper.Menu;
import ru.prolib.aquila.ui.wrapper.MenuBar;
import ru.prolib.aquila.ui.wrapper.MenuException;
import ru.prolib.aquila.ui.wrapper.MenuItem;

/**
 * Главное окно приложения.
 * <p>
 * 2012-12-07<br>
 * $Id: MainFrame.java 573 2013-03-12 18:18:44Z huan.kaktus $
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame implements EventListener, AquilaPlugin, ActionListener {
	private static Logger logger = LoggerFactory.getLogger(MainFrame.class);

	public static final String MENU_FILE = CommonMsg.MENU_FILE.getMessageId();
	public static final String MENU_TERM = CommonMsg.MENU_TERM.getMessageId();
	public static final String MENU_VIEW = CommonMsg.MENU_VIEW.getMessageId();
	public static final String MENU_VIEW_PORTFOLIO_STATUS = "MENU_VIEW_PORTFOLIO_STATUS";
	public static final String MENU_FILE_EXIT = CommonMsg.MENU_FILE_EXIT.getMessageId();
	public static final String MENU_TERM_START = CommonMsg.MENU_TERM_START.getMessageId();
	public static final String MENU_TERM_STOP = CommonMsg.MENU_TERM_STOP.getMessageId();

	private Runnable exitAction;
	private Terminal terminal;
	private MenuBar mainMenu;
	private JMenuItem exit;
	private MenuItem cmdStart, cmdStop;
	private FastOrderPanel fastOrder;
	private JPanel statusBar = new JPanel();
	
	private TerminalStatusBar terminalStatusBar;
	private PortfolioStatusBar portfolioStatusBar;
	private CurrentPortfolio portfolioSelector;
	
	private final JTabbedPane tabPanel = new JTabbedPane();	
	
	public MainFrame() {
		super();
	}
	
	@Override
	public void createUI(final AquilaUI facade) throws Exception {
		exitAction = facade.getExitAction();
		addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent e) { exit(); }
		});
		createMainMenu(facade);		
		
		EventSystem es = facade.getEventSystem();
 
		IMessages messages = facade.getTexts();
		
		EventDispatcher dispatcher = es.createEventDispatcher(); 
		portfolioSelector = new CurrentPortfolioImpl(
				terminal, dispatcher.createType(), dispatcher,
				mainMenu.getMenu(MENU_VIEW).getSubMenu(MENU_VIEW_PORTFOLIO_STATUS));
		
        setSize(800, 600);
        
        JPanel inp = new JPanel();
        inp.setLayout(new BorderLayout());
        fastOrder = new FastOrderPanel(terminal);
        inp.add(fastOrder, BorderLayout.NORTH);
        inp.add(tabPanel, BorderLayout.CENTER);
        getContentPane().add(inp, BorderLayout.CENTER);
        
        terminalStatusBar = new TerminalStatusBar(messages);
		portfolioStatusBar = new PortfolioStatusBar(messages);
        
        statusBar.setLayout(new FlowLayout());
        statusBar.add(terminalStatusBar);
        statusBar.add(portfolioStatusBar);
        getContentPane().add(statusBar, BorderLayout.SOUTH);
        
        terminal.OnStarted().addListener(this);
        terminal.OnStopped().addListener(this);
		terminal.OnConnected().addListener(this);
        terminal.OnDisconnected().addListener(this);
        terminal.OnPortfolioAvailable().addListener(this);
        terminal.OnPortfolioChanged().addListener(this);
        portfolioSelector.OnCurrentPortfolioChanged().addListener(this);
	}
	
	public void startTerminal() {
		try {
			if( terminal.stopped() ) {
				terminal.start();
			}
		} catch (StarterException e) {
			logger.error("Terminal start failed: ", e);
		}
	}
	
	public void stopTerminal() {
		try {
			if( terminal.started() ) {
				terminal.stop();
			}
		} catch (StarterException e) {
			logger.error("Terminal stop failed: ", e);
		}
	}

	@Override
	public void onEvent(final Event event) {		
		if ( event.isType(terminal.OnStarted()) ) {
			cmdStart.setEnabled(false);
			cmdStop.setEnabled(true);
			terminalStatusBar.updateTerminalStatus(terminal);
			
		} else if ( event.isType(terminal.OnStopped()) ) {
			cmdStart.setEnabled(true);
			cmdStop.setEnabled(false);
			terminalStatusBar.updateTerminalStatus(terminal);
			
		} else if ( event.isType(terminal.OnConnected()) ) {
			terminalStatusBar.updateTerminalStatus(terminal);
			
		} else if ( event.isType(terminal.OnDisconnected()) ) {
			terminalStatusBar.updateTerminalStatus(terminal);
			
		} else if ( event.isType(cmdStart.OnCommand()) ) {
			startTerminal();
			
		} else if ( event.isType(cmdStop.OnCommand()) ) {
			stopTerminal();
			
		} else if ( event.isType(terminal.OnPortfolioAvailable())
				|| event.isType(terminal.OnPortfolioChanged())
				|| event.isType(portfolioSelector.OnCurrentPortfolioChanged()) )
		{
			Portfolio p = portfolioSelector.getCurrentPortfolio();
			if ( p == ((PortfolioEvent)event).getPortfolio() ) {
				portfolioStatusBar.updateDisplayData(p);
			}
			
		} else {
			event.getType().removeListener(this);
			logger.debug("Unknown event: {}", event);
		}
	}
	
	/**
	 * Создать меню с базовыми пунктами.
	 * <p>
	 * @param facade фасад UI
	 * @throws MenuException
	 */
	private void createMainMenu(AquilaUI facade) throws MenuException {
		mainMenu = new MenuBar(facade.getEventSystem());
		setJMenuBar(mainMenu.getUnderlyingObject());
		
		IMessages messages = facade.getTexts();
		Menu menu = mainMenu.addMenu(MENU_FILE, messages.get(CommonMsg.MENU_FILE));
		menu.addBottomSeparator();
		exit = menu.addBottomItem(MENU_FILE_EXIT, messages.get(CommonMsg.MENU_FILE_EXIT))
				.getUnderlyingObject();
		exit.setActionCommand(MENU_FILE_EXIT);
		exit.addActionListener(this);
		
		menu = mainMenu.addMenu(MENU_TERM, messages.get(CommonMsg.MENU_TERM));
		cmdStart = menu.addItem(MENU_TERM_START, messages.get(CommonMsg.MENU_TERM_START));
		cmdStart.OnCommand().addListener(this);
		cmdStop = menu.addItem(MENU_TERM_STOP, messages.get(CommonMsg.MENU_TERM_STOP));
		cmdStop.OnCommand().addListener(this);
		
		menu = mainMenu.addMenu(MENU_VIEW, messages.get(CommonMsg.MENU_VIEW));
		menu.addSubMenu(MENU_VIEW_PORTFOLIO_STATUS, messages.get(CommonMsg.MENU_VIEW_PORTFOLIO_STATUS));
	}
	
	private void exit() {
		exitAction.run();
	}
	
	@Override
	public void
		initialize(ServiceLocator locator, Terminal terminal, String arg)
	{
		this.terminal = terminal;
	}

	@Override
	public void start() throws StarterException {
		setVisible(true);
		portfolioSelector.start();
		fastOrder.start();
		startTerminal();
	}

	@Override
	public void stop() throws StarterException {
		stopTerminal();
		fastOrder.stop();
		portfolioSelector.stop();
		dispose();
	}
	
	public CurrentPortfolio getCurrPortfolio() {
		return portfolioSelector;
	}
	
	public JTabbedPane getTabPanel() {
		return tabPanel;
	}

	/**
	 * Добавить вкладку.
	 * <p>
	 * @param title заголовок
	 * @param component компонент
	 */
	public void addTab(String title, JComponent component) {
		tabPanel.add(title, component);
	}
	
	/**
	 * Получить главное меню.
	 * <p>
	 * @return главное меню
	 */
	public MenuBar getMainMenu() {
		return mainMenu;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ( MENU_FILE_EXIT.equals(e.getActionCommand()) ) {
			exit();
		} 
	}

}
