package ru.prolib.aquila.ChaosTheory;

import ru.prolib.aquila.ta.ds.MarketData;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

/**
 * Конструктор локального сервиса активов.
 * Все параметры актива, кроме цены, описываются конфигурацией сервиса.
 * В качестве цены актива используется значение цены закрытия периода
 * сервиса {@link MarketData}. Так как сервис данных о торгах предоставляет
 * информацию только по отдельному активу, локальный сервис активов может
 * содержать только один актив.
 * 
 * Пример XML-узла конфигурации сервиса (все атрибуты обязательны):
 * 
 *	<Assets type="local" code="RIH2" class="SPBFUT" step="5" scale="0"
 *		initialMarginFactor="0.15" priceStepMoney="0.2"/>
 *
 */
public class AssetsBuilderLocal implements ServiceBuilder {
	public static final String CODE_ATTR  = "code";
	public static final String CLASS_ATTR = "class";
	public static final String STEP_ATTR  = "step";
	public static final String SCALE_ATTR = "scale";
	public static final String INITIAL_MARGIN_FACT_ATTR = "initialMarginFactor";
	public static final String PRICE_STEP_MONEY_ATTR = "priceStepMoney";
	
	private final ServiceBuilderHelper helper;
	
	public AssetsBuilderLocal(ServiceBuilderHelper helper) {
		super();
		this.helper = helper;
	}
	
	public AssetsBuilderLocal() {
		this(new ServiceBuilderHelperImpl());
	}

	@Override
	public Object create(ServiceLocator locator,
						 HierarchicalStreamReader reader)
		throws ServiceBuilderException, ServiceLocatorException
	{
		AssetsImpl assets = new AssetsImpl();
		AssetLocal asset = new AssetLocal(helper.getString(CODE_ATTR, reader),
					helper.getString(CLASS_ATTR, reader),
					helper.getDouble(STEP_ATTR, reader),
					helper.getInt(SCALE_ATTR, reader),
					helper.getDouble(INITIAL_MARGIN_FACT_ATTR, reader),
					helper.getDouble(PRICE_STEP_MONEY_ATTR, reader));
		try {
			asset.startService(locator.getMarketData());
			assets.add(asset);
		} catch ( AssetException e ) {
			throw new ServiceBuilderException(e);
		} catch ( AssetsAlreadyExistsException e ) {
			throw new ServiceBuilderException(e);
		}
		return assets;
	}

}
