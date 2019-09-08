package ru.prolib.aquila.ui.form;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.BusinessEntities.Order;
import ru.prolib.aquila.core.BusinessEntities.OrderException;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.ui.msg.CommonMsg;

public class MenuFactory {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(MenuFactory.class);
	}
	
	public interface OrderAccessor {
		Order getOrder();
	}
	
	static class OrderBySelectedRowAccessor implements OrderAccessor {
		private final JTable table;
		private final OrderListTableModel tableModel;
		
		public OrderBySelectedRowAccessor(JTable table, OrderListTableModel tableModel) {
			this.table = table;
			this.tableModel = tableModel;
		}
		
		@Override
		public Order getOrder() {
			int visible_index = table.getSelectedRow();
			return visible_index >= 0 ? tableModel.getOrder(table.convertRowIndexToModel(visible_index)) : null;
		}
		
	}
	
	static class OrderProxyAccessor implements OrderAccessor {
		private Order order;
		
		@Override
		public Order getOrder() {
			return order;
		}
		
		public void setOrder(Order order) {
			this.order = order;
		}

	}
	
	static class CancelOrder implements Runnable {
		private final OrderAccessor orderAccessor;
		private final JRootPane rootPanel;
		
		public CancelOrder(OrderAccessor order_accessor, JRootPane root_panel) {
			this.orderAccessor = order_accessor;
			this.rootPanel = root_panel;
		}

		@Override
		public void run() {
			Order order = orderAccessor.getOrder();
			if ( order != null ) {
				try {
					order.getTerminal().cancelOrder(order);
				} catch ( OrderException e ) {
					JOptionPane.showMessageDialog(rootPanel, e.getMessage());
					logger.error("Order cancel error: ", e);
				}
			}
		}
		
	}
	
	static class CancelOrderItemActivator implements Runnable {
		private final OrderAccessor orderAccessor;
		private final JMenuItem menuItem;
		
		public CancelOrderItemActivator(OrderAccessor order_accessor, JMenuItem menu_item) {
			this.orderAccessor = order_accessor;
			this.menuItem = menu_item;
		}

		@Override
		public void run() {
			Order order = orderAccessor.getOrder();
			menuItem.setEnabled(order != null && order.getStatus() != null && ! order.getStatus().isFinal());
		}
		
	}
	
	static class RunnableActionListener implements ActionListener {
		private final Runnable task;
		
		public RunnableActionListener(Runnable task) {
			this.task = task;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			task.run();
		}

	}
	
	static class OrderUnderCursorDetector extends MouseAdapter {
		private final OrderProxyAccessor orderAccessor;
		
		public OrderUnderCursorDetector(OrderProxyAccessor order_accessor) {
			this.orderAccessor = order_accessor;
		}
		
		@Override
		public void mousePressed(MouseEvent event) {
			if ( event.isPopupTrigger() ) {
				onEvent(event);
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent event) {
			if ( event.isPopupTrigger() ) {
				onEvent(event);
			}
		}
		
		private void onEvent(MouseEvent event) {
			orderAccessor.setOrder(null);
			Object _o = event.getSource();
			if ( _o.getClass() != JTable.class ) {
				logger.warn("Expected source to be JTable but: " + _o.getClass());
				return;
			}
			JTable table = (JTable) _o;
			TableModel _m = table.getModel();
			if ( !(_m instanceof OrderListTableModel) ) {
				logger.warn("Expected table model to be OrderListTableModel but: " + _m.getClass());
				return;
			}
			OrderListTableModel table_model = (OrderListTableModel) _m;
			int visible_index = table.rowAtPoint(new Point(event.getX(), event.getY()));
			if ( visible_index < 0 ) {
				//logger.debug("No row under cursor");
				return;
			}
			//logger.debug("Row visible index: {}", visible_index);
			int index = table.convertRowIndexToModel(visible_index);
			Order order = table_model.getOrder(index);
			if ( order == null ) {
				//logger.debug("No order under cursor (index {})", index);
			} else {
				//logger.debug("Order under cursor: {}", order.getID());
				orderAccessor.setOrder(order);
			}
		}
		
	}
	
	static class PopupMenuDisplayer extends MouseAdapter {
		private final JPopupMenu popupMenu;
		
		public PopupMenuDisplayer(JPopupMenu popup_menu) {
			this.popupMenu = popup_menu;
		}
		
		@Override
		public void mousePressed(MouseEvent event) {
			if ( event.isPopupTrigger() ) {
				onEvent(event);
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent event) {
			if ( event.isPopupTrigger() ) {
				onEvent(event);
			}
		}
		
		private void onEvent(MouseEvent event) {
			popupMenu.show(event.getComponent(), event.getX(), event.getY());
		}

	}
	
	private final IMessages messages;
	
	public MenuFactory(IMessages messages) {
		this.messages = messages;
	}
	
	public void addOrderCancel(JPopupMenu popup_menu, JTable table) {
		JMenuItem item;
		OrderProxyAccessor accessor = new OrderProxyAccessor();
		popup_menu.add(item = new JMenuItem(messages.get(CommonMsg.MENU_ORDER_CANCEL)));
		item.addActionListener(new RunnableActionListener(new CancelOrder(accessor, table.getRootPane())));
		table.addMouseListener(new OrderUnderCursorDetector(accessor));
		// TODO: add enable disable item
	}
	
	public JPopupMenu createOrderTablePopupMenu(JTable order_table) {
		JPopupMenu popup_menu = new JPopupMenu();
		addOrderCancel(popup_menu, order_table);
		order_table.addMouseListener(new PopupMenuDisplayer(popup_menu));
		return popup_menu;
	}

}
