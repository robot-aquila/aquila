package ru.prolib.aquila.ChaosTheory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Драйвер портфеля к стратегии по Теории Хаоса. 
 * 
 * Торговля по методике Билла Вильямса подразумевает, что одновременно могут
 * быть выставлены максимум только две заявки: одна на покупку и одна на
 * продажу. Таким образом, при выставлении новой заявки, предыдущая заявка, если
 * она не исполнена, должна быть снята. Так же, необходимо снимать все активные
 * заявки в случае перехода торговой стратегии в другое состояние. Данный
 * драйвер реализует минималистичный интерфейс, позволяющий управлять портфелем
 * в рамках стратегии на основе Теории Хаоса. 
 * 
 * TODO: надо попробовать стоп-цену наоборот ставить выше указанной цены.
 * 
 * По поводу цены заявки, фигурируемой в методах класса. Так как указывается
 * цена исполнения заявки, а выставляются стоп-ордера, которые требуют указания
 * стоп-цены, единственный вариант определения стоп-цены - это рассчет на основе
 * цены исполнения. При выставлении заявки на покупку стоп-цена соответствует
 * price - (priceStep * slippage), а при выставлении заявки на продажу
 * price + (priceStep * slippage) где price - цена выставления заявки (аргумент
 * метода), priceStep - шаг цены, slippage - резерв под проскальзывание в шагах
 * цены.
 */
public class PortfolioDriver {
	final static Logger logger = LoggerFactory.getLogger(PortfolioDriver.class);
	public final static int CLOSE_IMMEDIATELY_RETRIES = 100;
	public final static int CLOSE_IMMEDIATELY_TIMEOUT = 10000; // 10 seconds
	
	protected final Portfolio port;
	protected final RiskManager rm;
	protected int slippage = 1; 
	protected Order buy = null;
	protected Order sell = null;
	private final PortfolioDriverEmergClosePosition closeLong;
	private final PortfolioDriverEmergClosePosition closeShort;
	private final Asset asset;
	
	public PortfolioDriver(Portfolio portfolio, Asset asset,
						   RiskManager riskManager, long timeout)
	{
		this(portfolio, asset, riskManager,
			new PortfolioDriverEmergCloseLong(portfolio, asset, timeout),
			new PortfolioDriverEmergCloseShort(portfolio, asset, timeout)
		);
	}
	
	public PortfolioDriver(Portfolio portfolio, Asset asset,
			RiskManager riskManager,
			PortfolioDriverEmergClosePosition closeLong,
			PortfolioDriverEmergClosePosition closeShort)
	{
		this.port = portfolio;
		this.rm = riskManager;
		this.asset = asset;
		this.closeLong = closeLong;
		this.closeShort = closeShort;
	}
	
	public void setSlippage(int value) {
		slippage = value;
		logger.info("Slippage changed to: {}", value);
	}
	
	public Asset getAsset() {
		return asset;
	}
	
	public Portfolio getPortfolio() {
		return port;
	}
	
	public RiskManager getRiskManagement() {
		return rm;
	}
	
	/**
	 * Получить активную заявку на покупку.
	 * 
	 * Возвращает экземпляр заявки на покупку, если такая заявка была выставлена
	 * ранее. Если заявка не была выставлена, была исполнена или снята, то
	 * возвращает null. Для исполненных стоп-заявок возвращается экземпляр
	 * связаной заявки, если связанная заявка активна. 
	 * 
	 * @return экземпляр заявки 
	 */
	public Order getBuy() {
		if ( buy == null ) return null;
		switch ( buy.getStatus() ) {
		case Order.PENDING:
		case Order.ACTIVE:
			return buy;
		}
		if ( buy.isStopOrder() && buy.getStatus() == Order.FILLED ) {
			try {
				Order relatedOrder = buy.getRelatedOrder(); 
				switch ( relatedOrder.getStatus() ) {
				case Order.PENDING:
				case Order.ACTIVE:
					return relatedOrder;
				}
			} catch ( OrderException e ) {
				logger.error("This should not happen", e);
				return buy;
			}
		}
		return null;
	}
	
	/**
	 * Получить активную заявку на продажу.
	 * 
	 * Возвращает экземпляр заявки на продажу, если такая заявка была выставлена
	 * ранее. Если заявка не была выставлена, была исполнена или снята, то
	 * возвращает null. Для исполненных стоп-заявок возвращается экземпляр
	 * связаной заявки, если связаная заявка активна.
	 * 
	 * @return экземпляр заявки
	 */
	public Order getSell() {
		if ( sell == null ) return null;
		switch ( sell.getStatus() ) {
		case Order.PENDING:
		case Order.ACTIVE:
			return sell;
		}
		if ( sell.isStopOrder() && sell.getStatus() == Order.FILLED ) {
			try {
				Order relatedOrder = sell.getRelatedOrder();
				switch ( relatedOrder.getStatus()) {
				case Order.PENDING:
				case Order.ACTIVE:
					return relatedOrder;
				}
			} catch ( OrderException e ) {
				logger.error("This should not happen", e);
				return buy;
			}
		}
		return null;
	}

	/**
	 * Снять все активные заявки.
	 * 
	 * Снимает заявки всех типов, относящиеся к данному портфелю.
	 * 
	 * @throws PortfolioDriverException проброс исключения портфеля
	 * @throws InterruptedException поток был прерван во время транзакции
	 */
	public void killAll() throws PortfolioDriverException,InterruptedException {
		try {
			port.killAll();
		} catch ( PortfolioException e ) {
			throw new PortfolioDriverException(e);
		}
		buy = null;
		sell = null;
	}
	
	/**
	 * Снять все заявки на покупку.
	 * 
	 * Снимает заявки на покупку, относящиеся к данному портфелю.
	 * 
	 * @throws PortfolioDriverException проброс исключения портфеля
	 * @throws InterruptedException поток был прерван во время транзакции
	 */
	public void killBuy()
		throws PortfolioDriverException,InterruptedException
	{
		try {
			port.killAll(Order.BUY);
		} catch ( PortfolioException e ) {
			throw new PortfolioDriverException(e);
		}
		buy = null;
	}
	
	/**
	 * Снаять все заявки на продажу.
	 * 
	 * Снимает все заявки на продажу, относящиеся к данному портфелю.
	 * 
	 * @throws PortfolioDriverException проброс исключения портфеля
	 * @throws InterruptedException поток был прерван во время транзакции
	 */
	public void killSell()
		throws PortfolioDriverException,InterruptedException
	{
		try {
			port.killAll(Order.SELL);
		} catch ( PortfolioException e ) {
			throw new PortfolioDriverException(e);
		}
		sell = null;
	}
	
	/**
	 * Выставить заявку на увеличение длинной позиции.
	 * 
	 * Все активные заявки на покупку снимаются. Выставляется стоп-заявка на
	 * покупку со стоп-ценой меньшей указанной на priceStep * shift. В случае
	 * невозможности выставить заявку, ошибки игнорируются.
	 * 
	 * @param price цена заявки
	 * @param msg комментарий заявки
	 * @throws PortfolioDriverException не удалось снять заявки на покупку
	 * @throws InterruptedException поток прерван во время выполнения транзакции
	 */
	public void addLong(double price, String msg)
		throws PortfolioDriverException,InterruptedException
	{
		int qty = rm.getLongSize(price);
		if ( qty > 0 ) {
			try {
				// открытие длиной - это покупка -> стоп-цена ниже указанной
				double stopPrice = price - (asset.getPriceStep() * slippage);
				buyIfDifferent(qty, stopPrice, price, msg);
			} catch ( OrderException e ) {
				throw new PortfolioDriverException(e.getMessage(), e);
			} catch ( AssetException e ) {
				throw new PortfolioDriverException(e.getMessage(), e);
			} catch ( PortfolioException e ) {
				logger.warn("Couldn't increase long position", e);
				try {
					port.killAll(Order.BUY);
				} catch ( PortfolioException e1 ) {
					logger.error("Cleanup failed: {}", e1.getMessage(), e1);
				}
			}
		} else {
			killBuy();
		}
	}
	
	/**
	 * Выставить заявку на увеличение короткой позиции.
	 * 
	 * Все активные заявки на продажу снимаются. Выставляется стоп-заявка на
	 * продажу со стоп-ценой большей указанной на priceStep * shift. Ошибки при
	 * выставлении заявки игнорируются.
	 * 
	 * @param price цена заявки
	 * @param msg комментарий заявки
	 * @throws PortfolioDriverException не удалось снять активные заявки
	 * @throws InterruptedException поток прерван во время выполнения транзакции
	 */
	public void addShort(double price, String msg)
		throws PortfolioDriverException,InterruptedException
	{
		int qty = rm.getShortSize(price);
		if ( qty > 0 ) {
			try {
				// открытие короткой - это продажа -> стоп-цена выше указаной
				double stopPrice = price + (asset.getPriceStep() * slippage);
				sellIfDifferent(qty, stopPrice, price, msg);
			} catch ( OrderException e ) {
				throw new PortfolioDriverException(e.getMessage(), e);
			} catch ( AssetException e ) {
				throw new PortfolioDriverException(e.getMessage(), e);
			} catch ( PortfolioException e ) {
				logger.warn("Couldn't increase short position", e);
				try {
					port.killAll(Order.SELL);
				} catch ( PortfolioException e1 ) {
					logger.error("Cleanup failed: {}", e1.getMessage(), e1);
				}
			}
		} else {
			killSell();
		}
	}
	
	private void sellIfDifferent(int qty, double stopPrice,
								 double price, String msg)
		throws	OrderException,
				AssetException,
				PortfolioException,
				InterruptedException,
				PortfolioDriverException
	{
		Order curr = getSell();
		if ( curr != null ) {
			Order next = new OrderImpl(curr.getId(), Order.SELL, qty,
				asset.roundPrice(stopPrice), asset.roundPrice(price),
				msg);
			if ( curr.getStatus() == Order.ACTIVE ) {
				next.activate();
			}
			if ( curr.equals(next) ) {
				return;
			}
		}
		killSell();
		sell = port.stopSell(qty, stopPrice, price, msg);
	}
	
	private void buyIfDifferent(int qty, double stopPrice,
								double price, String msg)
		throws	OrderException,
				AssetException,
				PortfolioException,
				InterruptedException,
				PortfolioDriverException
	{
		Order curr = getBuy();
		if ( curr != null ) {
			Order next = new OrderImpl(curr.getId(), Order.BUY, qty,
				asset.roundPrice(stopPrice), asset.roundPrice(price),
				msg);
			if ( curr.getStatus() == Order.ACTIVE ) {
				next.activate();
			}
			if ( curr.equals(next) ) {
				return;
			}
		}
		killBuy();
		buy = port.stopBuy(qty, stopPrice, price, msg);
	}
	
	/**
	 * Выставить заявку на закрытие длинной позиции.
	 * 
	 * Все активные заявки на продажу снимаются. Выставляется стоп-заявка на
	 * продажу со стоп-ценой большей указанной на  priceStep * shift. Ошибки
	 * выставления заявки в данном случае критичны и все исключения портфеля
	 * пробрасываются.
	 * 
	 * @param price цена заявки
	 * @param msg комментарий заявки
	 * @throws PortfolioDriverException проброс ошибки портфеля
	 * @throws InterruptedException поток прерван во время выполнения транзакции
	 */
	public void closeLong(double price, String msg)
		throws PortfolioDriverException,InterruptedException
	{
		
		try {
			int qty = port.getPosition();
			if ( qty > 0 ) {
				double stopPrice = price + (asset.getPriceStep() * slippage);
				// закрытие длинной - это продажа -> стоп-цена выше указанной
				sellIfDifferent(qty, stopPrice, price, msg);
			} else {
				killSell();
			}
		} catch ( OrderException e ) {
			throw new PortfolioDriverException(e.getMessage(), e);
		} catch ( AssetException e ) {
			throw new PortfolioDriverException(e.getMessage(), e);
		} catch ( PortfolioException e ) {
			throw new PortfolioDriverException(e);
		}
	}
	
	/**
	 * Выставить заявку на закрытие короткой позиции.
	 * 
	 * Все активные заявки на покупку снимаются. Выставляется стоп-заявка на
	 * покупку со стоп-ценой меньше указанной на priceStep * shift. Выставление
	 * данной заявки критично, так что все исключения портфеля пробрасываются. 
	 * 
	 * @param price цена заявки
	 * @param comment комментарий заявки
	 * @throws PortfolioDriverException не удалось выставить заявку
	 * @throws InterruptedException поток прерван во время выполнения транзакции
	 */
	public void closeShort(double price, String msg)
		throws PortfolioDriverException,InterruptedException
	{
		
		try {
			int qty = -port.getPosition();
			if ( qty > 0 ) {
				double stopPrice = price - (asset.getPriceStep() * slippage);
				// закрытие короткой - это покупка -> стоп-цена ниже указаной
				buyIfDifferent(qty, stopPrice, price, msg);
			} else {
				killBuy();
			}
		} catch ( OrderException e ) {
			throw new PortfolioDriverException(e.getMessage(), e);
		} catch ( AssetException e ) {
			throw new PortfolioDriverException(e.getMessage(), e);
		} catch ( PortfolioException e ) {
			throw new PortfolioDriverException(e);
		}
	}
	
	/**
	 * Проверка состояния портфеля.
	 * 
	 * @return возвращает true, если открыта длиная позиция.
	 */
	public boolean isLong() throws PortfolioDriverException {
		return pos() > 0;
	}
	
	/**
	 * Проверка состояния портфеля.
	 * 
	 * @return возвращает true, если открыта короткая позиция.
	 */
	public boolean isShort() throws PortfolioDriverException {
		return pos() < 0;
	}
	
	/**
	 * Проверка состояния портфеля.
	 * 
	 * @return возвращает true, если позиция не открыта.
	 */
	public boolean isNeutral() throws PortfolioDriverException {
		return pos() == 0;
	}
	
	private int pos() throws PortfolioDriverException {
		try {
			return port.getPosition();
		} catch ( PortfolioException e ) {
			throw new PortfolioDriverException(e);
		}
	}

	/**
	 * Закрыть короткую позицию немедленно.
	 * 
	 * Выполняет {@link #CLOSE_IMMEDIATELY_RETRIES} попыток откупить короткую
	 * позицию по рыночной цене. На каждой итерации выполняется запрос к
	 * объекту-стратегии закрытия до тех пор, пока этот объект не вернет успех.
	 * 
	 * @param msg комментарий заявок
	 * @throws PortfolioDriverException максимальное количество попыток
	 * @throws InterruptedException поток прерван во время выполнения транзакции
	 */
	public void closeShortImmediately(String msg)
		throws PortfolioDriverException,InterruptedException
	{
		for ( int shift = 1; shift <= CLOSE_IMMEDIATELY_RETRIES; shift ++ ) {
			try {
				if ( closeShort.tryClose(shift, msg) ) {
					return;
				}
			} catch ( PortfolioException e ) {
				throw new PortfolioDriverException("Emergency close failed", e);
			}
		}
		throw new PortfolioDriverException("Max retries");
	}
	
	/**
	 * Закрыть длиную позицию немедленно.
	 * 
	 * Выполняет {@link #CLOSE_IMMEDIATELY_RETRIES} попыток продать длиную
	 * позицию по рыночной цене. На каждой итерации выполняется запрос к объекту
	 * стратегии закрытия до тех пор, пока этот объект не вернет успех. 
	 * 
	 * @param msg комментарий заявок
	 * @throws PortfolioDriverException максимальное количество попыток
	 * @throws InterruptedException поток прерван во время выполнения транзакции
	 */
	public void closeLongImmediately(String msg)
		throws PortfolioDriverException,InterruptedException
	{
		for ( int shift = 1; shift <= CLOSE_IMMEDIATELY_RETRIES; shift ++ ) {
			try {
				if ( closeLong.tryClose(shift, msg) ) {
					return;
				}
			} catch ( PortfolioException e ) {
				throw new PortfolioDriverException("Emergency close failed", e);
			}
		}
		throw new PortfolioDriverException("Max retries");
	}

}
