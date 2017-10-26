package ru.prolib.aquila.probe.scheduler.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.ZoneId;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ru.prolib.aquila.core.data.ZTFrame;
import ru.prolib.aquila.core.data.timeframe.ZTFHours;
import ru.prolib.aquila.core.data.timeframe.ZTFMinutes;
import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.ui.msg.CommonMsg;

public class SchedulerOptionsDialog extends JDialog
		implements SchedulerOptionsDialogView, ActionListener
{
	
	static class ExecutionSpeedItem {
		private final int executionSpeed;
		private final MsgID msgID;
		private String text;
		
		ExecutionSpeedItem(int executionSpeed, MsgID msgID) {
			this.executionSpeed = executionSpeed;
			this.msgID = msgID;
		}
		
		@Override
		public String toString() {
			return text;
		}
		
	}
	
	static class TimeFrameItem {
		private final ZTFrame timeFrame;
		private final MsgID msgID;
		private String text;
		
		TimeFrameItem(ZTFrame timeFrame, MsgID msgID) {
			this.timeFrame = timeFrame;
			this.msgID = msgID;
		}
		
		@Override
		public String toString() {
			return text;
		}
		
	}
	
	private static final long serialVersionUID = 1L;
	private static final String ACTION_CANCEL = "CANCEL";
	private static final String ACTION_OK = "OK";
	private static final Vector<ExecutionSpeedItem> EXEC_SPEED;
	
	static {
		{
			Vector<ExecutionSpeedItem> list = new Vector<>();
			list.add(new ExecutionSpeedItem(0, ProbeMsg.SOD_EXEC_SPEED0));
			list.add(new ExecutionSpeedItem(1, ProbeMsg.SOD_EXEC_SPEED1));
			list.add(new ExecutionSpeedItem(2, ProbeMsg.SOD_EXEC_SPEED2));
			list.add(new ExecutionSpeedItem(4, ProbeMsg.SOD_EXEC_SPEED4));
			list.add(new ExecutionSpeedItem(8, ProbeMsg.SOD_EXEC_SPEED8));			
			EXEC_SPEED = list;
		}
	};
	
	private final JPanel contentPanel = new JPanel();
	private final JComboBox<ExecutionSpeedItem> cmbExecSpeed;
	private final JComboBox<TimeFrameItem >cmbTimeFrame;
	private final JButton btnOK, btnCancel;
	private final Vector<TimeFrameItem> tframeOptions;
	private SchedulerOptions selectedOptions;
	
	public SchedulerOptionsDialog(IMessages messages, ZoneId zoneID) {
		setResizable(false);
		setAlwaysOnTop(true);
		setTitle(messages.get(ProbeMsg.SOD_DIALOG_TITLE));
		setModal(true);
		{
			Vector<TimeFrameItem> list = new Vector<>();
			list.add(new TimeFrameItem(new ZTFMinutes( 1, zoneID), ProbeMsg.SOD_INTERVAL_1MIN));
			list.add(new TimeFrameItem(new ZTFMinutes( 5, zoneID), ProbeMsg.SOD_INTERVAL_5MIN));
			list.add(new TimeFrameItem(new ZTFMinutes(10, zoneID), ProbeMsg.SOD_INTERVAL_10MIN));
			list.add(new TimeFrameItem(new ZTFMinutes(15, zoneID), ProbeMsg.SOD_INTERVAL_15MIN));
			list.add(new TimeFrameItem(new ZTFMinutes(30, zoneID), ProbeMsg.SOD_INTERVAL_30MIN));
			list.add(new TimeFrameItem(new ZTFHours(1, zoneID), ProbeMsg.SOD_INTERVAL_1HOUR));	
			tframeOptions = list;
		}
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
			panel.add(new JLabel(messages.get(ProbeMsg.SOD_EXEC_SPEED)));
			panel.add(Box.createRigidArea(new Dimension(10, 0)));
			for ( ExecutionSpeedItem item : EXEC_SPEED ) {
				item.text = messages.get(item.msgID);
			}
			cmbExecSpeed = new JComboBox<>(EXEC_SPEED);
			panel.add(cmbExecSpeed);
			contentPanel.add(panel);
		}
		contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		{
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
			panel.add(new JLabel(messages.get(ProbeMsg.SOD_INTERVAL)));
			panel.add(Box.createRigidArea(new Dimension(10, 0)));
			for ( TimeFrameItem item : tframeOptions ) {
				item.text = messages.get(item.msgID);
			}
			cmbTimeFrame = new JComboBox<>(tframeOptions);
			panel.add(cmbTimeFrame);
			contentPanel.add(panel);
		}
		contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		{
			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.SOUTH);
			btnCancel = new JButton(messages.get(CommonMsg.CANCEL));
			btnCancel.setActionCommand(ACTION_CANCEL);
			btnCancel.addActionListener(this);
			btnOK = new JButton(messages.get(CommonMsg.OK));
			btnOK.setActionCommand(ACTION_OK);
			btnOK.addActionListener(this);
			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			panel.add(btnCancel);
			panel.add(btnOK);
		}
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		getRootPane().setDefaultButton(btnOK);
		pack();
		setLocationRelativeTo(null);
	}

	@Override
	public SchedulerOptions showDialog(SchedulerOptions initialOptions) {
		selectedOptions = null;
		int expectedExecutionSpeed = initialOptions.getExecutionSpeed();
		for ( int i = 0; i < EXEC_SPEED.size(); i ++ ) {
			ExecutionSpeedItem item = EXEC_SPEED.get(i);
			if ( item.executionSpeed == expectedExecutionSpeed ) {
				cmbExecSpeed.setSelectedIndex(i);
			}
			if ( cmbExecSpeed.getSelectedIndex() == -1 ) {
				cmbExecSpeed.setSelectedIndex(0);
			}
		}
		ZTFrame expectedTimeFrame = initialOptions.getTimeFrame();
		for ( int i = 0; i < tframeOptions.size(); i ++ ) {
			TimeFrameItem item = tframeOptions.get(i);
			if ( item.timeFrame == expectedTimeFrame ) {
				cmbTimeFrame.setSelectedIndex(i);
			}
			if ( cmbTimeFrame.getSelectedIndex() == -1 ) {
				cmbTimeFrame.setSelectedIndex(0);
			}
		}
		setVisible(true);
		return selectedOptions;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getActionCommand().equals(ACTION_OK) ) {
			selectedOptions = new SchedulerOptions();
			selectedOptions.setExecutionSpeed(((ExecutionSpeedItem)cmbExecSpeed.getSelectedItem()).executionSpeed);
			selectedOptions.setTimeFrame(((TimeFrameItem)cmbTimeFrame.getSelectedItem()).timeFrame);
		}
		setVisible(false);
	}
	
}
