package ru.prolib.aquila.web.utils.jbd;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentCriteria;

public class JBDAttachmentFilter implements FilenameFilter {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(JBDAttachmentFilter.class);
	}
	
	private final HTTPAttachmentCriteria criteria;
	private final long fromTime;
	
	public JBDAttachmentFilter(HTTPAttachmentCriteria criteria) {
		this.criteria = criteria;
		this.fromTime = criteria.getTimeOfStartDownload().toEpochMilli();
	}
	
	/**
	 * Convert metadata file to content file.
	 * <p>
	 * @param metadata - metadata file 
	 * @return path to content file
	 * @throws IllegalArgumentException
	 */
	public static File toContentFile(File metadata) {
		String x = metadata.toString();
		if ( ! FilenameUtils.isExtension(x, "metadata") ) {
			throw new IllegalArgumentException("Unexpected file extension: " + metadata);
		}
		return new File(FilenameUtils.removeExtension(x).concat(".content"));
	}

	@Override
	public boolean accept(File dir, String name) {
		if ( ! FilenameUtils.isExtension(name, "metadata") ) {
			return false;
		}
		File f = new File(dir, name);
		if ( ! f.exists() || f.isDirectory() ) {
			return false;
		}
		if ( f.lastModified() < fromTime ) {
			return false;
		}
		File c = toContentFile(f);
		if ( ! c.exists() || c.isDirectory() ) {
			return false;
		}
		String url = criteria.getURL(),
			contentType = criteria.getContentType(),
			contentDisposition = criteria.getContentDisposition();
		if ( url != null || contentType != null || contentDisposition != null ) {
			List<String> lines = null;
			try {
				lines = FileUtils.readLines(f);
			} catch ( IOException e ) {
				logger.error("Error reading file: {}", f, e);
				return false;
			}
			if ( lines.size() < 3 ) {
				logger.error("Expected number of lines 3 but: " + lines.size());
				return false;
			}
			if ( (url != null && ! url.equals(lines.get(0)))
			  || (contentType != null && ! contentType.equals(lines.get(1)))
			  || (contentDisposition != null && ! contentDisposition.equals(lines.get(2))) )
			{
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != JBDAttachmentFilter.class ) {
			return false;
		}
		JBDAttachmentFilter o = (JBDAttachmentFilter) other;
		return new EqualsBuilder()
			.append(o.criteria, criteria)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(71237841, 983475)
			.append(criteria)
			.toHashCode(); 
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("criteria", criteria)
			.toString();
	}

}
