package ru.prolib.aquila.utils.experimental.chart.swing;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import ru.prolib.aquila.utils.experimental.chart.CursorController;

public class MouseAdapter implements MouseListener, MouseMotionListener {
	private final String chartID;
	private final CursorController controller;
	
	public MouseAdapter(String chartID, CursorController controller) {
		this.chartID = chartID;
		this.controller = controller;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		controller.cursorMoved(chartID, e.getX(), e.getY());
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		controller.cursorEntered(chartID, e.getX(), e.getY());
	}

	@Override
	public void mouseExited(MouseEvent e) {
		controller.cursorExited(chartID, e.getX(), e.getY());
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

}
