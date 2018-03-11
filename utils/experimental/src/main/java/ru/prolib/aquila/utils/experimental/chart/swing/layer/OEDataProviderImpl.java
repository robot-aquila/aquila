package ru.prolib.aquila.utils.experimental.chart.swing.layer;

import java.util.HashSet;
import java.util.Set;

import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.BusinessEntities.OrderExecution;
import ru.prolib.aquila.core.BusinessEntities.OrderExecutionEvent;
import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.core.data.EditableTSeries;

public class OEDataProviderImpl implements EventListener {
	private final EditableTSeries<OEEntrySet> entries;
	private final Set<Terminal> terminals;
	private final OEValidator validator;
	
	OEDataProviderImpl(EditableTSeries<OEEntrySet> entries,
					   Set<Terminal> terminals,
					   OEValidator validator)
	{
		this.entries = entries;
		this.terminals = terminals;
		this.validator = validator;
	}
	
	public OEDataProviderImpl(EditableTSeries<OEEntrySet> entries, OEValidator validator) {
		this(entries, new HashSet<>(), validator);
	}
	
	public OEDataProviderImpl(EditableTSeries<OEEntrySet> entries, OEValidator validator, Terminal terminal) {
		this(entries, validator);
		addTerminal(terminal);
	}
	
	public OEValidator getValidator() {
		return validator;
	}
	
	public Set<Terminal> getTrackedTerminals() {
		return terminals;
	}
	
	public EditableTSeries<OEEntrySet> getEntries() {
		return entries;
	}
	
	public synchronized void addTerminal(Terminal terminal) {
		if ( ! terminals.contains(terminal) ) {
			terminals.add(terminal);
			terminal.onOrderExecution().addListener(this);
		}
	}
	
	public synchronized void removeTerminal(Terminal terminal) {
		if ( terminals.contains(terminal) ) {
			terminals.remove(terminal);
			terminal.onOrderExecution().removeListener(this);
		}
	}

	@Override
	public synchronized void onEvent(Event event) {
		for ( Terminal terminal : terminals ) {
			if ( event.isType(terminal.onOrderExecution()) ) {
				OrderExecutionEvent e = (OrderExecutionEvent) event;
				OrderExecution oe = e.getExecution();
				if ( validator.isValid(oe) ) {
					OEEntrySet eset = entries.get(oe.getTime());
					if ( eset == null ) {
						eset = new OEEntrySetImpl();
						entries.set(oe.getTime(), eset);
					}
					eset.addEntry(new OEEntryImpl(oe.getAction() == OrderAction.BUY, oe.getPricePerUnit()));
				}
			}
		}
	}

}
