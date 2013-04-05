package ru.prolib.aquila.ChaosTheory;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Observable;

public class AssetImpl extends Observable implements Asset {
	private final String assetCode;
	private final String classCode;
	private final double priceStep;
	private final int priceScale;
	private final DecimalFormat priceFormat;
	private Double price = null;
	private Double priceStepMoney = null;
	private Double initialMarginMoney = null;
	private Double estimatedPrice = null;
	
	public AssetImpl(String assetCode, String classCode,
			double priceStep, int priceScale)
	{
		super();
		this.assetCode = assetCode;
		this.classCode = classCode;
		this.priceStep = priceStep;
		this.priceScale = priceScale;
		
		if ( priceScale > 0 ) {
			DecimalFormatSymbols symbols = new DecimalFormatSymbols();
			symbols.setDecimalSeparator('.');
			priceFormat = new DecimalFormat("#0." +
				new String(new char[priceScale]).replace("\0", "0"), symbols);
		} else {
			priceFormat = null;
		}
	}

	@Override
	public String getAssetCode() {
		return assetCode;
	}

	@Override
	public String getClassCode() throws AssetException {
		return classCode;
	}

	@Override
	public double getPriceStep() throws AssetException {
		return priceStep;
	}

	@Override
	public int getPriceScale() throws AssetException {
		return priceScale;
	}

	@Override
	public synchronized double getPrice() throws AssetException {
		if ( price == null ) {
			throw new AssetValueNotAvailableException("price");
		}
		return price;
	}

	@Override
	public String formatPrice(double price) throws AssetException {
		price = roundPrice(price);
		if ( priceScale == 0 ) {
			return String.valueOf(new Double(price).longValue());
		}
		return priceFormat.format(price);
	}
	
	public synchronized void updatePrice(double price) {
		this.price = price;
		setChanged();
	}
	
	public synchronized void updatePriceStepMoney(double money) {
		if ( priceStepMoney == null || money != priceStepMoney ) {
			priceStepMoney = money;
			setChanged();
		}
	}
	
	public synchronized void updateInitialMarginMoney(double margin) {
		if ( initialMarginMoney == null || margin != initialMarginMoney ) {
			initialMarginMoney = margin;
			setChanged();
		}
	}

	@Override
	public synchronized double getPriceStepMoney() throws AssetException {
		if ( priceStepMoney == null ) {
			throw new AssetValueNotAvailableException("priceStepMoney");
		}
		return priceStepMoney;
	}

	@Override
	public double priceToMoney(double price) throws AssetException {
		double money = roundPrice(price) / priceStep * getPriceStepMoney();
		return Math.round(money * 100.0d) / 100.0d;
	}

	@Override
	public double roundPrice(double price) throws AssetException {
		price = Math.round(price / priceStep) * priceStep;
		if ( priceScale == 0 ) {
			return Math.round(price);
		}
		double mul = priceScale * 10;
		return Math.round(price * mul) / mul;
	}

	@Override
	public synchronized double getInitialMarginMoney() throws AssetException {
		if ( initialMarginMoney == null ) {
			throw new AssetValueNotAvailableException("initialMarginMoney");
		}
		return initialMarginMoney;
	}

	@Override
	public synchronized double getEstimatedPrice() throws AssetException {
		if ( estimatedPrice == null ) {
			throw new AssetValueNotAvailableException("estimatedPrice");
		}
		return estimatedPrice;
	}
	
	public synchronized void updateEstimatedPrice(double price) {
		if ( estimatedPrice == null || price != estimatedPrice ) {
			estimatedPrice = price;
			setChanged();
		}
	}

}
