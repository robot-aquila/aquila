package ru.prolib.aquila.ui;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LabeledTextValue extends JPanel {

	/**
	 * $Id: LabeledTextValue.java 481 2013-02-03 15:14:28Z whirlwind $
	 */
	private static final long serialVersionUID = 3775748578679918249L;
	private JLabel label = new JLabel();
	private JLabel value = new JLabel();
	
	
	public LabeledTextValue(String labelTxt) {
		super();
		label.setText(labelTxt);
		setValueFont(new Font(null, Font.PLAIN, 12));
		add(label);
		add(value);
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
	}
	
	public void setValue(String valText) {
		value.setText(valText);
	}
	
	public void setValueFont(Font font) {
		value.setFont(font);
	}
	
	public void setLabelFont(Font font) {
		label.setFont(font);
	}
}
