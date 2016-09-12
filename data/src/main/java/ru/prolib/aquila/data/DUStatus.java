package ru.prolib.aquila.data;

/**
 * A data unit status.
 * <p>
 * The enumeration represents status of abstract data unit.
 */
public enum DUStatus {
	/**
	 * Status unknown (was not loaded). 
	 */
	UNKNOWN,
	
	/**
	 * Data unit contains errors.
	 */
	ERROR,
	
	/**
	 * Data unit exists but does not contains significant information.
	 */
	EMPTY,
	
	/**
	 * Data unit exists and contains significant information.
	 */
	FILLED,

}
