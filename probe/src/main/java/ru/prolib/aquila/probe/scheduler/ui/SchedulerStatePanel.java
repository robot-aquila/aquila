package ru.prolib.aquila.probe.scheduler.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.probe.scheduler.SchedulerMode;
import ru.prolib.aquila.probe.scheduler.SchedulerState;

public class SchedulerStatePanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final DateTimeFormatter timeFormat;
	private static final Map<SchedulerMode, MsgID> mode2MsgId;
	
	static {
		timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		mode2MsgId = new HashMap<>();
		mode2MsgId.put(SchedulerMode.CLOSE, ProbeMsg.SSP_MODE_CLOSE);
		mode2MsgId.put(SchedulerMode.RUN, ProbeMsg.SSP_MODE_RUN);
		mode2MsgId.put(SchedulerMode.RUN_CUTOFF, ProbeMsg.SSP_MODE_CUTOFF);
		mode2MsgId.put(SchedulerMode.RUN_STEP, ProbeMsg.SSP_MODE_STEP);
		mode2MsgId.put(SchedulerMode.WAIT, ProbeMsg.SSP_MODE_WAIT);
	}
	
	private final SchedulerState state;
	private final IMessages messages;
	private final Timer timer;
	private final JLabel lblCurrentTime, lblCutoffTime, lblCurrentMode, lblExecutionSpeed;
	private final ZoneId zoneId;
	
	public SchedulerStatePanel(IMessages messages, SchedulerState state, ZoneId zoneId) {
		this.messages = messages;
		this.state = state;
		this.zoneId = zoneId;
		setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		setLayout(new GridBagLayout());
		
		// Row 1
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 3, 0, 3);
		add(new JLabel(messages.get(ProbeMsg.SSP_CURRENT_TIME), SwingConstants.RIGHT), gbc);
		
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 3, 0, 3);
		add(lblCurrentTime = new JLabel(), gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 3, 0, 3);
		add(new JLabel(messages.get(ProbeMsg.SSP_CURRENT_MODE), SwingConstants.RIGHT), gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 3, 0, 3);
		add(lblCurrentMode = new JLabel(), gbc);
		
		// Row 2
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(0, 3, 0, 3);
		add(new JLabel(messages.get(ProbeMsg.SSP_CUTOFF_TIME), SwingConstants.RIGHT), gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = new Insets(0, 3, 0, 3);
		add(lblCutoffTime = new JLabel(), gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.insets = new Insets(0, 3, 0, 3);
		add(new JLabel(messages.get(ProbeMsg.SSP_EXEC_SPEED), SwingConstants.RIGHT), gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.insets = new Insets(0, 3, 0, 3);
		add(lblExecutionSpeed = new JLabel(), gbc);
		
		timer = new Timer(250, this);
		timer.setInitialDelay(1000);
		timer.setRepeats(true);
		timer.start();
	}
	
	public SchedulerStatePanel(IMessages messages, SchedulerState state) {
		this(messages, state, ZoneId.systemDefault());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		refreshControls();
	}
	
	private void refreshControls() {
		Instant currentTime, cutoffTime;
		SchedulerMode mode;
		int executionSpeed;
		state.lock();
		try {
			currentTime = state.getCurrentTime();
			cutoffTime = state.getCutoffTime();
			mode = state.getMode();
			executionSpeed = state.getExecutionSpeed();
		} finally {
			state.unlock();
		}
		lblCurrentTime.setText(currentTime.atZone(zoneId).format(timeFormat));
		lblCurrentMode.setText(messages.get(mode2MsgId.get(mode)));
		if ( mode == SchedulerMode.RUN_CUTOFF && cutoffTime != null ) {
			lblCutoffTime.setText(cutoffTime.atZone(zoneId).format(timeFormat));
		} else {
			lblCutoffTime.setText("");
		}
		lblExecutionSpeed.setText(Integer.toString(executionSpeed));
	}

}
