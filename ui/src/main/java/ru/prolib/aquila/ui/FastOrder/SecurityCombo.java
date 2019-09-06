package ru.prolib.aquila.ui.FastOrder;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import ru.prolib.aquila.core.*;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.*;
import ru.prolib.aquila.core.BusinessEntities.SecurityException;

/**
 * Селектор инструмента.
 */
public class SecurityCombo extends JComboBox<Symbol> implements Starter, EventListener {
	private static final long serialVersionUID = -3328773972490353436L;
	private final Terminal terminal;
	private final SortedComboBoxModel<Symbol> model;
	
	public SecurityCombo(Terminal terminal) {
		super(new SortedComboBoxModel<Symbol>());
		this.terminal = terminal;
		this.model = (SortedComboBoxModel<Symbol>) getModel();
	}
	
	@Override
	public void onEvent(Event event) {
		if ( event.isType(terminal.onSecurityAvailable()) ) {
			addSecurity(((SecurityEvent) event).getSecurity());
		}
	}

	@Override
	public void start() {
		terminal.onSecurityAvailable().addListener(this);
		removeAllItems();
		for ( Security security : terminal.getSecurities() ) {
			addSecurity(security);
		}
	}

	@Override
	public void stop() {
		terminal.onSecurityAvailable().removeListener(this);
	}

	/**
	 * Добавить инструмент для выбора.
	 * <p>
	 * @param security инструмент
	 */
	private void addSecurity(final Security security) {
		if ( SwingUtilities.isEventDispatchThread() ) {
			Symbol symbol = security.getSymbol();
			if ( model.getIndexOf(symbol) == -1 ) {
				addItem(symbol);
			}
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override public void run() {
					addSecurity(security);
				}
			});
		}
	}
	
	/**
	 * Получить дескриптор выбранного инструмента.
	 * <p>
	 * @return дескриптор инструмента или null, если инструмент не выбран
	 */
	public Symbol getSelectedSymbol() {
		return (Symbol) getSelectedItem();
	}
	
	/**
	 * Получить выбранный инструмент.
	 * <p>
	 * @return инструмент или null, если инструмент не выбран
	 */
	public Security getSelectedSecurity() {
		Symbol symbol = (Symbol) getSelectedItem();
		try {
			return symbol == null ? null : terminal.getSecurity(symbol);
		} catch ( SecurityException e ) {
			throw new RuntimeException("Unexpected error: ", e);
		}
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
