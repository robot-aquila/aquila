package ru.prolib.aquila.utils.experimental.charts;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import org.threeten.extra.Interval;
import ru.prolib.aquila.core.data.*;
import ru.prolib.aquila.utils.experimental.charts.formatters.InstantLabelFormatter;
import ru.prolib.aquila.utils.experimental.charts.indicators.IndicatorChartLayer;
import ru.prolib.aquila.utils.experimental.charts.indicators.IndicatorSettings;
import ru.prolib.aquila.utils.experimental.charts.indicators.calculator.Calculator;
import ru.prolib.aquila.utils.experimental.charts.indicators.forms.IndicatorParams;
import ru.prolib.aquila.utils.experimental.charts.indicators.forms.QEMAIndicatorParams;
import ru.prolib.aquila.utils.experimental.charts.layers.CandleChartLayer;
import ru.prolib.aquila.utils.experimental.charts.layers.ChartLayer;
import ru.prolib.aquila.utils.experimental.charts.layers.VolumeChartLayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentListener;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.Timer;

/**
 * Created by TiM on 25.12.2016.
 */
public class TestPanel extends JPanel {

    private JScrollBar scrollBar;
    private final ChartPanel<Instant> panel;
    private AdjustmentListener scrollBarListener;
    private SeriesImpl<Candle> candleData;
    private Series<Long> volumeData;
    private Series<Instant> categoriesData;
    private CandleChartLayer candles;
//    private TradeChartObject trades;
    private VolumeChartLayer volumes;
    private boolean replayStarted = false;
    private Timer replayTimer = new Timer("REPLAY_TIMER", true);
    private TimerTask replayTask;
    private int countReplay = 0;

    private final int TICK_DELAY = 200;
    private final int COUNT_TICK = 20;

    private List<IndicatorChartLayer> indicators = new ArrayList<>();
    private JMenu indicatorsMenu;

    private class AddIndicatorListener implements ActionListener {

        private final IndicatorParams form;

        public AddIndicatorListener(IndicatorParams form) {
            this.form = form;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            IndicatorSettings settings = form.showDialog();
            Calculator calculator = settings.getCalculator();
            if(settings!=null && findIndicatorById(calculator.getId())==null){
                indicators.add(new IndicatorChartLayer(settings));
                refreshIndicators();
                indicatorsMenu.insert(createIndicatorMenuItem(calculator.getId(), calculator.getName()), indicatorsMenu.getItemCount()-2);
            }
        }
    }

    public TestPanel() {
        super();
        setLayout(new BorderLayout());
        panel = createChartPanel();
        panel.setCategoriesLabelFormatter(new InstantLabelFormatter());

        ScrollBar sb = new ScrollBar();
        sb.setOrientation(Orientation.HORIZONTAL);
        sb.setPrefHeight(20);
        panel.setScrollbar(sb);

//        panel.setTimeAxisSettings(new M15TimeAxisSettings());
//        panel.getChart("VOLUMES").setTimeAxisSettings(new DefaultTimeAxisSettings());
//        panel.getChart("VOLUMES").getXAxis().setSide(Side.TOP);

        add(panel, BorderLayout.CENTER);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                generateRandomData();
                panel.setCurrentPosition(candleData.getLength());
            }
        });
    }

    private ChartPanel createChartPanel(){
        ChartPanel<Instant> panel = new ChartPanel<Instant>();
        panel.addChart("CANDLES");
        candles = new CandleChartLayer();
        panel.addChartLayer("CANDLES", candles);

//        trades = new TradeChartObject();
//        panel.addChartLayer("CANDLES", trades);

        panel.addChart("VOLUMES");
        volumes = new VolumeChartLayer();
        panel.addChartLayer("VOLUMES", volumes);

        panel.getChart("VOLUMES").setPrefHeight(200);
//        panel.getChart("CANDLES").setCategoriesAxisVisible(false);
//        panel.getChart("VOLUMES").setCategoriesAxisVisible(false);
        panel.getChart("CANDLES").setPrefHeight(1200);

        return panel;
    }

    private void generateRandomData(){
        candleData = getRandomData();
        volumeData = new CandleVolumeSeries(candleData);
        categoriesData = new CandleStartTimeSeries(candleData);

        candles.setData(candleData);
        volumes.setData(volumeData);
        volumes.setCategories(categoriesData);

//        trades.setXValues(candles.getCategories());
//        trades.setPeriod(candleData.get(0));
//        List<TradeInfo> tradesData = new ArrayList<>();
//        tradesData.add(new TradeInfo(candleData.get(4).getStartTime().plusSeconds(50),
//                OrderAction.SELL,
//                candleData.get(4).getHigh(),
//                500L));
//        tradesData.add(new TradeInfo(candleData.get(4).getStartTime().plus(10, ChronoUnit.MINUTES),
//                OrderAction.BUY,
//                candleData.get(4).getLow(),
//                500L));
//        tradesData.add(new TradeInfo(candleData.get(5).getStartTime().plus(10, ChronoUnit.MINUTES),
//                OrderAction.BUY,
//                candleData.get(5).getBodyMiddle(),
//                500L));
//        tradesData.add(new TradeInfo(candleData.get(5).getStartTime().plus(11, ChronoUnit.MINUTES),
//                OrderAction.BUY,
//                candleData.get(5).getBodyMiddle()+1,
//                500L));
//        tradesData.add(new TradeInfo(candleData.get(5).getStartTime().plus(12, ChronoUnit.MINUTES),
//                OrderAction.BUY,
//                candleData.get(5).getBodyMiddle(),
//                500L));
//        trades.setData(tradesData);
//
//        volumes.setData(candleData);

        refreshIndicatorsData();
        panel.refresh();
    }

    private SeriesImpl<Candle> getRandomData(){
        double previousClose = 1850;
        SeriesImpl<Candle> data = new SeriesImpl<>();
        Instant start = Instant.parse("2016-12-31T19:00:00.000Z");
        int step = 15;
        for (int i = 0; i < 10; i++) {
            Interval interval = Interval.of(start.plus(step*i, ChronoUnit.MINUTES), start.plus(step*(i+1), ChronoUnit.MINUTES));
            double open = previousClose;
            double close = getNewValue(open);
            double high = Math.max(open + getRandom(),close);
            double low = Math.min(open - getRandom(),close);
            long volume = Math.round(Math.random() * 5000);
            previousClose = close;
            try {
                data.add(new Candle(interval, open, high, low, close, volume));
            } catch (ValueException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    protected double getNewValue(double previousValue) {
        int sign;
        if( Math.random() < 0.5 ) {
            sign = -1;
        } else {
            sign = 1;
        }
        return getRandom() * sign + previousValue;
    }


    protected double getRandom() {
        double newValue = 0;
        newValue = Math.random() * 10;
        return newValue;
    }

    public JMenuBar createMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        JMenu main = new JMenu("Main");
        JMenuItem random = new JMenuItem("Random data");
        random.addActionListener(e -> generateRandomData());
        main.add(random);
        final JMenuItem miReplay = new JMenuItem("Start replay");
        miReplay.addActionListener(event->{
            replayStarted = !replayStarted;
            miReplay.setText(replayStarted?"Stop replay":"Start replay");
            if(replayStarted){
                replayTask = new TimerTask() {
                    @Override
                    public void run() {
                        countReplay++;
                        if(countReplay%COUNT_TICK == 0){
                            addZeroCandle();
                        } else {
                            changeCandle();
                        }
                    }
                };
                replayTimer.schedule(replayTask, new Date(), TICK_DELAY);
            } else {
                if(replayTask!=null){
                    countReplay = 0;
                    replayTask.cancel();
                }
            }
        });
        main.add(miReplay);

        main.add(new JPopupMenu.Separator());

        final JMenuItem miExit = new JMenuItem("Exit");
        miExit.addActionListener(event-> System.exit(0));
        main.add(miExit);

        menuBar.add(main);
        indicatorsMenu = createIndicatorMenu();
        menuBar.add(indicatorsMenu);

        JMenu view = new JMenu("View");
        JMenuItem zoomIn = new JMenuItem("Zoom In");
        zoomIn.addActionListener(e-> panel.setNumberOfPoints(panel.getNumberOfPoints()-1));
        JMenuItem zoomOut = new JMenuItem("Zoom Out");
        zoomOut.addActionListener(e-> panel.setNumberOfPoints(panel.getNumberOfPoints()+1));
        view.add(zoomIn);
        view.add(zoomOut);
        menuBar.add(view);

        return menuBar;
    }

    private JMenu createIndicatorMenu() {
        JMenu menu = new JMenu("Indicators");
        JMenu miAdd = new JMenu("Add indicator");
        JMenuItem miAddMA = new JMenuItem("QEMA");
        miAddMA.addActionListener(new AddIndicatorListener(new QEMAIndicatorParams()));
        miAdd.add(miAddMA);
        menu.add(new JPopupMenu.Separator());
        menu.add(miAdd);
        return menu;
    }

    private IndicatorChartLayer findIndicatorById(String id){
        for(IndicatorChartLayer obj: indicators){
            if(id.equals(obj.getId())){
                return obj;
            }
        }
        return null;
    }

    private JMenu createIndicatorMenuItem(final String id, String name) {
        final JMenu menu = new JMenu(name);
        JMenuItem miDelete = new JMenuItem("Remove");
        miDelete.addActionListener(e->{
            IndicatorChartLayer obj = findIndicatorById(id);
            if(obj!=null){
                indicators.remove(obj);
                indicatorsMenu.remove(menu);
                refreshIndicators();
            }
        });
        menu.add(miDelete);
        return menu;
    }

    private void refreshIndicators(){
        for(int i = panel.getChartLayers("CANDLES").size()-1; i>=0; i--){
            ChartLayer obj = panel.getChartLayers("CANDLES").get(i);
            if(obj instanceof IndicatorChartLayer){
                if(!indicators.contains(obj)){
                    panel.getChartLayers("CANDLES").remove(obj);
                    Node node = panel.getChart("CANDLES").getNodeById(((IndicatorChartLayer) obj).getId());
                    if(node!=null){
                        node.setId(null);
                    }
                }
            }
        }
        for(IndicatorChartLayer obj: indicators){
            panel.addChartLayer("CANDLES", obj);
        }
        refreshIndicatorsData();
        panel.refresh();
    }

    private void refreshIndicatorsData(){
        for(IndicatorChartLayer obj: indicators){
            obj.setData(new CandleCloseSeries(candles.getData()));
            obj.setCategories(new CandleStartTimeSeries(candles.getData()));
        }
    }

    private void changeCandle(){
        Platform.runLater(()->{
            Candle candle = null;
            try {
                candle = candleData.get();
            } catch (ValueException e) {
                e.printStackTrace();
            }
            if(candle!=null){
                long vol = candle.getVolume() + Math.round(Math.random() * 500);
                double close = candle.getClose() + (Math.random()-0.5)*2;
                double high = close>candle.getHigh()?close:candle.getHigh();
                double low = close<candle.getLow()?close:candle.getLow();
                Candle newCandle = new Candle(candle.getInterval(), candle.getOpen(), high, low, close, vol);
                try {
                    candleData.set(newCandle);
                } catch (ValueException e) {
                    e.printStackTrace();
                }
            }
            if(panel.isCategoryDisplayed(candle.getStartTime())){
                panel.refresh();
            }
        });
    }

    private void addZeroCandle(){
        Platform.runLater(()->{
            Candle candle = null;
            try {
                candle = candleData.get();
            } catch (ValueException e) {
                e.printStackTrace();
            }
            if(candle!=null){
                Interval interval = Interval.of(candle.getEndTime(), candle.getEndTime().plus(candle.getInterval().toDuration().getSeconds(), ChronoUnit.SECONDS));
                double open = candle.getClose();
                double close = open;
                double high = open;
                double low = open;
                Candle newCandle = new Candle(interval, open, high, low, close, 0L);
                try {
                    candleData.add(newCandle);
                } catch (ValueException e) {
                    e.printStackTrace();
                }
                if(panel.isCategoryDisplayed(candle.getStartTime())){
                    panel.setCurrentPosition(panel.getCurrentPosition()+1);
                }
            }
        });
    }
}
