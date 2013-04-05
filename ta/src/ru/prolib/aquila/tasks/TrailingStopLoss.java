package ru.prolib.aquila.tasks;

import java.util.Observable;
import java.util.Observer;

import ru.prolib.aquila.ChaosTheory.Asset;
import ru.prolib.aquila.ChaosTheory.AssetException;
import ru.prolib.aquila.ChaosTheory.Order;
import ru.prolib.aquila.ChaosTheory.OrderException;
import ru.prolib.aquila.ChaosTheory.Portfolio;
import ru.prolib.aquila.ChaosTheory.PortfolioException;
import ru.prolib.aquila.ChaosTheory.ServiceLocator;
import ru.prolib.aquila.ChaosTheory.ServiceLocatorException;
import ru.prolib.aquila.ta.ds.MarketData;

/**
 * Скользящий стоп-лосс.
 * 
 * Скользящий стоп-лосс подразумевает сдвиг защитной стоп-заявки в случае,
 * если цена сдвигается в направлении открытой позиции. Для скользящего
 * стоп-лосса помимо начально стоп-цены используются параметр защитный спред.
 * Защитный спред указывается как и проскальзывание - в шагах цены. Спред
 * определяет расстояние от текущей цены до стоп-цены выставляемой защитной
 * заявки. Если цена двигается в нужном направлении и достигла значения спред
 * * 1.5, то защитная стоп-заявка переставляется на уровень текущая цена -
 * спред. Если цена двигается в пределах спреда, то защитная заявка остается
 * без изменений. Как только цена достигает уровень стоп-цены, активизируется
 * лимитная заявка на закрытие позиции. Если после этого цена ускачет в
 * противоположном открытой позиции направлении более чем на значение спреда,
 * либо если лимитная заявка не исполняется более 2 баров, то инициируется
 * процедура экстренного закрытия позиции.
 * 
 * Фактически, данная задача может быть завершена отменой только принудительно.
 * В случае вызова {@link #cancel()} все активные заявки снимаются и
 * ответственность за контроль над позицией с задачи снимается. В ином случае,
 * задача сопровождает позицию до тех пор, пока она не закроется по описанным
 * выше условиям.
 * 
 * 2012-02-14
 * $Id: TrailingStopLoss.java 201 2012-04-03 14:45:43Z whirlwind $
 */
abstract public class TrailingStopLoss
	extends TaskUsesLocator implements Observer
{
	protected final Portfolio portfolio;
	protected final MarketData data;
	protected final Asset asset;
	private Order stopOrder;
	private Order limitOrder;
	private String comment;
	private int qty = 1;
	private int spread = 15;
	private int activatedAt = -1;
	protected int slippage = 2;
	protected Double price;
	protected double spreadPoints = 0;

	public TrailingStopLoss(ServiceLocator locator) {
		super(locator);
		try {
			portfolio = locator.getPortfolio();
			data = locator.getMarketData();
			asset = portfolio.getAsset();
		} catch ( ServiceLocatorException e ) {
			debugException("Initialize failed", e);
			throw new RuntimeException("Initialize failed", e);
		}
	}
	
	/**
	 * Установить начальную цену.
	 * 
	 * Указание этого параметра обязательно. Если начальное значение цены не
	 * указано, то запуск задачи завершится отменой задачи. Начальная цена - это
	 * цена лимтиной заявки, которая будет сформирована при активации стоп
	 * заявки. В качестве цены может быть указано неокругленное в соответствии
	 * с параметрами актива значения. После запуска задачи цена автоматически
	 * приводится в соответствии с требованиями актива.
	 * 
	 * @param price
	 * @return
	 */
	public synchronized TrailingStopLoss price(double price) {
		this.price = price;
		return this;
	}
	
	/**
	 * Установить величину защитного спреда.
	 * 
	 * Защитный спред определяет допустимый коридор хода цены. Стоп-заявка
	 * переставляется, если цена локально достигла смещения в 1.5 раза от
	 * величины защитного спреда. Значение по умолчанию 15 шагов цены.  
	 * 
	 * @param spread
	 * @return
	 */
	public synchronized TrailingStopLoss spread(int spread) {
		this.spread = spread;
		return this;
	}
	
	/**
	 * Установить запас проскальзывания.
	 * 
	 * Данный параметр определяет сколько шагов цены резервируется под вероятное
	 * проскальзывание при активации защитной заявки. Значение по умолчанию 2
	 * шага цены.
	 * 
	 * @param value
	 * @return
	 */
	public synchronized TrailingStopLoss slippage(int value) {
		this.slippage = value;
		return this;
	}
	
	/**
	 * Установить количество заявки.
	 * 
	 * Значение по умолчанию 1.
	 * 
	 * @param quantity
	 * @return
	 */
	public synchronized TrailingStopLoss qty(int quantity) {
		this.qty = quantity;
		return this;
	}
	
	/**
	 * Установить комментарий заявки.
	 * 
	 * По умолчанию - не определено.
	 * 
	 * @param comment
	 * @return
	 */
	public synchronized TrailingStopLoss comment(String comment) {
		this.comment = comment;
		return this;
	}

	@Override
	public synchronized void start() {
		if ( ! pending() ) {
			return;
		}
		if ( price == null ) {
			setCancelled();
			return;
		}
		try {
			spreadPoints = asset.getPriceStep() * spread;
			initialLocalPrice(asset.getPrice());
		} catch ( AssetException e ) {
			debugException("Cannot obtain asset info", e);
			cleanup();
			setCancelled();
			return;
		}
		asset.addObserver(this);
		setStarted();
		replaceStopOrder();
	}

	@Override
	public synchronized void cancel() {
		if ( started() ) {
			cleanup();
			setCancelled();
		}
	}
	
	/**
	 * Выполнить постановку/замену стоп-заявки.
	 */
	protected void replaceStopOrder() {
		try {
			price = asset.roundPrice(price);
			killOrders();
			stopOrder = createOrder(qty, price,
				slippage * asset.getPriceStep(), comment);
			stopOrder.addObserver(this);
		} catch ( AssetException e ) {
			debugException("Cannot round price", e);
			cleanup();
			setCancelled();
		} catch ( PortfolioException e ) {
			debugException("Cannot place stop-order", e);
			cleanup();
			setCancelled();
		} catch ( InterruptedException e ) {
			debugException("Unexpected interruption", e);
			setCancelled();
			throw new RuntimeException("Thread interrupted", e);
		}
	}
	
	private void cleanup() {
		data.deleteObserver(this);
		asset.deleteObserver(this);
		killOrders();
	}
	
	private void killOrders() {
		if ( stopOrder != null ) {
			stopOrder.deleteObserver(this);
		}
		if ( limitOrder != null ) {
			limitOrder.deleteObserver(this);
		}
		try {
			portfolio.killAll();
		} catch ( PortfolioException e ) {
			debugException("Cannot kill order", e);
		} catch ( InterruptedException e ) {
			debugException("Unexpeted interruption", e);
			throw new RuntimeException("Thread interrupted", e);
		}
	}
	
	@Override
	public synchronized void update(Observable o, Object arg) {
		if ( ! started() ) {
			return;
		}
		// Изменение статуса стоп-заявки. Статус стоп-заявки может измениться
		// в двух случаях: 
		// 1) заявка была активирована естественным образом;
		// 2) заявка была отменена в результате постороннего приказа на снятие
		//	  вообще всех стоп-заявок.
		// В случае когда мы снимаем заявку после выставления более точной,
		// мы здесь быть не можем, так как перед снятием предыдущей заявки
		// мы от нее отписываемся.
		if ( o == stopOrder ) {
			if ( stopOrder.isFilled() ) {
				try {
					// Стоп-заявка была исполнена. Это значит, что
					// ловить изменения цены теперь нет смысла, так как
					// стоп-заявка не будет больше переставляться.
					asset.deleteObserver(this);
					stopOrder.deleteObserver(this);
					// Сохраняем и начинаем слушать созданный лимитный ордер.
					limitOrder = stopOrder.getRelatedOrder();
					limitOrder.addObserver(this);
					// Начинаем наблюдать за количеством баров, в течение
					// которых висит заявка. Будем снимать, если ордер зависнет
					// на пару баров - это значит что цена пролетела очень
					// далеко и надо закрывать позу немедленно.
					activatedAt = data.getLastBarIndex();
					data.addObserver(this);
				} catch ( OrderException e ) {
					debugException("Cannot obtain related order", e);
					setCancelled();
					cleanup();
				}
			} else {
				// Стоп-заявка была снята. Если получили это уведомление,
				// то снята она не нами, а кем то снаружи. Это значит что
				// необходимо завершить любую работу и подчистить заявки.
				setCancelled();
				cleanup();
			}
		
		// Изменения статуса лимитной заявки. Статус может измениться только в
		// двух случаях:
		// 1) заявка была естественным образом исполнена;
		// 2) заявка была отменена в результате постороннего приказа на снятие
		//	  всех активных заявок.
		} else if ( o == limitOrder ) {
			if ( limitOrder.isFilled() ) {
				// Все отлично - лимитная заявка исполнена.
				setCompleted();
			} else {
				// Мы не можем здесь быть в результате снятия лимитной заявки
				// нами же, так как при отмене заявки мы отписываемся от нее.
				// Здесь мы можем быть только в том случае, если кто-то снаружи
				// ее отменил. В этом случае завершаем всякую работу и 
				// заканчиваем отменой задачи.
				setCancelled();
				cleanup();
			}
		
		// Уведомление о формировании нового бара начинает приходить только
		// после выставления лимитной заявки. Нужно проверить сколько баров
		// висит лимитная заявка.
		} else if ( o == data ) {
			if ( data.getLastBarIndex() - activatedAt >= 2 ) {
				// Слишком долго исполняется лимитная заявка.
				// Снимаем лимитную заявку и закрываем экстренно.
				cleanup();
				Task close = new CloseImmediately(locator);
				close.start();
				if ( close.completed() ) {
					setCompleted();
				} else {
					setCancelled();
				}
			}

		// Уведомления об обновление цены приходит
		// пока стоит активная стоп-заявка. 
		} else if ( o == asset ) {
			// Пересчитываем локальный пик/дно цены.
			try {
				if ( updateLocalPrice(asset.getPrice()) ) {
					replaceStopOrder();
				}
			} catch ( AssetException e ) {
				debugException("Cannot obtain asset info", e);
			}
		}
		
	}
	
	/**
	 * Выставить стоп-заявку с указанными параметрами
	 * 
	 * Данный метод реализуется наследниками и должен обеспечивать выставление
	 * заявки, ориентированной в нужном направлении.
	 * 
	 * @param qty количество заявки
	 * @param price цена заявки (цена, которая будет использоваться в лимитной)
	 * @param slippage запас скольжения в пунктах!!! цены (не в шагах)
	 * @param comment комментарий заявки
	 * @return экземпляр соответствующей стоп-заявки
	 * @throws PortfolioException
	 * @throws InterruptedException
	 */
	abstract protected Order createOrder(int qty, double price,
										 double slippage, String comment)
		throws PortfolioException,InterruptedException;
	
	/**
	 * Обработать обновление цены актива.
	 * 
	 * Данный метод реализуется наследниками и должен обеспечивать обновление
	 * локального максимума/минимума цены, в зависимости от направления
	 * сопровождаемой позиции. В случае необходимости замены, метод должен
	 * выставить новую цену стоп-заявки и вернуть true. 
	 * 
	 * @param currentPrice текущая цена актива
	 * @return true - необходимо переставить заявку, false - оставить заявку
	 * без изменений
	 */
	abstract protected boolean updateLocalPrice(double currentPrice);
	
	/**
	 * Установить начальное значение локальной цены.
	 * 
	 * @param price текущая цена актива.
	 */
	abstract protected void initialLocalPrice(double price);

}
