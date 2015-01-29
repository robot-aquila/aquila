package ru.prolib.aquila.ui.FastOrder;

import javax.swing.JComboBox;

import ru.prolib.aquila.core.BusinessEntities.OrderType;

/**
 * Селектор типа заявки.
 */
public class TypeCombo extends JComboBox {
	private static final long serialVersionUID = -4001585829087067712L;

	public TypeCombo() {
		super();
		addItem(OrderType.LIMIT);
		addItem(OrderType.MARKET);
	}
	
	/**
	 * Получить выбранный тип заявки.
	 * <p>
	 * @return тип заявки
	 */
	public OrderType getSelectedType() {
		return (OrderType) this.getSelectedItem();
	}

}
