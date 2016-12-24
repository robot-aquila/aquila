package ru.prolib.aquila.utils.experimental.charts;

import javafx.scene.control.*;
import org.threeten.extra.Interval;
import ru.prolib.aquila.core.data.Candle;

import javax.swing.*;
import java.awt.*;
import java.awt.Button;
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

    public TestPanel() {
        super();
        setLayout(new BorderLayout());
        panel = createChartPanel();
        panel.setActionListener(this);
        add(panel, BorderLayout.CENTER);

        JButton random = new JButton("Random Data");

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
            List<Candle> data = getRandomData();
            panel.setCandleData(data);
            scrollBar.setVisible(true);
            initScrollBar();
            topRight.setVisible(true);
        });

        top.add(random, BorderLayout.WEST);
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
                scrollBar.removeAdjustmentListener(scrollBarListener);
                scrollBar.setValue(panel.getCurrentPosition());
                scrollBar.addAdjustmentListener(scrollBarListener);
                break;
            case CandleChart.NUMBER_OF_POINTS_CHANGE:
                numberOfPointsLabel.setText(panel.getNumberOfPoints().toString());
                initScrollBar();
                break;
        }
    }

    private void initScrollBar(){
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
        Instant start = Instant.parse("2016-12-30T19:00:00.000Z");
        int step = 30;
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
