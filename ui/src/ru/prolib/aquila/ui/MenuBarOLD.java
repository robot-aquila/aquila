package ru.prolib.aquila.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * 
 * <p>
 * 2013-02-28<br>
 * $Id: MenuBarOLD.java 554 2013-03-01 13:43:04Z whirlwind $
 */
@Deprecated // TODO: to remove
public class MenuBarOLD extends JMenuBar {
	private static final long serialVersionUID = 1774459594278039943L;
	public static final String MENU_FILE = "ITEM_FILE";
	public static final String CMD_EXIT = "ITEM_EXIT";
	public static final String ITEM_DATA = "ITEM_DATA";
	public static final String ITEM_REQ_SECURITY = "ITEM_REQ_SECURITY";
	public static final String ITEM_ORDERS = "ITEM_ORDERS";
	public static final String ITEM_PLACE_ORDER = "ITEM_PLACE_ORDER";
	public static final String ITEM_CANCEL_ORDERS = "ITEM_CANCEL_ORDERS";
	public static final String MENU_TERMINAL = "ITEM_TERMINAL";
	public static final String ITEM_START = "ITEM_START";
	public static final String ITEM_STOP = "ITEM_STOP";
	
	private final Map<String,JMenuItem> items = new HashMap<String,JMenuItem>();
	private final MainFrame owner;
	private ClassLabels uiLabels;
	
	public MenuBarOLD(MainFrame o, UiTexts texts) {
		this.owner = o;
		uiLabels = texts.get("MenuBar");
		
		JMenu option = null;
		add(option = new JMenu(uiLabels.get(MENU_FILE)));
		option.add(createItem(uiLabels.get(CMD_EXIT), new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//owner.onExit();
			}			
		}, true));
		
		add(option = new JMenu(uiLabels.get(MENU_TERMINAL)));
		
		option.add(createItem(uiLabels.get(ITEM_START), new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				owner.startTerminal();
			}			
		}, true));
		
		option.add(createItem(uiLabels.get(ITEM_STOP), new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				owner.stopTerminal();
			}			
		}, false));
		
		add(option = new JMenu(uiLabels.get(ITEM_DATA)));
		
		option.add(createItem(uiLabels.get(ITEM_REQ_SECURITY), new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//owner.onRequestSecurity();
				
			}			
		}, true));
		
		add(option = new JMenu(uiLabels.get(ITEM_ORDERS)));
		
		option.add(createItem(uiLabels.get(ITEM_PLACE_ORDER), new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//owner.onPlaceOrder();
				
			}			
		}, true));
		option.add(createItem(uiLabels.get(ITEM_CANCEL_ORDERS), new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//owner.onCancelOrders();
				
			}			
		}, false));
	}

	public void setItemEnable(String name, boolean enable) {
		items.get(uiLabels.get(name)).setEnabled(enable);
	}
	
	private JMenuItem createItem(String text, ActionListener action, boolean enable) {
		JMenuItem item = new JMenuItem(text);
		item.addActionListener(action);
		item.setEnabled(enable);
		items.put(text,  item);
		return item;		
	}
}
