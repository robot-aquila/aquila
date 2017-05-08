package ru.prolib.aquila.utils.experimental.charts.fxcharts;

import javafx.application.Platform;
import ru.prolib.aquila.core.Event;
import ru.prolib.aquila.core.EventListener;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.utils.experimental.charts.ChartPanel;
import ru.prolib.aquila.utils.experimental.charts.formatters.InstantLabelFormatter;
import ru.prolib.aquila.utils.experimental.charts.indicators.IndicatorChartLayer;
import ru.prolib.aquila.utils.experimental.charts.indicators.IndicatorSettings;
import ru.prolib.aquila.utils.experimental.charts.indicators.calculator.Calculator;
import ru.prolib.aquila.utils.experimental.charts.layers.CandleChartLayer;

import javax.swing.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TiM on 08.05.2017.
 */
public class CBFXChartPanel extends ChartPanel<Instant> implements EventListener {
    private CandleChartLayer candles;
    private List<IndicatorChartLayer> indicators = new ArrayList<>();
    private ObservableSeries<Candle>  candleData;

    public CBFXChartPanel(ObservableSeries<Candle> candleData) {
        setScrollbar(new JScrollBar(JScrollBar.HORIZONTAL));
        this.candleData = candleData;
        addChart("CANDLES");
        candles = new CandleChartLayer();
        addChartLayer("CANDLES", candles);
        candles.setData(candleData);
        setCategoriesLabelFormatter(new InstantLabelFormatter());
        candleData.onAdd().addListener(this);
        candleData.onSet().addListener(this);
        setCurrentPosition(candleData.getLength());
    }

    public IndicatorChartLayer addSmoothLine(Series<Double> data){
        IndicatorSettings indicatorSettings = new IndicatorSettings(new Calculator() {
            @Override
            public String getId() {
                return data.getId();
            }

            @Override
            public String getName() {
                return data.getId();
            }

            @Override
            public Series calculate(Series data) {
                return data;
            }
        }, "");
        IndicatorChartLayer indicator = new IndicatorChartLayer(indicatorSettings);
        indicator.setCategories(new CandleStartTimeSeries(candleData));
        indicator.setData(data);
        indicators.add(indicator);
        addChartLayer("CANDLES", indicator);
        return indicator;
    }

    @Override
    public void onEvent(Event event) {
        if(event.isType(candleData.onAdd()) || event.isType(candleData.onSet())){
            Platform.runLater(()->{
                Instant lastChartCategory = getCategories().get(getCategories().size()-1);
                if(lastChartCategory!=null){
                    if(isCategoryDisplayed(lastChartCategory)){
                        setCurrentPosition(getCurrentPosition()+1);
                    }
                }
            });
        }
    }
}
