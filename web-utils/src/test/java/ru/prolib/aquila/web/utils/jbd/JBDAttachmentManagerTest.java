package ru.prolib.aquila.web.utils.jbd;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.easymock.IAnswer;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachment;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentCriteria;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentCriteriaBuilder;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentNotFoundException;
import ru.prolib.aquila.web.utils.httpattachment.HTTPDownloadInitiator;
import ru.prolib.aquila.web.utils.jbd.JBDAttachmentManager.ScannerHelper;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;

public class JBDAttachmentManagerTest {
	private IMocksControl control;
	private JBrowserDriver driverMock;
	private ScannerHelper helperMock;
	private HTTPDownloadInitiator downloadInitiatorMock;
	private HTTPAttachmentCriteria criteriaStub;
	private JBDAttachmentManager service;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		driverMock = control.createMock(JBrowserDriver.class);
		helperMock = control.createMock(ScannerHelper.class);
		downloadInitiatorMock = control.createMock(HTTPDownloadInitiator.class);
		criteriaStub = new HTTPAttachmentCriteriaBuilder().build();
		service = new JBDAttachmentManager(driverMock, helperMock);
	}
	
	@Test
	public void testGetLast_IfSeveralFilesAreMatched() throws Exception {
		downloadInitiatorMock.run();
		List<File> files = new ArrayList<>();
		files.add(new File("/foo/x.metadata"));
		files.add(new File("/foo/b.metadata"));
		files.add(new File("/foo/z.metadata"));
		expect(driverMock.attachmentsDir()).andReturn(new File("/foo"));
		expect(helperMock.listFiles(new File("/foo"), new JBDAttachmentFilter(criteriaStub)))
			.andReturn(new ArrayList<>(files));
		helperMock.sortFiles(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
		expectLastCall().andAnswer(new IAnswer<Void>() {
			@SuppressWarnings("unchecked")
			@Override
			public Void answer() throws Throwable {
				List<File> dummy = (List<File>) getCurrentArguments()[0];
				Collections.sort(dummy);
				return null;
			}
		});
		control.replay();
		
		HTTPAttachment actual = service.getLast(criteriaStub, downloadInitiatorMock);
		
		HTTPAttachment expected = new JBDAttachment(new File("/foo/b.metadata"), new File("/foo/b.content"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetLast_IfOneMatched() throws Exception {
		downloadInitiatorMock.run();
		List<File> files = new ArrayList<>();
		files.add(new File("/foo/x.metadata"));
		expect(driverMock.attachmentsDir()).andReturn(new File("/foo"));
		expect(helperMock.listFiles(new File("/foo"), new JBDAttachmentFilter(criteriaStub)))
			.andReturn(new ArrayList<>(files));
		helperMock.sortFiles(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
		control.replay();
		
		HTTPAttachment actual = service.getLast(criteriaStub, downloadInitiatorMock);
		
		HTTPAttachment expected = new JBDAttachment(new File("/foo/x.metadata"), new File("/foo/x.content"));
		assertEquals(expected, actual);
	}
	
	@Test (expected=HTTPAttachmentNotFoundException.class)
	public void testGetLast_IfNoneMatched() throws Exception {
		downloadInitiatorMock.run();
		expect(driverMock.attachmentsDir()).andReturn(new File("/foo"));
		expect(helperMock.listFiles(new File("/foo"), new JBDAttachmentFilter(criteriaStub))).andReturn(new ArrayList<>());
		control.replay();
		
		service.getLast(criteriaStub, downloadInitiatorMock);
	}

}
