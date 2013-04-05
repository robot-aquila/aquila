package ru.prolib.aquila.ChaosTheory;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.ipc.ISession;
import ru.prolib.aquila.ipc.ltam.Session;
import ru.prolib.aquila.rxltdde.Protocol;
import ru.prolib.aquila.rxltdde.Receiver.ReceiverService;
import ru.prolib.aquila.stat.TrackingTrades;
import ru.prolib.aquila.stat.TrackingTradesImpl;
import ru.prolib.aquila.ta.ds.MarketData;
import ru.prolib.aquila.ta.ds.jdbc.DbAccessor;
import ru.prolib.aquila.ta.ds.quik.RXltDdeDispatcher;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ServiceLocatorImpl implements ServiceLocator {
	static final Logger logger = LoggerFactory.getLogger(ServiceLocatorImpl.class);
	
	public static final String	RXLTDDE_RECV_HOST = "0.0.0.0";
	public static final int 	RXLTDDE_RECV_PORT = 5000;
	
	static private ServiceBuilder builder = new ServiceLocatorDefaultBuilder();
	
	private DbAccessor dba;
	private MarketData ds;
	private Export export;
	private RXltDdeDispatcher ddeDisp;
	private ReceiverService ddeRecv;
	private ISession ipc;
	private Props props;
	private Portfolio portfolio;
	private PortfolioState state;
	private PortfolioOrders orders;
	private Assets assets;
	private TrackingTrades tracking;
	
	/**
	 * Инициализировать объект в соответствии с XML-конфигурацией
	 * @param xml
	 * @return
	 * @throws ServiceLocatorException 
	 * @throws ServiceBuilderException 
	 */
	public synchronized static ServiceLocatorImpl getInstance(File xml)
		throws ServiceLocatorException, ServiceBuilderException
	{
		ServiceLocatorImpl locator = new ServiceLocatorImpl();
		HierarchicalStreamReader reader = new DomDriver().createReader(xml);
		builder.create(locator, reader);
		reader.close();
		return locator;
	}
	
	/**
	 * Задать билдер сервис-локатора.
	 * @param serviceBuilder
	 */
	public synchronized static
		void setServiceLocatorBuilder(ServiceBuilder serviceBuilder)
	{
		builder = serviceBuilder;
	}
	
	public ServiceLocatorImpl() {
		super();
	}

	@Override
	public DbAccessor getDatabase() throws ServiceLocatorException {
		if ( dba == null ) {
			throw new ServiceLocatorNoServiceException("Database");
		}
		return dba;
	}
	
	@Override
	public void setDatabase(DbAccessor dba) throws ServiceLocatorException {
		this.dba = dba;
	}
	
	@Override
	public MarketData getMarketData() throws ServiceLocatorException {
		if ( ds == null ) {
			throw new ServiceLocatorNoServiceException("MarketData");
		}
		return ds;
	}
	
	@Override
	public void setMarketData(MarketData ds) throws ServiceLocatorException {
		this.ds = ds;
	}

	@Override
	public ISession getIpcSession() throws ServiceLocatorException {
		if ( ipc == null ) {
			ipc = new Session();
		}
		return ipc;
	}
	
	@Override
	public void setIpcSession(ISession sess) throws ServiceLocatorException {
		ipc = sess;
	}

	@Override
	public RXltDdeDispatcher getRXltDdeDispatcher()
		throws ServiceLocatorException
	{
		if ( ddeDisp == null ) {
			ddeDisp = new RXltDdeDispatcher();
		}
		return ddeDisp;
	}
	
	@Override
	public void setRXltDdeDispatcher(RXltDdeDispatcher dispatcher)
			throws ServiceLocatorException
	{
		ddeDisp = dispatcher;
	}

	@Override
	public ReceiverService getRXltDdeReceiver() throws ServiceLocatorException {
		if ( ddeRecv == null ) {
			try {
				ddeRecv = new ReceiverService(RXLTDDE_RECV_HOST,
						RXLTDDE_RECV_PORT, getRXltDdeDispatcher());
			} catch ( Protocol.ProtocolException e ) {
				throw new ServiceLocatorException(e.getMessage(), e);
			}
		}
		return ddeRecv;
	}
	
	@Override
	public void setRXltDdeReceiver(ReceiverService recv)
			throws ServiceLocatorException
	{
		ddeRecv = recv;
	}
	
	@Override
	public Export getExportService()
			throws ServiceLocatorException
	{
		if ( export == null ) {
			throw new ServiceLocatorNoServiceException("Export");
		}
		return export;
	}
	
	@Override
	public void setExportService(Export service)
			throws ServiceLocatorException
	{
		export = service;
	}
	
	@Override
	public Props getProperties() throws ServiceLocatorException {
		if ( props == null ) {
			props = new PropsImpl();
		}
		return props;
	}
	
	@Override
	public void setProperties(Props props)
		throws ServiceLocatorException
	{
		this.props = props;
	}
	
	@Override
	public Portfolio getPortfolio() throws ServiceLocatorException {
		if ( portfolio == null ) {
			throw new ServiceLocatorNoServiceException("Portfolio");
		}
		return portfolio;
	}
	
	@Override
	public void setPortfolio(Portfolio port) throws ServiceLocatorException {
		portfolio = port;
	}

	@Override
	public Assets getAssets() throws ServiceLocatorException {
		if ( assets == null ) {
			throw new ServiceLocatorNoServiceException("Assets");
		}
		return assets;
	}

	@Override
	public void setAssets(Assets service) throws ServiceLocatorException {
		assets = service;
	}

	@Override
	public PortfolioState getPortfolioState() throws ServiceLocatorException {
		if ( state == null ) {
			throw new ServiceLocatorNoServiceException("PortfolioState");
		}
		return state;
	}

	@Override
	public void setPortfolioState(PortfolioState state)
			throws ServiceLocatorException
	{
		this.state = state; 
	}

	@Override
	public PortfolioOrders getPortfolioOrders() throws ServiceLocatorException {
		if ( orders == null ) {
			throw new ServiceLocatorNoServiceException("PortfolioOrders");
		}
		return orders;
	}

	@Override
	public void setPortfolioOrders(PortfolioOrders orders)
			throws ServiceLocatorException
	{
		this.orders = orders;
	}

	@Override
	public TrackingTrades getTrackingTrades() throws ServiceLocatorException {
		if ( tracking == null ) {
			tracking = new TrackingTradesImpl();
		}
		return tracking;
	}

	@Override
	public void setTrackingTrades(TrackingTrades service)
		throws ServiceLocatorException
	{
		tracking = service;
	}

}
