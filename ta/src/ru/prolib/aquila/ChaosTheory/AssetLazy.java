package ru.prolib.aquila.ChaosTheory;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Поздняя загрузка актива.
 * 
 * Выполнен в виде декоратора актива. На запрос кода отвечает имеющимся кодом.
 * При первом запросе к другим атрибутам актива, обращается к хранилищу с
 * запросом актива, соответствующего установленному коду. Если хранилище
 * не содержит требуемого актива, то запрос атрибута актива завершается
 * исключением {@link AssetLazyLoadException}. Если хранилище находит требуемый
 * актив, то этот и последующие запросы к атрибутам актива будут направляться
 * реальному активу, полученному из хранилища.
 */
public class AssetLazy extends Observable implements Asset,Observer {
	private static final Logger logger = LoggerFactory.getLogger(AssetLazy.class);
	private final Assets assets;
	private final String assetCode;
	private Asset asset;
	
	public AssetLazy(Assets assets, String assetCode) {
		super();
		if ( assets == null ) {
			throw new NullPointerException("Assets should be not null");
		}
		if ( assetCode == null ) {
			throw new NullPointerException("Asset code should be not null");
		}
		this.assets = assets;
		this.assetCode = assetCode;
		assets.addObserver(this);
	}
	
	private Asset getAsset() throws AssetLazyLoadException {
		if ( asset != null ) {
			return asset;
		}
		try {
			asset = assets.getByCode(assetCode);
			asset.addObserver(this);
			assets.deleteObserver(this);
			logger.debug("Asset {} actually loaded. All notifications will " +
					"delegated to my listeners.", assetCode);
			return asset;
		} catch ( AssetsException e ) {
			throw new AssetLazyLoadException(assetCode, e);
		}
	}

	@Override
	public String getAssetCode() {
		return assetCode;
	}

	@Override
	public String getClassCode() throws AssetException {
		return getAsset().getClassCode();
	}

	@Override
	public double getPriceStep() throws AssetException {
		return getAsset().getPriceStep();
	}

	@Override
	public int getPriceScale() throws AssetException {
		return getAsset().getPriceScale();
	}

	@Override
	public double getPrice() throws AssetException {
		return getAsset().getPrice();
	}

	@Override
	public double getPriceStepMoney() throws AssetException {
		return getAsset().getPriceStepMoney();
	}
	
	@Override
	public double getInitialMarginMoney() throws AssetException {
		return getAsset().getInitialMarginMoney();
	}

	@Override
	public double getEstimatedPrice() throws AssetException {
		return getAsset().getEstimatedPrice();
	}
	
	@Override
	public String formatPrice(double price) throws AssetException {
		return getAsset().formatPrice(price);
	}

	@Override
	public double priceToMoney(double price) throws AssetException {
		return getAsset().priceToMoney(price);
	}

	@Override
	public double roundPrice(double price) throws AssetException {
		return getAsset().roundPrice(price);
	}

	@Override
	public void update(Observable o, Object arg) {
		if ( o == assets ) {
			Asset asset = (Asset) arg;
			if ( asset.getAssetCode().equals(assetCode) ) {
				try {
					getAsset(); // force load
				} catch (AssetLazyLoadException e) {
					logger.error("Unexpected exception: ", e);
				}
			}
		} else if ( asset != null && o == asset ) {
			setChanged();
			notifyObservers(arg);
		}
	}
	
	@Override
	public void addObserver(Observer o) {
		if ( asset == null ) {
			logger.warn("Listeners will not receive any notifications " +
					"until asset {} not actually loaded.", assetCode);
		}
		super.addObserver(o);
	}

}
