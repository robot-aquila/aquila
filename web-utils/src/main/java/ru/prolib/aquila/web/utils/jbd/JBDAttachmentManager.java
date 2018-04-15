package ru.prolib.aquila.web.utils.jbd;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;

import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachment;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentCriteria;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentException;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentManager;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentNotFoundException;
import ru.prolib.aquila.web.utils.httpattachment.HTTPDownloadInitiator;

public class JBDAttachmentManager implements HTTPAttachmentManager {
	
	interface ScannerHelper {
		List<File> listFiles(File dir, FilenameFilter filter);
		void sortFiles(List<File> files, Comparator<File> comparator);
	}
	
	static class ScannerHelperImpl implements ScannerHelper {

		@Override
		public List<File> listFiles(File dir, FilenameFilter filter) {
			List<File> files = new ArrayList<>();
			for ( String x : dir.list(filter) ) {
				files.add(new File(dir, x));
			}
			return files;
		}

		@Override
		public void sortFiles(List<File> files, Comparator<File> comparator) {
			Collections.sort(files, comparator);
		}
		
	}
	
	private final JBrowserDriver driver;
	private final ScannerHelper helper;
	
	JBDAttachmentManager(JBrowserDriver driver, ScannerHelper helper) {
		this.driver = driver;
		this.helper = helper;
	}
	
	public JBDAttachmentManager(JBrowserDriver driver) {
		this(driver, new ScannerHelperImpl());
	}

	@Override
	public HTTPAttachment getLast(HTTPAttachmentCriteria criteria, HTTPDownloadInitiator initiator)
			throws HTTPAttachmentException, IOException
	{
		initiator.run();
		List<File> files = helper.listFiles(driver.attachmentsDir(), new JBDAttachmentFilter(criteria));
		if ( files.size() == 0 ) {
			throw new HTTPAttachmentNotFoundException(criteria);
		}
		helper.sortFiles(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
		File m = files.get(0);
		return new JBDAttachment(m, JBDAttachmentFilter.toContentFile(m));
	}

}
