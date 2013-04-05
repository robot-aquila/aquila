package ru.prolib.aquila.ta.ds.quik;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ChaosTheory.ServiceBuilder;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderException;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderHelper;
import ru.prolib.aquila.ChaosTheory.ServiceBuilderHelperImpl;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;
import ru.prolib.aquila.ipc.ISession;
import ru.prolib.aquila.ipc.IpcException;
import ru.prolib.aquila.ta.ds.BarWriterGrouper;
import ru.prolib.aquila.ta.ds.DealWriter;
import ru.prolib.aquila.ta.ds.DealWriterAggregate;
import ru.prolib.aquila.ta.ds.DealWriterToBarWriter;
import ru.prolib.aquila.ta.ds.jdbc.BarWriterJdbc;
import ru.prolib.aquila.ta.ds.jdbc.DbAccessor;
import ru.prolib.aquila.util.AlignDateMinute;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

public class ExportBuilderQuik implements ServiceBuilder {
	private static final Logger logger = LoggerFactory.getLogger(ExportBuilderQuik.class); 
	public final static String ASSET_NODE	= "Asset";
	public final static String WRITER_NODE	= "Writer";
	public final static String TOPIC_ATTR	= "topic";
	public final static String CODE_ATTR	= "code";
	public final static String TABLE_ATTR	= "table";
	public final static String PERIOD_ATTR	= "period";
	
	private final ServiceBuilderHelper helper;
	
	public ExportBuilderQuik(ServiceBuilderHelper helper) {
		super();
		this.helper = helper;
	}
	
	public ExportBuilderQuik() {
		this(new ServiceBuilderHelperImpl());
	}

	@Override
	public Object create(ServiceLocator locator, HierarchicalStreamReader reader)
			throws ServiceBuilderException, ServiceLocatorException
	{
		ExportQuik dispatcher =
			new ExportQuik(helper.getString(TOPIC_ATTR, reader));
		while ( reader.hasMoreChildren() ) {
			reader.moveDown();
			if ( reader.getNodeName().equals(ASSET_NODE) ) {
				String code = helper.getString(CODE_ATTR, reader);
				logger.info("Export asset {}", code);
				try {
					dispatcher.attachWriter(code, readWriters(locator, reader));
				} catch ( IpcException e ) {
					throw new ServiceBuilderException(e.getMessage(), e);
				}
			} else {
				logger.warn("Ignore unknown node: {}, Expected: {}",
						reader.getNodeName(), ASSET_NODE);
			}
			reader.moveUp();
		}
		dispatcher.registerHandler(locator.getRXltDdeDispatcher());
		return dispatcher;
	}
	
	private DealWriter readWriters(ServiceLocator locator,
									 HierarchicalStreamReader reader)
		throws ServiceLocatorException, ServiceBuilderException, IpcException
	{
		DbAccessor dba = locator.getDatabase();
		ISession ipc = locator.getIpcSession();
		DealWriterAggregate root = new DealWriterAggregate();
		while ( reader.hasMoreChildren() ) {
			reader.moveDown();
			if ( reader.getNodeName().equals(WRITER_NODE) ) {
				String table = helper.getString(TABLE_ATTR, reader);
				int period = helper.getInt(PERIOD_ATTR, reader);
				logger.info("Write to table {} group by {} m", table, period);
				DealWriter writer = new DealWriterToBarWriter(
					new BarWriterGrouper(new AlignDateMinute(period),
						new BarWriterJdbc(dba, table)));
				root.attachWriter(new ExportQuikNotifier(writer,
								  ipc.createEvent(table)));
			} else {
				logger.warn("Ignore unknown node: {}, Expected: {}",
						reader.getNodeName(), WRITER_NODE);
			}
			reader.moveUp();
		}
		return root;
	}

}
