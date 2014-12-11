package ru.prolib.aquila.probe.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DateFormatter;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

@SuppressWarnings({ "serial" })
public class SelectTimeDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JButton cancelButton;
	private JButton okButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SelectTimeDialog dialog = new SelectTimeDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public SelectTimeDialog() {
		setResizable(false);
		setAlwaysOnTop(true);
		setTitle("Select target time");
		setModal(true);
		setBounds(100, 100, 321, 150);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{110, 171, 0};
		gbl_contentPanel.rowHeights = new int[]{18, 23, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblCurrentTime = new JLabel("Current time:");
			lblCurrentTime.setHorizontalAlignment(SwingConstants.RIGHT);
			GridBagConstraints gbc_lblCurrentTime = new GridBagConstraints();
			gbc_lblCurrentTime.fill = GridBagConstraints.BOTH;
			gbc_lblCurrentTime.insets = new Insets(0, 0, 5, 5);
			gbc_lblCurrentTime.gridx = 0;
			gbc_lblCurrentTime.gridy = 0;
			contentPanel.add(lblCurrentTime, gbc_lblCurrentTime);
		}
		{
			JLabel lblCurrentTimeValue = new JLabel("_TIME_");
			GridBagConstraints gbc_lblCurrentTimeValue = new GridBagConstraints();
			gbc_lblCurrentTimeValue.fill = GridBagConstraints.BOTH;
			gbc_lblCurrentTimeValue.insets = new Insets(0, 0, 5, 0);
			gbc_lblCurrentTimeValue.gridx = 1;
			gbc_lblCurrentTimeValue.gridy = 0;
			contentPanel.add(lblCurrentTimeValue, gbc_lblCurrentTimeValue);
		}
		{
			JLabel lblTargetTime = new JLabel("Target time:");
			lblTargetTime.setHorizontalAlignment(SwingConstants.RIGHT);
			lblTargetTime.setHorizontalTextPosition(SwingConstants.RIGHT);
			GridBagConstraints gbc_lblTargetTime = new GridBagConstraints();
			gbc_lblTargetTime.fill = GridBagConstraints.BOTH;
			gbc_lblTargetTime.insets = new Insets(0, 0, 0, 5);
			gbc_lblTargetTime.gridx = 0;
			gbc_lblTargetTime.gridy = 1;
			contentPanel.add(lblTargetTime, gbc_lblTargetTime);
		}
		JSpinner spinner = new JSpinner(new SpinnerDateModel());
		JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd HH:mm:ss.SSS");
		DateFormatter formatter = (DateFormatter) editor.getTextField().getFormatter();
		//formatter.setOverwriteMode(true);
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
				cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
			}
			{
				okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				getRootPane().setDefaultButton(okButton);
			}
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			buttonPane.add(cancelButton);
			buttonPane.add(okButton);
		}
	}

}
