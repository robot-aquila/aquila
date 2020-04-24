package ru.prolib.aquila.web.utils.httpattachment;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChromeAttachmentManager implements HTTPAttachmentManager {
	static final Logger logger = LoggerFactory.getLogger(ChromeAttachmentManager.class);
	
	private final File targetDir;
	private final long timeout;
	
	public ChromeAttachmentManager(File target_dir, long timeout_millis) {
		this.targetDir = target_dir;
		this.timeout = timeout_millis;
	}
	
	private HTTPAttachment scan(HTTPAttachmentCriteria criteria, Set<String> init_files) {
		Set<String> curr_files = new HashSet<>(Arrays.asList(targetDir.list()));
		curr_files.removeAll(init_files);

		// filename patterns:
		// <filename>.<ext>
		// <filename>.<ext>.crdownload
		// <filename> (\d+).<ext>
		// <filename> (\d+).<ext>.crdownload
		boolean found_expected = false;
		int max_number = -1, find_len = criteria.getFileName().length() + " (0)".length();
		String name_tpl = FilenameUtils.getBaseName(criteria.getFileName()) + " (";
		String ext_tpl = ")." + FilenameUtils.getExtension(criteria.getFileName());
		for ( String curr_file : curr_files ) {
			if ( curr_file.length() >= find_len && curr_file.startsWith(name_tpl) && curr_file.endsWith(ext_tpl) ) {
				String number = curr_file.substring(name_tpl.length(), curr_file.length() - ext_tpl.length());
				if ( number.matches("^[0-9]+$") ) {
					max_number = Math.max(max_number, Integer.valueOf(number));
				}
			} else if ( curr_file.equals(criteria.getFileName()) ) {
				found_expected = true;
			}
		}
		if ( max_number > -1 ) {
			// found numbered file, return recent one
			return new HTTPAttachmentImpl(new File(targetDir, new StringBuilder()
				.append(name_tpl)
				.append(max_number)
				.append(ext_tpl)
				.toString()));
		}
		if ( found_expected ) {
			return new HTTPAttachmentImpl(new File(targetDir, criteria.getFileName()));
		}
		return null;
	}

	@Override
	public HTTPAttachment getLast(HTTPAttachmentCriteria criteria, HTTPDownloadInitiator initiator)
			throws HTTPAttachmentException, IOException
	{
		logger.debug("Entered attachment manager. Target dir: " + targetDir);
		String files[] = targetDir.list();
		logger.debug("File list obtained: " + (files == null ? null : files.length));
		Set<String> init_files = new HashSet<>(Arrays.asList(targetDir.list()));
		logger.debug("Initial file list obtained");
		long end_time = System.currentTimeMillis() + timeout;
		CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
			try {
				initiator.run();
			} catch ( IOException e ) {
				throw new IllegalStateException(e);
			}
		});
		logger.debug("Start waiting for initiator finish");
		try {
			task.get(timeout, TimeUnit.MILLISECONDS);
		} catch ( TimeoutException e ) {
			throw new HTTPAttachmentException("Timeout", e);
		} catch ( ExecutionException|InterruptedException e ) {
			throw new HTTPAttachmentException("Unexpected exception: ", e);
		}
		logger.debug("Initiator finished, start scanning for changes");
		//try {
		//	Thread.sleep(60000);
		//} catch ( InterruptedException e ) {
		//	throw new IOException(e);
		//}
		do {
			logger.debug("Scan...");
			HTTPAttachment result = scan(criteria, init_files);
			if ( result != null ) {
				return result;
			}
			try {
				Thread.sleep(100L);
			} catch ( InterruptedException e ) {
				throw new HTTPAttachmentException("Interrupted: ", e);
			}

		} while ( System.currentTimeMillis() < end_time );
		
		throw new HTTPAttachmentNotFoundException(criteria, new StringBuilder()
			.append("Procedure finished but expected file not found: ")
			.append(criteria.getFileName())
			.toString());
	}

}
