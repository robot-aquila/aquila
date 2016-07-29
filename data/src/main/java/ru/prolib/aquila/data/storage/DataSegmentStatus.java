package ru.prolib.aquila.data.storage;

public enum DataSegmentStatus {
	/**
	 * Data segment has an error marker.
	 */
	ERROR,
	/**
	 * Data segment exists but contains no significant data.
	 */
	EMPTY,
	/**
	 * Data segment exists and contains significant data.
	 */
	OK
}
