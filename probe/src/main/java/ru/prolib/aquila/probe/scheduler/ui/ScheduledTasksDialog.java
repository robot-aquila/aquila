package ru.prolib.aquila.probe.scheduler.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.probe.scheduler.SchedulerState;

public class ScheduledTasksDialog extends JDialog implements ScheduledTasksDialogView, ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String ENABLE_PFX = "ENABLE";
	private static final String SETUP_PFX = "SETUP";
	
	static class FilterEntry {
		private final SchedulerTaskFilter filter;
		private final JCheckBoxMenuItem enableItem;
		private final JMenuItem setupItem;
		
		FilterEntry(SchedulerTaskFilter filter, JCheckBoxMenuItem enableItem, JMenuItem setupItem) {
			this.filter = filter;
			this.enableItem = enableItem;
			this.setupItem = setupItem;
		}
		
		public boolean isEnableItem(String actionCommand) {
			return actionCommand.equals(enableItem.getActionCommand());
		}
		
		public boolean isSetupItem(String actionCommand) {
			return setupItem != null && actionCommand.equals(setupItem.getActionCommand());
		}
		
	}
	
	private final IMessages messages;
	private final ScheduledTasksPanel tasksPanel;
	private final List<FilterEntry> filterEntries = new ArrayList<>();
	private SchedulerState state;
	
	public ScheduledTasksDialog(IMessages messages, List<SchedulerTaskFilter> filters, ZoneId zoneId) {
		this.messages = messages;
		if ( filters != null ) {
			filters = new ArrayList<>(filters);
			filters.add(new SchedulerTaskDefaultFilter());
			createMenu(filters);
		}
		this.tasksPanel = new ScheduledTasksPanel(messages, filters, zoneId);
		setModal(true);
		setTitle(messages.get(ProbeMsg.STD_DIALOG_TITLE));
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setPreferredSize(new Dimension(600, 400));
		getContentPane().add(tasksPanel);
		pack();
	}
	
	private void createMenu(List<SchedulerTaskFilter> filters) {
		JMenuBar menuBar = new JMenuBar();
		JMenu menuFilter = new JMenu(messages.get(ProbeMsg.STD_MENU_FILTERS));
		JMenu menuSetup = new JMenu(messages.get(ProbeMsg.STD_MENU_FILTER_SETUP));
		for ( int i = 0; i < filters.size(); i ++ ) {
			SchedulerTaskFilter filter = filters.get(i);
			JCheckBoxMenuItem enableItem = new JCheckBoxMenuItem(messages.get(filter.getTitle()));
			enableItem.setSelected(filter.isEnabled());
			enableItem.setActionCommand(ENABLE_PFX + i);
			enableItem.addActionListener(this);
			menuFilter.add(enableItem);
			JMenuItem setupItem = null;
			if ( filter.isSettingSupported() ) {
				setupItem = new JMenuItem(messages.get(filter.getTitle()));
				setupItem.setActionCommand(SETUP_PFX + i);
				setupItem.addActionListener(this);
				menuSetup.add(setupItem);
			}
			filterEntries.add(new FilterEntry(filter, enableItem, setupItem));
		}
		if ( menuFilter.getItemCount() > 0 ) {
			menuBar.add(menuFilter);
		}
		if ( menuSetup.getItemCount() > 0 ) {
			menuBar.add(menuSetup);
		}
		if ( menuFilter.getItemCount() > 0 ) {
			setJMenuBar(menuBar);
		}
	}
	
	public ScheduledTasksDialog(IMessages messages, List<SchedulerTaskFilter> filters) {
		this(messages, filters, ZoneId.systemDefault());
	}
	
	public ScheduledTasksDialog(IMessages messages) {
		this(messages, null, ZoneId.systemDefault());
	}

	@Override
	public void showDialog(SchedulerState state) {
		tasksPanel.reloadTree(this.state = state);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		for ( FilterEntry entry : filterEntries ) {
			SchedulerTaskFilter filter = entry.filter;
			boolean reload = false;
			if ( entry.isEnableItem(e.getActionCommand()) ) {
				filter.setEnabled(entry.enableItem.isSelected());
				reload = true;
			} else if ( entry.isSetupItem(e.getActionCommand()) ) {
				filter.showSettingsDialog();
				reload = true;
			}
			if ( reload ) {
				tasksPanel.reloadTree(state);
				return;
			}
		}
	}

}
