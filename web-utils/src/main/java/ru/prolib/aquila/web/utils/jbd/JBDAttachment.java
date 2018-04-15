package ru.prolib.aquila.web.utils.jbd;

import java.io.File;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachment;

public class JBDAttachment implements HTTPAttachment {
	private final File metadataFile, contentFile;
	
	public JBDAttachment(File metadataFile, File contentFile) {
		this.metadataFile = metadataFile;
		this.contentFile = contentFile;
	}

	@Override
	public File getFile() {
		return contentFile;
	}
	
	public File getMetaDataFile() {
		return metadataFile;
	}

	@Override
	public void remove() {
		metadataFile.delete();
		contentFile.delete();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("metadata", metadataFile)
			.append("content", contentFile)
			.toString();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(265491261, 555123)
			.append(metadataFile)
			.append(contentFile)
			.toHashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != JBDAttachment.class ) {
			return false;
		}
		JBDAttachment o = (JBDAttachment) other;
		return new EqualsBuilder()
			.append(o.metadataFile, metadataFile)
			.append(o.contentFile, contentFile)
			.isEquals();
	}

}
