package ru.prolib.aquila.probe.scheduler.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.time.Instant;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import ru.prolib.aquila.core.data.TimeFrame;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.probe.SchedulerImpl;
import ru.prolib.aquila.ui.form.TimeSelectionDialog;
import ru.prolib.aquila.ui.form.TimeSelectionDialogView;

public class SchedulerControlToolbar extends JToolBar implements ActionListener, Observer {
	private static final long serialVersionUID = 1L;
	private static final String ICON_PATH_PREFIX = "shared/images/probe/";
	private static final String ACTION_OPTIONS = "OPTIONS";
	private static final String ACTION_PAUSE = "PAUSE";
	private static final String ACTION_RUN = "RUN";
	private static final String ACTION_RUN_TIME = "RUN_TIME";
	private static final String ACTION_RUN_INTERVAL = "RUN_INTERVAL";
	private static final String ACTION_RUN_STEP = "RUN_STEP";
	
	private final IMessages messages;
	private final SchedulerImpl scheduler;
	private final JButton btnOptions, btnPause, btnRun, btnRunTime, btnRunInterval, btnRunStep;
	private final TimeSelectionDialogView timeSelectionDialog;
	private final SchedulerOptionsDialogView schedulerOptionsDialog;
	private SchedulerOptions schedulerOptions;
	
	public SchedulerControlToolbar(IMessages messages, SchedulerImpl scheduler) {
		this.messages = messages;
		this.scheduler = scheduler;
		setName(messages.get(ProbeMsg.TOOLBAR_TITLE));
		timeSelectionDialog = new TimeSelectionDialog(messages);
		schedulerOptionsDialog = new SchedulerOptionsDialog(messages);
		btnOptions = createButton("options", ACTION_OPTIONS, ProbeMsg.BTN_TTIP_OPTIONS);
		btnPause = createButton("pause", ACTION_PAUSE, ProbeMsg.BTN_TTIP_PAUSE);
		btnRun = createButton("run1", ACTION_RUN, ProbeMsg.BTN_TTIP_RUN);
		btnRunTime = createButton("run2", ACTION_RUN_TIME, ProbeMsg.BTN_TTIP_RUN_TIME);
		btnRunInterval = createButton("run3", ACTION_RUN_INTERVAL, ProbeMsg.BTN_TTIP_RUN_INTERVAL);
		btnRunStep = createButton("run4", ACTION_RUN_STEP, ProbeMsg.BTN_TTIP_RUN_STEP);
		scheduler.getState().addObserver(this);
		refreshControls();
		schedulerOptions = new SchedulerOptions();
		schedulerOptions.setTimeFrame(TimeFrame.M1);
	}
	
	private JButton createButton(String iconName, String actionCommand, MsgID tooltip) {
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(messages.get(tooltip));
		button.addActionListener(this);
		URL iconURL = getClass().getClassLoader().getResource(ICON_PATH_PREFIX + iconName + ".png");
		if ( iconURL != null ) {
			button.setIcon(new ImageIcon(iconURL));
		}
		add(button);
		return button;
	}
	
	private void refreshControls() {
		switch (  scheduler.getState().getMode() ) {
		case WAIT:
			btnOptions.setEnabled(true);
			btnPause.setEnabled(false);
			btnRun.setEnabled(true);
			btnRunTime.setEnabled(true);
			btnRunInterval.setEnabled(true);
			btnRunStep.setEnabled(true);
			break;
		case CLOSE:
			btnOptions.setEnabled(false);
			btnPause.setEnabled(false);
			btnRun.setEnabled(false);
			btnRunTime.setEnabled(false);
			btnRunInterval.setEnabled(false);
			btnRunStep.setEnabled(false);
			break;
		case RUN:
		case RUN_STEP:
		case RUN_CUTOFF:
			btnOptions.setEnabled(false);
			btnPause.setEnabled(true);
			btnRun.setEnabled(false);
			btnRunTime.setEnabled(false);
			btnRunInterval.setEnabled(false);
			btnRunStep.setEnabled(false);
			break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch ( e.getActionCommand() ) {
		case ACTION_OPTIONS:
			schedulerOptions.setExecutionSpeed(scheduler.getState().getExecutionSpeed());
			SchedulerOptions o = schedulerOptionsDialog.showDialog(schedulerOptions);
			if ( o != null ) {
				schedulerOptions = o;
				scheduler.setExecutionSpeed(o.getExecutionSpeed());
			}
			break;
		case ACTION_RUN_INTERVAL:
			scheduler.setModeRun(schedulerOptions.getTimeFrame()
					.getInterval(scheduler.getCurrentTime()).getEnd());
			break;
		case ACTION_RUN_TIME:
			Instant time = timeSelectionDialog.showDialog(scheduler.getCurrentTime());
			if ( time != null ) {
				scheduler.setModeRun(time);
			}
			break;
		case ACTION_PAUSE:
			scheduler.setModeWait();
			break;
		case ACTION_RUN:
			scheduler.setModeRun();
			break;
		case ACTION_RUN_STEP:
			scheduler.setModeStep();
			break;
		}
	}

	@Override
	public void update(final Observable o, final Object arg) {
		if ( ! SwingUtilities.isEventDispatchThread() ) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override public void run() {
					update(o, arg);
				}				
			});
			return;
		}
		
		if ( o == scheduler.getState() ) {
			refreshControls();
		}
	}

}
