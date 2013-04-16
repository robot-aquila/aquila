package ru.prolib.aquila.ui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.ui.StatusBar;
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
public class MainFrame extends JFrame implements EventListener, AquilaPlugin {
	private static final long serialVersionUID = -3728478826451820673L;
	private static Logger logger = LoggerFactory.getLogger(MainFrame.class);

	public static final String MENU_SECT = "MenuBar";
	public static final String MENU_FILE = "MENU_FILE";
	public static final String MENU_TERM = "MENU_TERM";
	public static final String MENU_VIEW = "MENU_VIEW";
	public static final String MENU_VIEW_PORTFOLIO_STATUS = "MENU_VIEW_PORTFOLIO_STATUS";
	public static final String MENU_FILE_EXIT = "MENU_FILE_EXIT";
	public static final String MENU_TERM_START = "MENU_TERM_START";
	public static final String MENU_TERM_STOP = "MENU_TERM_STOP";

	private Runnable exitAction;
	private Terminal terminal;
	private MenuBar mainMenu;
	private EventType onExit;
	private MenuItem cmdStart, cmdStop;
	
	private StatusBar status;
	private CurrentPortfolio currPortfolio;
	
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
 
		UiTexts uiLabels = facade.getTexts();
		
		EventDispatcher dispatcher = es.createEventDispatcher(); 
		currPortfolio = new CurrentPortfolioImpl(
				(Portfolios) terminal, es.createGenericType(dispatcher), dispatcher,
				mainMenu.getMenu(MENU_VIEW).getSubMenu(MENU_VIEW_PORTFOLIO_STATUS));
		
		PortfolioDataPanel portfolioBox = new PortfolioDataPanel(
				currPortfolio, uiLabels);
		
        setSize(800, 600);
        
        getContentPane().add(tabPanel, BorderLayout.CENTER);
        
        status = new StatusBar(portfolioBox, terminal, uiLabels);        
        getContentPane().add(status, BorderLayout.SOUTH);
        terminal.OnStarted().addListener(this);
        terminal.OnStopped().addListener(this);
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
			
		} else if ( event.isType(terminal.OnStopped()) ) {
			cmdStart.setEnabled(true);
			cmdStop.setEnabled(false);
			
		} else if ( event.isType(onExit) ) {
			exit();
			
		} else if ( event.isType(cmdStart.OnCommand()) ) {
			startTerminal();
			
		} else if ( event.isType(cmdStop.OnCommand()) ) {
			stopTerminal();
			
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
		ClassLabels text = facade.getTexts().get(MENU_SECT);
		
		Menu menu = mainMenu.addMenu(MENU_FILE, text.get(MENU_FILE));
		menu.addBottomSeparator();
		onExit = menu.addBottomItem(MENU_FILE_EXIT,
				text.get(MENU_FILE_EXIT)).OnCommand();
		onExit.addListener(this);
		
		menu = mainMenu.addMenu(MENU_TERM, text.get(MENU_TERM));
		cmdStart = menu.addItem(MENU_TERM_START, text.get(MENU_TERM_START));
		cmdStart.OnCommand().addListener(this);
		cmdStop = menu.addItem(MENU_TERM_STOP, text.get(MENU_TERM_STOP));
		cmdStop.OnCommand().addListener(this);
		
		menu = mainMenu.addMenu(MENU_VIEW, text.get(MENU_VIEW));
		menu.addSubMenu(MENU_VIEW_PORTFOLIO_STATUS, text.get(MENU_VIEW_PORTFOLIO_STATUS));
	}
	
	private void exit() {
		exitAction.run();
	}
	
	@Override
	public void initialize(ServiceLocator locator, Terminal terminal) {
		this.terminal = terminal;
	}

	@Override
	public void start() throws StarterException {
		setVisible(true);
		status.start();
		startTerminal();
	}

	@Override
	public void stop() throws StarterException {
		stopTerminal();
		status.stop();
		dispose();
	}
	
	public Terminal getTerminal() {
		return terminal;
	}
	
	public void setTerminal(Terminal term) {
		terminal = term;
	}
	
	public void setStatus(StatusBar status) {
		this.status = status;
	}
	
	public StatusBar getStatus() {
		return status;
	}
	
	public CurrentPortfolio getCurrPortfolio() {
		return currPortfolio;
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

}
