package ru.prolib.aquila.qforts.impl;

public class QFPositionField {
	/**
	 * Variation Margin of currently open position.
	 */
	public static final int QF_VAR_MARGIN = 2000;
	/**
	 * Variation Margin of closed positions.
	 */
	public static final int QF_VAR_MARGIN_CLOSE = 2001;
	/**
	 * Variation Margin calculated at intermediate clearing.
	 */
	public static final int QF_VAR_MARGIN_INTER = 2002;
	/**
	 * Tick Value at the moment of position opening.
	 */
	public static final int QF_TICK_VALUE = 2003;
}
