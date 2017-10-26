package ru.prolib.aquila.data.storage.segstor.file;

import java.io.File;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class SegmentFileInfoImpl implements SegmentFileInfo {
	private File dir;
	private String name;
	private String suffix;
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.data.storage.segstor.file.SegmentFileInfo#getFullPath()
	 */
	@Override
	public File getFullPath() {
		if ( dir == null || name == null || suffix == null ) {
			throw new IllegalStateException("One of mandatory components is undefined");
		}
		return new File(dir, name + suffix);
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.data.storage.segstor.file.SegmentFileInfo#getDirectory()
	 */
	@Override
	public File getDirectory() {
		return dir;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.data.storage.segstor.file.SegmentFileInfo#getBaseName()
	 */
	@Override
	public String getBaseName() {
		return name;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.data.storage.segstor.file.SegmentFileInfo#getNameSuffix()
	 */
	@Override
	public String getNameSuffix() {
		return suffix;
	}
	
	/**
	 * Initialize using full path to a file.
	 * <p>
	 * This call means that the name suffix is empty (equals to "").
	 * <p>
	 * @param path - full path to segment file
	 * @return this
	 */
	public SegmentFileInfoImpl setFullPath(File path) {
		dir = path.getParentFile();
		name = path.getName();
		suffix = "";
		return this;
	}

	/**
	 * Initialize using components to build full path.
	 * <p>
	 * @param dir - directory which contains segment file
	 * @param baseName - base name of file
	 * @param nameSuffix - personal suffix of file name
	 * @return this
	 */
	public SegmentFileInfoImpl setFullPath(File dir, String baseName, String nameSuffix) {
		this.dir = dir;
		this.name = baseName;
		this.suffix = nameSuffix;
		return this;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != SegmentFileInfoImpl.class ) {
			return false;
		}
		SegmentFileInfoImpl o = (SegmentFileInfoImpl) other;
		return new EqualsBuilder()
				.append(o.dir, dir)
				.append(o.name, name)
				.append(o.suffix, suffix)
				.isEquals();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[path=" + getFullPath()
			+ " dir=" + getDirectory()
			+ " name=" + getBaseName()
			+ " suffix=" + getNameSuffix()
			+ "]";
	}

}
