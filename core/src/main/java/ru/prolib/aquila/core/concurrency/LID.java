package ru.prolib.aquila.core.concurrency;

/**
 * Lockable object unique ID.
 */
public class LID implements Comparable<LID> {
	private static long currIID = Long.MIN_VALUE;
	
	/**
	 * Create new ID instance.
	 * <p>
	 * @return instance
	 */
	public static synchronized LID createInstance() {
		return new LID(currIID++);
	}
	
	/**
	 * Get current internal ID.
	 * <p>
	 * Note: the service method to help make a test.
	 * <p>
	 * @return current internal ID
	 */
	public static synchronized long getCurrentIID() {
		return currIID;
	}
	
	/**
	 * Test the LID is a last created LID.
	 * <p>
	 * @param lid - lockable ID
	 * @return true if is a last created LID, false otherwise
	 */
	public static synchronized boolean isLastCreatedLID(LID lid) {
		return lid.iid == currIID - 1;
	}
	
	private final long iid;
	
	private LID(long iid) {
		this.iid = iid;
	}
	
	long getIID() {
		return iid;
	}

	@Override
	public int compareTo(LID o) {
		return Long.compare(iid, o.iid);
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + iid + "]"; 
	}

}
