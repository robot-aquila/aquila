package ru.prolib.aquila.ui;

import javax.swing.*;

import ru.prolib.aquila.core.EventSystem;
import ru.prolib.aquila.ui.wrapper.MenuBar;

/**
 * Интерфейс фасада UI.
 * <p>
 * 2013-02-28<br>
 * $Id: AquilaUI.java 554 2013-03-01 13:43:04Z whirlwind $
 */
public interface AquilaUI {
	
	/**
	 * Получить фасад событийной системы.
	 * <p>
	 * @return фасад системы событий
	 */
	public EventSystem getEventSystem();
	
	/**
	 * Добавить вкладку.
	 * <p>
	 * @param title заголовок вкладки
	 * @param component вкладка
	 */
	public void addTab(String title, JComponent component);
	
	/**
	 * Получить главное меню.
	 * <p>
	 * @return главное меню
	 */
	public MenuBar getMainMenu();
	
	/**
	 * Получить главное окно приложение.
	 * <p>
	 * @return главное окно
	 */
	public JFrame getMainFrame();
	
	public UiTexts getTexts();
	
	public Runnable getExitAction();

}
