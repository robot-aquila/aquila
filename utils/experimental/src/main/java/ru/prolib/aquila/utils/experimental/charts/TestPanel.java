package ru.prolib.aquila.utils.experimental.charts;

import org.threeten.extra.Interval;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.utils.experimental.charts.formatters.M15TimeAxisSettings;
import ru.prolib.aquila.utils.experimental.charts.objects.CandleChartObject;
import ru.prolib.aquila.utils.experimental.charts.objects.CircleChartObject;

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
    private CircleChartObject circle;
    private boolean replayStarted = false;
    private Timer replayTimer = new Timer("REPLAY_TIMER", true);
    private TimerTask replayTask;
    private int countReplay = 0;

    private final int TICK_DELAY = 200;
    private final int COUNT_TICK = 20;



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

            scrollBar.setVisible(true);
            updateScrollBar();
            topRight.setVisible(true);
            changeCandle.setVisible(true);
            addCandle.setVisible(true);
        });

        changeCandle.addActionListener(e->{
            changeCandle();
        });

        addCandle.addActionListener(e->{
            addCandle();
        });

        top.add(topLeft, BorderLayout.WEST);
        top.add(topRight, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        scrollBarListener = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                panel.setCurrentPosition(e.getValue());
            }
        };

        scrollBar = new JScrollBar(SwingConstants.HORIZONTAL);
        scrollBar.addAdjustmentListener(scrollBarListener);
        add(scrollBar, BorderLayout.SOUTH);
        scrollBar.setVisible(false);
        random.doClick();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()){
            case Chart.CURRENT_POSITION_CHANGE:
                if(scrollBar!=null){
                    scrollBar.removeAdjustmentListener(scrollBarListener);
                    updateScrollBar();
                    scrollBar.setValue(panel.getCurrentPosition());
                    scrollBar.addAdjustmentListener(scrollBarListener);
                }
                break;
            case Chart.NUMBER_OF_POINTS_CHANGE:
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
        candles = new CandleChartObject();
        panel.addChartObject(candles);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime time = LocalDateTime.parse("2016-12-31 22:00", formatter);
        panel.addChartObject(new CircleChartObject(time, 1847, 1));
        return panel;
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
            previousClose = close;
            if(interval.getStart().atOffset(ZoneOffset.UTC).toLocalDateTime().getHour()!=20){
                data.add(new Candle(interval, open, high, low, close, 1L));
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
        menuBar.add(main);
        return menuBar;
    }

    private void changeCandle(){
        Candle candle = candles.getLastCandle();
        candles.setLastClose(candle.getClose()+(Math.random()-0.5)*2);
    }

    private void addCandle(){
        Candle candle = candles.getLastCandle();
        if(candle!=null){
            Interval interval = Interval.of(candle.getEndTime(), candle.getEndTime().plus(candle.getInterval().toDuration().getSeconds(), ChronoUnit.SECONDS));
            double open = candle.getClose();
            double close = getNewValue(open);
            double high = Math.max(open + getRandom(),close);
            double low = Math.min(open - getRandom(),close);
            Candle newCandle = new Candle(interval, open, high, low, close, 1L);
            candleData.add(newCandle);
            candles.addCandle(newCandle);
        }
    }

    private void addZeroCandle(){
        Candle candle = candles.getLastCandle();
        if(candle!=null){
            Interval interval = Interval.of(candle.getEndTime(), candle.getEndTime().plus(candle.getInterval().toDuration().getSeconds(), ChronoUnit.SECONDS));
            double open = candle.getClose();
            double close = open;
            double high = open;
            double low = open;
            Candle newCandle = new Candle(interval, open, high, low, close, 1L);
            candleData.add(newCandle);
            candles.addCandle(newCandle);
        }
    }
}
