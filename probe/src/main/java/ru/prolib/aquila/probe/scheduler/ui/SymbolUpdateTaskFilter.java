package ru.prolib.aquila.probe.scheduler.ui;

import java.util.ArrayList;
import java.util.List;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.data.replay.sus.SusTask;
import ru.prolib.aquila.probe.scheduler.SchedulerTask;
import ru.prolib.aquila.ui.form.SymbolListSelectionDialog;
import ru.prolib.aquila.ui.form.SymbolListSelectionDialogView;

/**
 * Filter on standard symbol update source task.
 * <p>
 * See {@link ru.prolib.aquila.data.replay.SymbolUpdateSourceImpl the data source}
 * and {@link ru.prolib.aquila.data.replay.sus.SusTask its task} classes for
 * more details.
 */
public class SymbolUpdateTaskFilter implements SchedulerTaskFilter {
	private final List<Symbol> filterBySymbol = new ArrayList<>();
	private final SymbolListSelectionDialogView filterDialog;
	private boolean enabled = true;
	
	public SymbolUpdateTaskFilter(SymbolListSelectionDialog filterDialog) {
		this.filterDialog = filterDialog;
	}

	public SymbolUpdateTaskFilter(IMessages messages) {
		this(new SymbolListSelectionDialog(messages, ProbeMsg.STD_SUS_FILTER_DIALOG_TITLE));
	}
	
	@Override
	public MsgID getTitle() {
		return ProbeMsg.STD_SUS_FILTER_MAIN_MENU;
	}

	@Override
	public boolean isIncluded(SchedulerTask handler) {
		if ( ! enabled ) {
			return false;
		}
		if ( handler.getRunnable().getClass() != SusTask.class ) {
			return false;
		}
		if ( filterBySymbol.size() == 0 ) {
			return true;
		}
		SusTask task = (SusTask) handler.getRunnable();
		if ( filterBySymbol.contains(task.getSymbol()) ) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enable) {
		this.enabled = enable;
	}

	@Override
	public boolean isSettingSupported() {
		return true;
	}

	@Override
	public void showSettingsDialog() {
		List<Symbol> dummy = filterDialog.showDialog(filterBySymbol);
		if ( dummy != null ) {
			filterBySymbol.clear();
			filterBySymbol.addAll(dummy);
		}
	}

	@Override
	public String formatTitle(SchedulerTask handler) {
		SusTask task = (SusTask) handler.getRunnable();
		return "Update " + task.getSymbol() + " " + task.getUpdate().getContents();
	}

}
