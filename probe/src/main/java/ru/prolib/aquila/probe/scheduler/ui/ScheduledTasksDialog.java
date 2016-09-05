package ru.prolib.aquila.probe.scheduler.ui;

import java.awt.Dimension;
import java.time.ZoneId;

import javax.swing.JDialog;

import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.probe.scheduler.SchedulerState;

public class ScheduledTasksDialog extends JDialog implements ScheduledTasksDialogView {
	private static final long serialVersionUID = 1L;
	
	private final ScheduledTasksPanel tasksPanel;
	
	public ScheduledTasksDialog(IMessages messages, ZoneId zoneId) {
		this.tasksPanel = new ScheduledTasksPanel(messages, zoneId);
		setModal(true);
		setTitle(messages.get(ProbeMsg.STD_DIALOG_TITLE));
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setPreferredSize(new Dimension(600, 400));
		getContentPane().add(tasksPanel);
		pack();
	}
	
	public ScheduledTasksDialog(IMessages messages) {
		this(messages, ZoneId.systemDefault());
	}

	@Override
	public void showDialog(SchedulerState state) {
		tasksPanel.reloadTree(state);
		setVisible(true);
	}

}
