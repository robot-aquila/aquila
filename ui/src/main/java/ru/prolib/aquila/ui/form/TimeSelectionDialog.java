package ru.prolib.aquila.ui.form;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DateFormatter;

import ru.prolib.aquila.core.text.IMessages;
import ru.prolib.aquila.core.text.MsgID;
import ru.prolib.aquila.ui.msg.CommonMsg;

public class TimeSelectionDialog extends JDialog implements ActionListener, TimeSelectionDialogView {
	private static final long serialVersionUID = 1L;
	private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	private static final SimpleDateFormat format;
	private static final String ACTION_CANCEL = "CANCEL";
	private static final String ACTION_OK = "OK";
	
	static {
		format = new SimpleDateFormat(TIME_FORMAT);
	}
	
	private final JPanel contentPanel = new JPanel();
	private final JButton btnCancel;
	private final JButton btnOK;
	private final JSpinner spinner;
	private final JLabel lblInitialTimeValue;
	private final SpinnerDateModel spinnerData;
	private final ZoneId zoneId;
	private Date selectedTime;
	
	public TimeSelectionDialog(IMessages messages, MsgID titleID, ZoneId zoneId) {
		this.zoneId = zoneId;
		setResizable(false);
		setAlwaysOnTop(true);
		setTitle(messages.get(titleID));
		setModal(true);
		setBounds(100, 100, 321, 150);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridBagLayout());
		{
			JLabel lbl = new JLabel(messages.get(CommonMsg.TSD_INITIAL_TIME));
			lbl.setHorizontalAlignment(SwingConstants.RIGHT);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(0, 0, 5, 5);
			gbc.gridx = 0;
			gbc.gridy = 0;
			contentPanel.add(lbl, gbc);
		}
		{
			lblInitialTimeValue = new JLabel();
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(0, 0, 5, 0);
			gbc.gridx = 1;
			gbc.gridy = 0;
			contentPanel.add(lblInitialTimeValue, gbc);
		}
		{
			JLabel lbl = new JLabel(messages.get(CommonMsg.TSD_SELECTED_TIME));
			lbl.setHorizontalAlignment(SwingConstants.RIGHT);
			lbl.setHorizontalTextPosition(SwingConstants.RIGHT);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(0, 0, 0, 5);
			gbc.gridx = 0;
			gbc.gridy = 1;
			contentPanel.add(lbl, gbc);
		}
		{
			spinnerData = new SpinnerDateModel();
			spinner = new JSpinner(spinnerData);
			JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, TIME_FORMAT);
			DateFormatter formatter = (DateFormatter) editor.getTextField().getFormatter();
			spinner.setEditor(editor);
			spinner.setValue(new Date());
	
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 1;
			gbc.gridy = 1;
			contentPanel.add(spinner, gbc);
			formatter.setAllowsInvalid(false);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			btnCancel = new JButton(messages.get(CommonMsg.CANCEL));
			btnCancel.setActionCommand(ACTION_CANCEL);
			btnCancel.addActionListener(this);
			btnOK = new JButton(messages.get(CommonMsg.OK));
			btnOK.setActionCommand(ACTION_OK);
			btnOK.addActionListener(this);
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			buttonPane.add(btnCancel);
			buttonPane.add(btnOK);
		}
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		getRootPane().setDefaultButton(btnOK);
		pack();
	}
	
	public TimeSelectionDialog(IMessages messages) {
		this(messages, CommonMsg.TSD_DEFAULT_TITLE, ZoneId.systemDefault());
	}
	
	@Override
	public LocalDateTime showDialog(LocalDateTime initialTime) {
		Instant dummy = showDialog(initialTime.atZone(zoneId).toInstant());
		if ( dummy != null ) {
			return LocalDateTime.ofInstant(dummy, zoneId);
		} else {
			return null;
		}
	}
	
	@Override
	public Instant showDialog(Instant initialTime) {
		selectedTime = null;
		Date j_startTime = Date.from(initialTime);
		lblInitialTimeValue.setText(format.format(j_startTime));
		spinner.setValue(j_startTime);
		spinnerData.setStart(j_startTime);
		setVisible(true);
		if ( selectedTime != null ) {
			return Instant.ofEpochMilli(selectedTime.getTime());
		} else {
			return null;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getActionCommand().equals(ACTION_OK) ) {
			Date j_selected = (Date) spinner.getValue(),
					j_start = (Date) spinnerData.getStart();
			if ( j_selected.compareTo(j_start) > 0 ) {
				selectedTime = (Date) spinner.getValue();
			}
		}
		setVisible(false);
	}
	
}
