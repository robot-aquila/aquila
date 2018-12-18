package ru.prolib.aquila.utils.experimental.chart;

public interface CursorController {
	void cursorEntered(String chartID, int x, int y);
	void cursorMoved(String chartID, int x, int y);
	void cursorExited(String chartID, int x, int y);
}
