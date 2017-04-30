package ru.prolib.aquila.utils.experimental.charts;

import javafx.application.Platform;
import javafx.scene.Node;
import org.threeten.extra.Interval;
import ru.prolib.aquila.core.BusinessEntities.OrderAction;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.utils.experimental.charts.formatters.M15TimeAxisSettings;
import ru.prolib.aquila.utils.experimental.charts.indicators.IndicatorChartObject;
import ru.prolib.aquila.utils.experimental.charts.indicators.IndicatorSettings;
import ru.prolib.aquila.utils.experimental.charts.indicators.calculator.Calculator;
import ru.prolib.aquila.utils.experimental.charts.indicators.forms.IndicatorParams;
import ru.prolib.aquila.utils.experimental.charts.indicators.forms.MovingAverageIndicatorParams;
import ru.prolib.aquila.utils.experimental.charts.objects.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.Timer;

import static ru.prolib.aquila.utils.experimental.charts.ChartPanel.CURRENT_POSITION_CHANGE;
import static ru.prolib.aquila.utils.experimental.charts.ChartPanel.NUMBER_OF_POINTS_CHANGE;

/**
 * Created by TiM on 25.12.2016.
 */
public class TestPanel extends JPanel implements ActionListener {

    private JScrollBar scrollBar;
    private final ChartPanel panel;
    private AdjustmentListener scrollBarListener;
    private JLabel numberOfPointsLabel;
    private List<Candle> candleData = new ArrayList<>();
    private CandleChartObject candles;
    private TradeChartObject trades;
    private VolumeChartObject volumes;
    private boolean replayStarted = false;
    private Timer replayTimer = new Timer("REPLAY_TIMER", true);
    private TimerTask replayTask;
    private int countReplay = 0;

    private final int TICK_DELAY = 200;
    private final int COUNT_TICK = 20;

    private List<IndicatorChartObject> indicators = new ArrayList<>();
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
                indicators.add(new IndicatorChartObject(settings));
                refreshIndicators();
                indicatorsMenu.insert(createIndicatorMenuItem(calculator.getId(), calculator.getName()), indicatorsMenu.getItemCount()-2);
            }
        }
    }

    public TestPanel() {
        super();
        setLayout(new BorderLayout());
        panel = createChartPanel();
        panel.setActionListener(this);
        panel.setTimeAxisSettings(new M15TimeAxisSettings());
        add(panel, BorderLayout.CENTER);

        JButton random = new JButton("Random Data");
        final JButton changeCandle = new JButton("Change last candle");
        changeCandle.setVisible(false);
        final JButton addCandle = new JButton("Add candle");
        addCandle.setVisible(false);

        final JPanel topLeft = new JPanel();
        topLeft.setLayout(new BoxLayout(topLeft, BoxLayout.X_AXIS));
        topLeft.add(random);
        topLeft.add(changeCandle);
        topLeft.add(addCandle);


        numberOfPointsLabel = new JLabel(panel.getNumberOfPoints().toString());
        JButton minus = new JButton("minus");
        minus.addActionListener(e -> {
            panel.setNumberOfPoints(panel.getNumberOfPoints()-1);
            numberOfPointsLabel.setText(panel.getNumberOfPoints().toString());
        });
        JButton plus = new JButton("plus");
        plus.addActionListener(e -> {
            panel.setNumberOfPoints(panel.getNumberOfPoints()+1);
            numberOfPointsLabel.setText(panel.getNumberOfPoints().toString());
        });

        JPanel top = new JPanel(new BorderLayout());

        final JPanel topRight = new JPanel();
        topRight.setLayout(new BoxLayout(topRight, BoxLayout.X_AXIS));
        topRight.add(new JLabel("Number of points: "));
        topRight.add(minus);
        topRight.add(numberOfPointsLabel);
        topRight.add(plus);
        topRight.setVisible(false);


        random.addActionListener(e->{
            candleData = getRandomData();

            candles.setData(candleData);
            refreshIndicatorsData();
            panel.refresh();


            scrollBar.setVisible(true);
            updateScrollBar();
            topRight.setVisible(true);
            changeCandle.setVisible(true);
            addCandle.setVisible(true);
        });

        changeCandle.addActionListener(e-> changeCandle());

        addCandle.addActionListener(e-> addCandle());

        top.add(topLeft, BorderLayout.WEST);
        top.add(topRight, BorderLayout.EAST);

//        add(top, BorderLayout.NORTH);

        scrollBarListener = e -> panel.setCurrentPosition(e.getValue());

        scrollBar = new JScrollBar(SwingConstants.HORIZONTAL);
        scrollBar.addAdjustmentListener(scrollBarListener);
        add(scrollBar, BorderLayout.SOUTH);
//        scrollBar.setVisible(false);
//        panel.setCurrentPosition(0);
//        random.doClick();
        Platform.runLater(() -> generateRandomData());

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()){
            case CURRENT_POSITION_CHANGE:
                if(scrollBar!=null){
                    scrollBar.removeAdjustmentListener(scrollBarListener);
                    updateScrollBar();
                    scrollBar.setValue(panel.getCurrentPosition());
                    scrollBar.addAdjustmentListener(scrollBarListener);
                }
                break;
            case NUMBER_OF_POINTS_CHANGE:
                if(scrollBar!=null){
                    numberOfPointsLabel.setText(panel.getNumberOfPoints().toString());
                    updateScrollBar();
                }
                break;
        }
    }

    private void updateScrollBar(){
        scrollBar.setMinimum(0);
        scrollBar.setMaximum(panel.getXValues().size());
        scrollBar.setVisibleAmount(panel.getNumberOfPoints());
    }

    private ChartPanel createChartPanel(){
        ChartPanel panel = new ChartPanel();
        panel.addChart("CANDLES");
        candles = new CandleChartObject();
        panel.addChartObject("CANDLES", candles);

        trades = new TradeChartObject();
        panel.addChartObject("CANDLES", trades);

        panel.addChart("VOLUMES");
        volumes = new VolumeChartObject();
        panel.addChartObject("VOLUMES", volumes);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime time = LocalDateTime.parse("2016-12-31 22:00", formatter);

        panel.addChartObject("CANDLES", new CircleChartObject(time, 1847, 1));

        panel.getChart("VOLUMES").setPrefHeight(200);
        panel.getChart("CANDLES").setXAxisVisible(false);
        panel.getChart("CANDLES").setPrefHeight(1200);

        return panel;
    }

    private void generateRandomData(){
        candleData = getRandomData();
        candles.setData(candleData);

        trades.setXValues(candles.getXValues());
        trades.setPeriod(candleData.get(0));
        List<TradeInfo> tradesData = new ArrayList<>();
        tradesData.add(new TradeInfo(candleData.get(4).getStartTime().plusSeconds(50),
                OrderAction.SELL,
                candleData.get(4).getHigh(),
                500L));
        tradesData.add(new TradeInfo(candleData.get(4).getStartTime().plus(10, ChronoUnit.MINUTES),
                OrderAction.BUY,
                candleData.get(4).getLow(),
                500L));
        tradesData.add(new TradeInfo(candleData.get(5).getStartTime().plus(10, ChronoUnit.MINUTES),
                OrderAction.BUY,
                candleData.get(5).getBodyMiddle(),
                500L));
        tradesData.add(new TradeInfo(candleData.get(5).getStartTime().plus(11, ChronoUnit.MINUTES),
                OrderAction.BUY,
                candleData.get(5).getBodyMiddle()+1,
                500L));
        tradesData.add(new TradeInfo(candleData.get(5).getStartTime().plus(12, ChronoUnit.MINUTES),
                OrderAction.BUY,
                candleData.get(5).getBodyMiddle(),
                500L));
        trades.setData(tradesData);

        volumes.setData(candleData);
        refreshIndicatorsData();
        panel.refresh();
        updateScrollBar();
    }

    private List<Candle> getRandomData(){
        double previousClose = 1850;
        List<Candle> data = new ArrayList<>();
        Instant start = Instant.parse("2016-12-31T19:00:00.000Z");
        int step = 15;
        for (int i = 0; i < 100; i++) {
            Interval interval = Interval.of(start.plus(step*i, ChronoUnit.MINUTES), start.plus(step*(i+1), ChronoUnit.MINUTES));
            double open = previousClose;
            double close = getNewValue(open);
            double high = Math.max(open + getRandom(),close);
            double low = Math.min(open - getRandom(),close);
            long volume = Math.round(Math.random() * 5000);
            previousClose = close;
            if(interval.getStart().atOffset(ZoneOffset.UTC).toLocalDateTime().getHour()!=20){
                data.add(new Candle(interval, open, high, low, close, volume));
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
        JMenuItem miAddMA = new JMenuItem("Moving Average");
        miAddMA.addActionListener(new AddIndicatorListener(new MovingAverageIndicatorParams()));
        miAdd.add(miAddMA);
        menu.add(new JPopupMenu.Separator());
        menu.add(miAdd);
        return menu;
    }

    private IndicatorChartObject findIndicatorById(String id){
        for(IndicatorChartObject obj: indicators){
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
            IndicatorChartObject obj = findIndicatorById(id);
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
        for(int i=panel.getChartObjects("CANDLES").size()-1; i>=0; i--){
            ChartObject obj = panel.getChartObjects("CANDLES").get(i);
            if(obj instanceof IndicatorChartObject){
                if(!indicators.contains(obj)){
                    panel.getChartObjects("CANDLES").remove(obj);
                    Node node = panel.getChart("CANDLES").getNodeById(((IndicatorChartObject) obj).getId());
                    if(node!=null){
                        node.setId(null);
                    }
                }
            }
        }
        for(IndicatorChartObject obj: indicators){
            panel.addChartObject("CANDLES", obj);
        }
        refreshIndicatorsData();
        panel.refresh();
    }

    private void refreshIndicatorsData(){
        for(IndicatorChartObject obj: indicators){
            obj.setData(candles.getData());
        }
//        panel.refresh();
    }

    private void changeCandle(){
        Platform.runLater(()->{
            Candle candle = candles.getLastCandle();
            long dV = Math.round(Math.random() * 500);
            Candle newCandle = new Candle(candle.getInterval(), candle.getOpen(), candle.getHigh(), candle.getLow(), candle.getClose()+(Math.random()-0.5)*2, candle.getVolume()+ dV);
            candles.setLastClose(newCandle.getClose(), newCandle.getVolume());
            volumes.setLastVolume(newCandle);
            List<Candle> data = candles.getData();
            refreshIndicatorsData();
            if(panel.isTimeDisplayed(candle.getStartTime())){
                panel.refresh();
            }
        });
    }

    private void addCandle(){
        Platform.runLater(()-> {
            Candle candle = candles.getLastCandle();
            if (candle != null) {
                Interval interval = Interval.of(candle.getEndTime(), candle.getEndTime().plus(candle.getInterval().toDuration().getSeconds(), ChronoUnit.SECONDS));
                double open = candle.getClose();
                double close = getNewValue(open);
                double high = Math.max(open + getRandom(), close);
                double low = Math.min(open - getRandom(), close);
                long volume = Math.round(Math.random() * 5000);
                Candle newCandle = new Candle(interval, open, high, low, close, volume);
                candleData.add(newCandle);
                candles.addCandle(newCandle);
                List<Candle> data = candles.getData();
                refreshIndicatorsData();
                if(panel.isTimeDisplayed(candle.getStartTime())){
                    panel.setCurrentPosition(panel.getCurrentPosition()+1);
                }
            }
        });
    }

    private void addZeroCandle(){
        Platform.runLater(()->{
            Candle candle = candles.getLastCandle();
            if(candle!=null){
                Interval interval = Interval.of(candle.getEndTime(), candle.getEndTime().plus(candle.getInterval().toDuration().getSeconds(), ChronoUnit.SECONDS));
                double open = candle.getClose();
                double close = open;
                double high = open;
                double low = open;
                Candle newCandle = new Candle(interval, open, high, low, close, 0L);
                candleData.add(newCandle);
                candles.addCandle(newCandle);
                List<Candle> data = candles.getData();
                refreshIndicatorsData();
                if(panel.isTimeDisplayed(candle.getStartTime())){
                    panel.setCurrentPosition(panel.getCurrentPosition()+1);
                }
            }
        });
    }
}
