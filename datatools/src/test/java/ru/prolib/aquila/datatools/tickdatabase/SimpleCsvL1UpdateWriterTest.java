package ru.prolib.aquila.datatools.tickdatabase;

import static org.easymock.EasyMock.*;

import java.io.Writer;
import java.time.Instant;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.BusinessEntities.L1Update;

public class SimpleCsvL1UpdateWriterTest {
	private IMocksControl control;
	private Writer writerMock;
	private SimpleCsvL1UpdatePacker packerMock;
	private SimpleCsvL1UpdateWriter updateWriter;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		writerMock = control.createMock(Writer.class);
		packerMock = control.createMock(SimpleCsvL1UpdatePacker.class);
		updateWriter = new SimpleCsvL1UpdateWriter(writerMock, packerMock);
	}
	
	@Test
	public void testClose() throws Exception {
		writerMock.close();
		control.replay();
		
		updateWriter.close();
		
		control.verify();
	}
	
	@Test
	public void testFlush() throws Exception {
		writerMock.flush();
		control.replay();
		
		updateWriter.flush(Instant.now());
		
		control.verify();
	}

	@Test
	public void testWriteUpdate() throws Exception {
		L1Update update = control.createMock(L1Update.class);
		expect(packerMock.pack(update)).andReturn("packed line");
		writerMock.write("packed line");
		writerMock.write(System.lineSeparator());
		control.replay();
		
		updateWriter.writeUpdate(update);
		
		control.verify();
	}

}
