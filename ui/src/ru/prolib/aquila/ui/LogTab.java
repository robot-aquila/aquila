package ru.prolib.aquila.ui;

import java.awt.Adjustable;
import java.awt.BorderLayout;

import java.awt.Font;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

/**
 * 2013-03-01<br>
 * $Id: LogTab.java 554 2013-03-01 13:43:04Z whirlwind $
 */
public class LogTab extends JPanel implements TextAreaAppender.Appender {
	private static final long serialVersionUID = -6943677087631071841L;
	
	private final JTextArea output = new JTextArea();
	private final JScrollPane scroll = new JScrollPane(output);
	private int scrollDownCount = 0;
	
	public LogTab() {
		super(new BorderLayout());
		output.setLineWrap(true);
		output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		scroll.getVerticalScrollBar()
			.addAdjustmentListener(new AdjustmentListener() {  
				@Override
				public void adjustmentValueChanged(AdjustmentEvent e) {
					onScrollerChanged(e);
				}
	    }); 
		add(scroll);
		Logger root = Logger.getRootLogger();
		((TextAreaAppender) root.getAppender("TEXTAREA-APPENDER"))
			.setTarget(this);
	}
	
	@Override
	public void append(String str) {
		Adjustable a = scroll.getVerticalScrollBar();
		if ( a.getVisibleAmount() < a.getMaximum()
				&& a.getValue() + a.getVisibleAmount() >= a.getMaximum() )
		{
			synchronized ( this ) {
				scrollDownCount ++;
			}
		}		
		output.append(str);
	}
	
	private void onScrollerChanged(AdjustmentEvent e) {
		Adjustable a = e.getAdjustable();
		synchronized ( this ) {
			if ( scrollDownCount > 0 ) {
				scrollDownCount = 0;
				a.setValue(a.getMaximum());
			}
		}
	}

}
