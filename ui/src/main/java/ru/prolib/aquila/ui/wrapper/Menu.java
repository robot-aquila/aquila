package ru.prolib.aquila.ui.wrapper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import ru.prolib.aquila.core.*;


/**
 * Конструктор/обертка меню.
 * <p>
 * 2013-03-01<br>
 * $Id: Menu.java 554 2013-03-01 13:43:04Z whirlwind $
 */
public class Menu {
	private final JMenu underlying;
	private final EventSystem es;
	private final EventDispatcher dispatcher;
	/**
	 * Количество элементов приклеенных внизу меню.
	 */
	private int bottomElements = 0;
	/**
	 * Карта соответствия идентификатор -> подменю.
	 */
	private final Map<String, Menu> childMenu;
	/**
	 * Карта соответствия идентификатор -> элемент меню.
	 */
	private final Map<String, MenuItem> childItem;
	
	public Menu(JMenu underlying, EventSystem es) {
		super();
		this.underlying = underlying;
		this.es = es;
		dispatcher = es.createEventDispatcher("MENU");
		childMenu = new HashMap<String, Menu>();
		childItem = new HashMap<String, MenuItem>();
	}
	
	/**
	 * Добавить элемент меню.
	 * <p>
	 * Данный метод добавляет элемент меню в конец нормальной последовательности
	 * элементов перед первым элементом, приклеенным к низу меню.
	 * <p>
	 * @param id идентификатор элемента
	 * @param title заголовок
	 * @return элемент меню 
	 * @throws MenuItemAlreadyExsistException идентификатор не уникален
	 */
	public synchronized MenuItem addItem(String id, String title) throws MenuException
	{
		MenuItem item = createItem(id, title, new JMenuItem());
		underlying.insert(item.getUnderlyingObject(), getNormalPosition());
		return item;
	}
	
	/**
	 * Добавить элемент меню.
	 * <p>
	 * Данный метод добавляет элемент меню в конец нормальной последовательности
	 * элементов перед первым элементом, приклеенным к низу меню.
	 * <p>
	 * @param id идентификатор элемента
	 * @param title заголовок
	 * @param underlyed оборачиваемый элемент меню
	 * @return элемент меню
	 * @throws MenuItemAlreadyExsistException идентификатор не уникален
	 */
	public synchronized MenuItem addItem(String id, String title, JMenuItem underlyed)
		throws MenuException
	{
		MenuItem item = createItem(id, title, underlyed);
		underlying.insert(item.getUnderlyingObject(), getNormalPosition());
		return item;
	}
	
	/**
	 * Добавить субменю.
	 * <p>
	 * Данный метод добавляет субменю в конец нормальной последовательности
	 * элементов перед первым элементом, приклеенным к низу меню.
	 * <p>
	 * @param id идентификатор субменю
	 * @param title заголовок
	 * @return субменю
	 * @throws MenuItemAlreadyExistsException идентификатор не уникален
	 */
	public synchronized Menu addSubMenu(String id, String title)
		throws MenuException
	{
		Menu menu = createMenu(id, title);
		underlying.insert(menu.getUnderlyingObject(), getNormalPosition());
		return menu;
	}
	
	/**
	 * Добавить сепаратор.
	 * <p>
	 * Добавляет сепаратор в конец нормально последовательности элементов
	 * перед первым элементом, приклеенным к низу меню.
	 * <p>
	 */
	public synchronized void addSeparator() {
		underlying.insertSeparator(getNormalPosition());
	}
	
	/**
	 * Добавить элемент меню в конец меню с привязкой.
	 * <p>
	 * Данный метод добавляет элемент с привязкой к низу меню. Элемент будет
	 * вставлен в самый конец меню и будет находиться на этой позиции до тех
	 * пор, пока добавление нового элемента в конец нормальной
	 * последовательности с помощью одного из методов
	 * {@link #addItem(String, String)}, {@link #addSubMenu(String, String)}
	 * или {@link #addSeparator()} не сместит его на позицию ниже. 
	 * <p>
	 * @param id идентификатор элемента
	 * @param title заголовок
	 * @return элемент меню
	 * @throws MenuItemAlreadyExsistException идентификатор не уникален
	 */
	public synchronized MenuItem addBottomItem(String id, String title) throws MenuException
	{
		MenuItem item = createItem(id, title, new JMenuItem());
		underlying.add(item.getUnderlyingObject());
		bottomElements ++;
		return item;
	}

	/**
	 * Добавить элемент меню в конец меню с привязкой.
	 * <p>
	 * Данный метод добавляет элемент с привязкой к низу меню. Элемент будет
	 * вставлен в самый конец меню и будет находиться на этой позиции до тех
	 * пор, пока добавление нового элемента в конец нормальной
	 * последовательности с помощью одного из методов
	 * {@link #addItem(String, String)}, {@link #addSubMenu(String, String)}
	 * или {@link #addSeparator()} не сместит его на позицию ниже. 
	 * <p>
	 * @param id идентификатор элемента
	 * @param title заголовок
	 * @param underlyed оборачиваемый элемент меню
	 * @return элемент меню
	 * @throws MenuException
	 */
	public synchronized MenuItem addBottomItem(String id, String title, JMenuItem underlyed)
			throws MenuException
	{
		MenuItem item = createItem(id, title, underlyed);
		underlying.add(item.getUnderlyingObject());
		bottomElements ++;
		return item;
	}

	
	/**
	 * Добавить субменю в конец меню с привязкой.
	 * <p>
	 * Данный метод добавляет субменю с привязкой к низу меню. Позиция
	 * добавляемого элемента ведет себя аналогично элементу, добавленному
	 * посредством метода {@link #addBottomItem(String, String)}.
	 * <p>
	 * @param id идентификатор субменю
	 * @param title заголовок
	 * @return элемент меню
	 * @throws MenuItemAlreadyExsistException идентификатор не уникален
	 */
	public synchronized Menu addBottomSubMenu(String id, String title)
		throws MenuException
	{
		Menu menu = createMenu(id, title);
		underlying.add(menu.getUnderlyingObject());
		bottomElements ++;
		return menu;
	}
	
	/**
	 * Добавить разделитель в конец меню с привязкой.
	 * <p>
	 * Данный метод добавляет разделитель с привязкой к низу меню. Позиция
	 * добавляемого элемента ведет себя аналогично элементу, добавленному
	 * посредством метода {@link #addBottomItem(String, String)}.
	 */
	public synchronized void addBottomSeparator() {
		underlying.addSeparator();
		bottomElements ++;
	}
	
	/**
	 * Получить элемент меню по идентификатору.
	 * <p>
	 * @param id идентификатор элемента
	 * @return экземпляр элемента меню
	 * @throws MenuException элемент не найден
	 */
	public synchronized MenuItem getItem(String id) throws MenuException {
		MenuItem item = childItem.get(id);
		if ( item == null ) {
			throw new MenuItemNotExistsException(id);
		}
		return item;
	}
	
	/**
	 * Получить субменю по идентификатору.
	 * <p>
	 * @param id идентификатор субменю
	 * @return экземпляр субменю
	 * @throws MenuException элемент не найден
	 */
	public synchronized Menu getSubMenu(String id) throws MenuException {
		Menu menu = childMenu.get(id);
		if ( menu == null ) {
			throw new MenuItemNotExistsException(id);
		}
		return menu;
	}
	
	/**
	 * Получить SWING экземпляр меню.
	 * <p>
	 * @return экземпляр меню
	 */
	public JMenu getUnderlyingObject() {
		return underlying;
	}
	
	/**
	 * Проверить наличие элемента с указанным идентификатором.
	 * <p>
	 * @param id идентификатор элемента меню
	 * @return true - элемент существует, false - элемент не существует
	 */
	public synchronized boolean isItemExists(String id) {
		return childItem.containsKey(id) || childMenu.containsKey(id);
	}
	
	/**
	 * Создать элемент меню.
	 * <p>
	 * @param id идентификатор
	 * @param title текст элемента
	 * @param underlyed оборачиваемый элемент меню
	 * @return экземпляр элемента
	 * @throws MenuItemAlreadyExistsException 
	 */
	private MenuItem createItem(String id, String title, JMenuItem underlyed) throws MenuException {
		if ( isItemExists(id) ) {
			throw new MenuItemAlreadyExistsException(id);
		}
		final EventType eventType = dispatcher.createType(id);		
		underlyed.setText(title);
		MenuItem item = new MenuItem(underlyed, eventType); 
		item.getUnderlyingObject().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatcher.dispatch(eventType, SimpleEventFactory.getInstance());
			}
		});
		childItem.put(id, item);
		return item;
	}
	
	/**
	 * Создать субменю.
	 * <p>
	 * @param id идентификатор
	 * @param title заголовок
	 * @return экзхемпляр субменю
	 * @throws MenuItemAlreadyExistsException
	 */
	private Menu createMenu(String id, String title) throws MenuException {
		if ( isItemExists(id) ) {
			throw new MenuItemAlreadyExistsException(id);
		}
		Menu menu = new Menu(new JMenu(title), es);
		childMenu.put(id, menu);
		return menu;
	}
	
	/**
	 * Расчитать позицию для вставки в конец нормальной последовательности.
	 * <p>
	 * В меню могут быть эелементы, привязанные к концу меню. Данный метод
	 * используется для определения индекса вставки элемента в конец нормальной
	 * последовательности элементов. 
	 * <p>
	 * @return индекс элемента
	 */
	private int getNormalPosition() {
		return underlying.getItemCount() - bottomElements;
	}

}
