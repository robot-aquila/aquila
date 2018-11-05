package ru.prolib.aquila.ui.form;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import ru.prolib.aquila.core.EventQueue;
import ru.prolib.aquila.core.text.IMessages;

public class EventQueueStatePanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private final EventQueue queue;
	private final IMessages messages;
	private final Timer timer;
	private final JLabel lblTotalEvents;
	
	public EventQueueStatePanel(IMessages messages, EventQueue queue) {
		this.messages = messages;
		this.queue = queue;
		setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 3, 0, 3);
		add(new JLabel("TEC", SwingConstants.RIGHT), gbc);
		
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(0, 3, 0, 3);
		add(lblTotalEvents = new JLabel(), gbc);
		
		timer = new Timer(1000, this);
		timer.setInitialDelay(1000);
		timer.setRepeats(true);
		timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		lblTotalEvents.setText(Long.toString(queue.getTotalEvents()));
	}
	
}
