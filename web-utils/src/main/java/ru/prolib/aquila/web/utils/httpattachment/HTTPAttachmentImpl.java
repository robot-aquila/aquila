package ru.prolib.aquila.web.utils.httpattachment;

import java.io.File;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Simple file-based HTTP attachment handler.
 */
public class HTTPAttachmentImpl implements HTTPAttachment {
	private final File file;
	
	public HTTPAttachmentImpl(File file) {
		this.file = file;
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public void remove() {
		file.delete();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("file", file)
			.toString();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(554917, 331557)
			.append(file)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != HTTPAttachmentImpl.class ) {
			return false;
		}
		HTTPAttachmentImpl o = (HTTPAttachmentImpl) other;
		return new EqualsBuilder()
			.append(o.file, file)
			.isEquals();
	}

}
