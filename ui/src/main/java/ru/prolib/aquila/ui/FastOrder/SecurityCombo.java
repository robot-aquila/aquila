package ru.prolib.aquila.ui.FastOrder;

import java.util.*;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.*;

/**
 * Селектор инструмента.
 */
public class SecurityCombo extends JComboBox<SecurityDescriptor> implements Starter, EventListener {
	private static final long serialVersionUID = -3328773972490353436L;
	private final Terminal securities;
	private final Vector<Security> list = new Vector<Security>();
	
	public SecurityCombo(Terminal securities) {
		super();
		this.securities = securities;
	}
	
	@Override
	public void onEvent(Event event) {
		//if ( event.isType(securities.OnSecurityAvailable()) ) {
		//	addSecurity(((SecurityEvent) event).getSecurity());
		//}
	}

	@Override
	public void start() {
		//securities.OnSecurityAvailable().addListener(this);
		//list.clear();
		//removeAllItems();
		//for ( Security security : securities.getSecurities() ) {
		//	addSecurity(security);
		//}
	}

	@Override
	public void stop() {
		//securities.OnSecurityAvailable().removeListener(this);
	}

	/**
	 * Добавить инструмент для выбора.
	 * <p>
	 * @param security инструмент
	 */
	private void addSecurity(final Security security) {
		if ( ! list.contains(security) ) {
			if ( SwingUtilities.isEventDispatchThread() ) {
				list.add(security);
				addItem(security.getDescriptor());
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					@Override public void run() {
						addSecurity(security);
					}
				});
			}
		}
	}
	
	/**
	 * Получить дескриптор выбранного инструмента.
	 * <p>
	 * @return дескриптор инструмента или null, если инструмент не выбран
	 */
	public SecurityDescriptor getSelectedDescriptor() {
		Security selected = getSelectedSecurity();
		return selected != null ? selected.getDescriptor() : null;
	}
	
	/**
	 * Получить выбранный инструмент.
	 * <p>
	 * @return инструмент или null, если инструмент не выбран
	 */
	public Security getSelectedSecurity() {
		int index = getSelectedIndex();
		return index >= 0 ? list.get(index) : null;		
	}
	
	/**
	 * Проверить факт выбора инструмента.
	 * <p>
	 * @return true - инструмент выбран, false - инструмент не выборан
	 */
	public boolean isSelected() {
		return getSelectedSecurity() != null;
	}

}
