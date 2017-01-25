package ru.prolib.aquila.utils.experimental.charts;

import org.threeten.extra.Interval;
import ru.prolib.aquila.core.data.Candle;
import ru.prolib.aquila.utils.experimental.charts.formatters.M15TimeAxisSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

/**
 * Created by TiM on 25.12.2016.
 */
public class TestPanel extends JPanel implements ActionListener {

    private JScrollBar scrollBar;
    private final CandleChartPanel panel;
    private AdjustmentListener scrollBarListener;
    private JLabel numberOfPointsLabel;
    private List<Candle> candleData = new ArrayList<>();

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
            panel.setCandleData(candleData);
            scrollBar.setVisible(true);
            updateScrollBar();
            topRight.setVisible(true);
            changeCandle.setVisible(true);
            addCandle.setVisible(true);
        });

        changeCandle.addActionListener(e->{

            Candle candle = candleData.get(candleData.size()-1);
            panel.setLastClose(candle.getClose()+(Math.random()-0.5)*2);
        });

        addCandle.addActionListener(e->{
            Candle candle = candleData.get(candleData.size()-1);
            Interval interval = Interval.of(candle.getEndTime(), candle.getEndTime().plus(candle.getInterval().toDuration().getSeconds(), ChronoUnit.SECONDS));
            double open = candle.getClose();
            double close = getNewValue(open);
            double high = Math.max(open + getRandom(),close);
            double low = Math.min(open - getRandom(),close);
            Candle newCandle = new Candle(interval, open, high, low, close, 1L);
            candleData.add(newCandle);
            panel.setCandleData(newCandle);
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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()){
            case CandleChart.CURRENT_POSITION_CHANGE:
                updateScrollBar();
                scrollBar.removeAdjustmentListener(scrollBarListener);
                scrollBar.setValue(panel.getCurrentPosition());
                scrollBar.addAdjustmentListener(scrollBarListener);
                break;
            case CandleChart.NUMBER_OF_POINTS_CHANGE:
                numberOfPointsLabel.setText(panel.getNumberOfPoints().toString());
                updateScrollBar();
                break;
        }
    }

    private void updateScrollBar(){
        scrollBar.setMinimum(0);
        scrollBar.setMaximum(panel.getCandleDataCount());
        scrollBar.setVisibleAmount(panel.getNumberOfPoints());
    }

    private CandleChartPanel createChartPanel(){
        CandleChartPanel panel = new CandleChartPanel();
        return panel;
    }

    private List<Candle> getRandomData(){
        double previousClose = 1850;
        List<Candle> data = new ArrayList<>();
        Instant start = Instant.parse("2016-12-31T19:15:00.000Z");
        int step = 15;
        for (int i = 0; i < 100; i++) {
            Interval interval = Interval.of(start.plus(step*i, ChronoUnit.MINUTES), start.plus(step*(i+1), ChronoUnit.MINUTES));
            double open = previousClose;
            double close = getNewValue(open);
            double high = Math.max(open + getRandom(),close);
            double low = Math.min(open - getRandom(),close);
            previousClose = close;
            data.add(new Candle(interval, open, high, low, close, 1L));
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
}
