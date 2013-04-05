package ru.prolib.aquila.ta.ds;

import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.ta.ValueException;
import ru.prolib.aquila.ta.ValueListImpl;
import ru.prolib.aquila.util.AlignDateMinute;

/**
 * Источник данных с группировкой данных другого источника.
 * Используется для формирования баров в большем таймфрейме на основе баров
 * в меньшем таймфрейме. Например из минутных баров делает пятиминутные.
 * 
 * Работает следующим образом. При создании регистрирует типовые значения
 * источника данных. Значения обновляются через датасет. Датасет инициируется
 * нужным баром через группирующий райтер баров. Объект подписывается на
 * уведомления от исходного источника данных. Запросы на подготовку и обновление
 * делегируются исходному источнику данных.  Далее объект действует
 * исключетельно как обработчик событий исходного источника. При поступлении
 * события формирует новый бар на основе последних типовых значений. Полученный
 * бар передается райтеру. Если райтер на добавление бара ответил сохранением
 * данных (вернул true), то данный экземпляр выполняет обновление значений
 * и уведомляет своих наблюдателей.
 */
public class MarketDataGrouper extends MarketDataCommon
	implements Observer
{
	private static final Logger logger = LoggerFactory.getLogger(MarketDataGrouper.class);
	private final MarketData data;
	private final BarWriter writer;

	public MarketDataGrouper(MarketData srcData, int period) {
		super(new ValueListImpl());
		DataSetBar dataset = new DataSetBar();
		writer = new BarWriterGrouper(new AlignDateMinute(period),
				new BarWriterDataSetBar(dataset));
		data = srcData;
		try {
			addValues(dataset);
		} catch ( ValueException e ) {
			throw new IllegalStateException("Unexpected exception: " +
					e.getMessage(), e);
		}
		data.addObserver(this);
		logger.info("Configured for {} minutes period", period);
	}
	
	private void addValues(DataSet dataset) throws ValueException {
		addValue(new DataSetDate(MarketData.TIME, dataset, MarketData.TIME));
		addValue(new DataSetDouble(MarketData.OPEN, dataset, MarketData.OPEN));
		addValue(new DataSetDouble(MarketData.HIGH, dataset, MarketData.HIGH));
		addValue(new DataSetDouble(MarketData.LOW, dataset, MarketData.LOW));
		addValue(new DataSetDouble(MarketData.CLOSE, dataset, MarketData.CLOSE));
		addValue(new DataSetDouble(MarketData.VOL, dataset, MarketData.VOL));
		addMedian(MarketData.HIGH, MarketData.LOW, MarketData.MEDIAN);
	}

	@Override
	public int getLevel() {
		return data.getLevel() + 1;
	}

	@Override
	public MarketData getSource() throws MarketDataException {
		return data;
	}

	@Override
	public void prepare() throws ValueException {
		data.prepare();
	}

	@Override
	public void update() throws ValueException {
		// TODO: выполнять запрос до тех пор:
		// 1. Пока количество баров у нас не изменится
		// 2. Пока количество баров у источника изменяется
		int srcBefLength = data.getLength();
		int meBefLength = getLength();
		while ( true ) {
			data.update();
			int srcAftLength = data.getLength();
			if ( srcAftLength == srcBefLength ) {
				break;
			} else {
				srcBefLength = srcAftLength;
			}
			if ( getLength() != meBefLength ) {
				break;
			}
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if ( arg0 == data ) {
			try {
				if ( writer.addBar(new Candle(data.getTime().get(),
					data.getOpen().get(),
					data.getHigh().get(),
					data.getLow().get(),
					data.getClose().get(),
					data.getVolume().get().longValue())) )
				{
					values.update();
					logger.debug("New bar was formed: {}", getTime().get());
					setChanged();
					notifyObservers();
				} else {
					
				}
			} catch ( BarWriterException e ) {
				error(e);
			} catch ( ValueException e ) {
				error(e);
			}
		}
	}
	
	private void error(Exception e) {
		Object params[] = null;
		if ( logger.isDebugEnabled() ) {
			params = new Object[] { e.getMessage(), e };
		} else {
			params = new Object[] { e.getMessage() };
		}
		logger.error("{}", params);
	}

}
