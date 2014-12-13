package ru.prolib.aquila.probe.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DateFormatter;

import org.joda.time.DateTime;

import ru.prolib.aquila.core.text.IMessages;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings({ "serial" })
public class SelectTargetTimeDialog extends JDialog
	implements SelectTargetTimeDialogView, ActionListener
{
	private static final String TITLE = "STTDLG_TITLE";
	private static final String CURTIME = "STTDLG_CURRENT_TIME";
	private static final String TGTTIME = "STTDLG_TARGET_TIME";
	private static final String OK = "OK";
	private static final String CANCEL = "CANCEL";
	private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	private static final SimpleDateFormat format;
	
	static {
		format = new SimpleDateFormat(TIME_FORMAT);
	}
	
	private final JPanel contentPanel = new JPanel();
	private final JButton cancelButton;
	private final JButton okButton;
	private final JSpinner spinner;
	private final JLabel lblCurrentTimeValue;
	private final SpinnerDateModel spinnerData;
	private DateTime selectedTime;

	public SelectTargetTimeDialog(IMessages messages) {
		super();
		setResizable(false);
		setAlwaysOnTop(true);
		setTitle(messages.get(TITLE));
		setModal(true);
		setBounds(100, 100, 321, 150);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblCurrentTime = new JLabel(messages.get(CURTIME));
			lblCurrentTime.setHorizontalAlignment(SwingConstants.RIGHT);
			GridBagConstraints gbc_lblCurrentTime = new GridBagConstraints();
			gbc_lblCurrentTime.fill = GridBagConstraints.BOTH;
			gbc_lblCurrentTime.insets = new Insets(0, 0, 5, 5);
			gbc_lblCurrentTime.gridx = 0;
			gbc_lblCurrentTime.gridy = 0;
			contentPanel.add(lblCurrentTime, gbc_lblCurrentTime);
		}
		{
			lblCurrentTimeValue = new JLabel();
			GridBagConstraints gbc_lblCurrentTimeValue = new GridBagConstraints();
			gbc_lblCurrentTimeValue.fill = GridBagConstraints.BOTH;
			gbc_lblCurrentTimeValue.insets = new Insets(0, 0, 5, 0);
			gbc_lblCurrentTimeValue.gridx = 1;
			gbc_lblCurrentTimeValue.gridy = 0;
			contentPanel.add(lblCurrentTimeValue, gbc_lblCurrentTimeValue);
		}
		{
			JLabel lblTargetTime = new JLabel(messages.get(TGTTIME));
			lblTargetTime.setHorizontalAlignment(SwingConstants.RIGHT);
			lblTargetTime.setHorizontalTextPosition(SwingConstants.RIGHT);
			GridBagConstraints gbc_lblTargetTime = new GridBagConstraints();
			gbc_lblTargetTime.fill = GridBagConstraints.BOTH;
			gbc_lblTargetTime.insets = new Insets(0, 0, 0, 5);
			gbc_lblTargetTime.gridx = 0;
			gbc_lblTargetTime.gridy = 1;
			contentPanel.add(lblTargetTime, gbc_lblTargetTime);
		}
		spinnerData = new SpinnerDateModel();
		spinner = new JSpinner(spinnerData);
		JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, TIME_FORMAT);
		DateFormatter formatter = (DateFormatter) editor.getTextField().getFormatter();
		spinner.setEditor(editor);
		spinner.setValue(new Date());
		
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.fill = GridBagConstraints.BOTH;
		gbc_spinner.gridx = 1;
		gbc_spinner.gridy = 1;
		contentPanel.add(spinner, gbc_spinner);
		{
			formatter.setAllowsInvalid(false);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				cancelButton = new JButton(messages.get(CANCEL));
				cancelButton.setActionCommand(CANCEL);
				cancelButton.addActionListener(this);
			}
			{
				okButton = new JButton(messages.get(OK));
				okButton.setActionCommand(OK);
				okButton.addActionListener(this);
				getRootPane().setDefaultButton(okButton);
			}
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			buttonPane.add(cancelButton);
			buttonPane.add(okButton);
		}
	}

	@Override
	public DateTime showDialog(DateTime initialTime) {
		selectedTime = null;
		lblCurrentTimeValue.setText(format.format(initialTime.toDate()));
		Date startTime = initialTime.plus(1).toDate();
		spinner.setValue(startTime);
		spinnerData.setStart(startTime);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		pack();
		setVisible(true);
		return selectedTime;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getActionCommand().equals(OK) ) {
			selectedTime = new DateTime(spinner.getValue());
			setVisible(false);
		} else if ( e.getActionCommand().equals(CANCEL) ) {
			setVisible(false);
		}
	}

}
