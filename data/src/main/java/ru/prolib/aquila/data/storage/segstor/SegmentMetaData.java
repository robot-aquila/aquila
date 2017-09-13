package ru.prolib.aquila.data.storage.segstor;

import java.time.Instant;

public interface SegmentMetaData {
	
	/**
	 * Get virtual path of the segment.
	 * <p>
	 * Virtual path represents location to the segment in some virtual space.
	 * For example it may be a file system path for files or URL for internet files.
	 * <p>
	 * @return virtual path
	 */
	String getPath();
	
	/**
	 * Get hash code of the segment.
	 * <p>
	 * Hash code is a checksum of data stored in the segment.  If checksums are same
	 * that means that the data of the segment not changed.
	 * <p>
	 * @return hash code
	 */
	String getHashCode();
	
	/**
	 * Get segment update time.
	 * <p>
	 * @return time when segment was created or changed last time
	 */
	Instant getUpdateTime();
	
	/**
	 * Get number of elements contained in the segment.
	 * <p>
	 * @return number of elements
	 */
	long getNumberOfElements();

}
