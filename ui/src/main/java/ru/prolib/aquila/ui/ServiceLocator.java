package ru.prolib.aquila.ui;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.springframework.context.ApplicationContext;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.ui.wrapper.MenuBar;

/**
 * Сервис-локатор.
 * <p>
 * 2013-02-28<br>
 * $Id: ServiceLocator.java 554 2013-03-01 13:43:04Z whirlwind $
 */
public class ServiceLocator implements AquilaUI {
	private final EventSystem es;
	private final MessageRegistry texts;
	private final Runnable exitAction;
	private MainFrame frame;
	private ApplicationContext appContext;
	
	public ServiceLocator(MessageRegistry texts, Runnable exitAction) {
		super();
		es = new EventSystemImpl(new EventQueueImpl("AQUILA-UI"));
		this.texts = texts;
		this.exitAction = exitAction;
	}

	@Override
	public EventSystem getEventSystem() {
		return es;
	}
	
	@Override
	public MessageRegistry getTexts() {
		return texts;
	}
	
	@Override
	public Runnable getExitAction() {
		return exitAction;
	}

	@Override
	public void addTab(String title, JComponent component) {
		frame.addTab(title, component);
	}
	
	@Override
	public MenuBar getMainMenu() {
		return frame.getMainMenu();
	}

	@Override
	public JFrame getMainFrame() {
		return frame;
	}

	/**
	 * Установить экземпляр главного окна приложения.
	 * <p>
	 * @param frame окно
	 */
	public void setMainFrame(MainFrame frame) {
		this.frame = frame;
	}

	@Override
	public ApplicationContext getApplicationContext() {
		return appContext;
	}
	
	public void setApplicationContext(ApplicationContext context) {
		this.appContext = context;
	}

}
