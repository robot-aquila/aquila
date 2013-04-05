package ru.prolib.aquila.tasks;

import ru.prolib.aquila.ChaosTheory.*;

/**
 * Задача закрытия открытой позиции по рыночной цене.
 * 
 * При запуске выполняются попытки закрыть позицию. Вызывающий код не получит
 * управления до тех пор, пока задача не будет исполнена или не прекратятся
 * попытки закрытия позиции. 
 * 
 * Очевидно, что пытаться исполнить ее бесконечно не имеет смысла.
 * Если обнаружился какой то серьезный недостаток, типа прекращение получения
 * данных о состоянии портфеля, то долбежка заявками может такую позицию
 * наоткрывать, что мало не покажется. Исходя из этого, количество попыток
 * закрыть позу ограничено.
 */
public class CloseImmediately extends TaskUsesLocator {
	private final Portfolio portfolio;
	private final PortfolioState state;
	private final Asset asset;
	private PortfolioDriverEmergClosePosition l,s;
	private int retries = 20;
	private long timeout = 10000; // ms
	private String comment;
	private int slippage = 2;
	
	/**
	 * Конструктор.
	 * 
	 * @param locator локатор
	 * @throws Exception
	 */
	public CloseImmediately(ServiceLocator locator) {
		super(locator);
		try {
			portfolio = locator.getPortfolio();
			state = locator.getPortfolioState();
			asset = portfolio.getAsset();
		} catch ( ServiceLocatorException e ) {
			debugException("Initialize failed", e);
			throw new RuntimeException("Initialize failed", e);
		}
	}
	
	/**
	 * Определить комментарий заявок.
	 * По умолчанию комментарий неопределен.
	 * 
	 * @param comment
	 * @return this
	 */
	public CloseImmediately comment(String comment) {
		this.comment = comment;
		return this;
	}
	
	/**
	 * Определить максимальное количество попыток.
	 * Значение по умолчанию - 20 попыток.
	 * 
	 * @param maxRetries
	 * @return this
	 */
	public CloseImmediately maxRetries(int maxRetries) {
		this.retries = maxRetries;
		return this;
	}
	
	/**
	 * Определить таймаут операций работы с драйвером.
	 * Значение по умолчанию 10000 миллисекунд.
	 *  
	 * @param ms таймаут в миллисекундах
	 * @return this
	 */
	public CloseImmediately timeout(long ms) {
		this.timeout = ms;
		return this;
	}
	
	/**
	 * Установить величину начального проскальзывания.
	 * Проскальзывание используется для смещения цены заявки в более выгодную
	 * сторону для встречной заявки. Проскальзывание указывается в шагах цены.
	 * Например для RIH2 проскальзывание равное 3 соответствует движению цены
	 * на 15 пунктов. С каждой неудавшейся попыткой закрыть позицию,
	 * проскальнывание увеличивается на единицу. По умолчанию величина
	 * проскальзывания равна 2.
	 * 
	 * @param value
	 * @return
	 */
	public CloseImmediately slippage(int value) {
		slippage = value;
		return this;
	}

	@Override
	public void start() {
		if ( ! pending() ) {
			return;
		}
		setStarted();
		l = new PortfolioDriverEmergCloseLong(portfolio, asset, timeout);
		s = new PortfolioDriverEmergCloseShort(portfolio, asset, timeout);
		boolean result = false;
		for ( int i = 0; i < retries; i ++ ) {
			try {
				int pos = state.getPosition();
				if ( pos > 0 ) {
					result = l.tryClose(i + slippage, comment);
				} else if ( pos < 0 ) {
					result = s.tryClose(i + slippage, comment);
				}
				if ( result ) {
					getClassLogger().debug("Position closed");
					setCompleted();
					return;
				}
			} catch ( PortfolioException e ) {
				debugException("Trying to close position failed", e);
			} catch ( InterruptedException e ) {
				debugException("Unexpected interruption", e);
				throw new RuntimeException("Thread interrupted", e);
			}
		}
		getClassLogger().debug("Too much failed retries. Cancel task.");
		setCancelled();
	}

	@Override
	public void cancel() {
		
	}

}
