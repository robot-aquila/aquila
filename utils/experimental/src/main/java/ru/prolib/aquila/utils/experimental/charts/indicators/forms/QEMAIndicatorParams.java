package ru.prolib.aquila.utils.experimental.charts.indicators.forms;

import ru.prolib.aquila.utils.experimental.charts.indicators.IndicatorSettings;
import ru.prolib.aquila.utils.experimental.charts.indicators.calculator.Calculator;
import ru.prolib.aquila.utils.experimental.charts.indicators.calculator.QEMACalculator;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

/**
 * Created by TiM on 12.02.2017.
 */
public class QEMAIndicatorParams extends IndicatorParams {

    private JFormattedTextField periodField;
    JComboBox<String> stylesField;
    private int period = 0;

    @Override
    protected IndicatorSettings createSettings() {
        if(period>0){
            Calculator calculator = new QEMACalculator(period);
            String styleClass = stylesField.getSelectedItem().toString();
            return new IndicatorSettings(calculator, styleClass);
        }
        return null;
    }

    @Override
    protected JPanel createContentPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new Label("Period"));
        NumberFormat nf = NumberFormat.getInstance();
        nf.setParseIntegerOnly(true);
        periodField = new JFormattedTextField(nf);
        periodField.setValue(7L);
        panel.add(periodField);

        panel.add(new Label("Style"));
        stylesField = new JComboBox<>();
        for(String style: lineStyles){
            stylesField.addItem(style);
        }
        panel.add(stylesField);
        return panel;
    }

    @Override
    protected String checkParams() {
        try {
            period = ((Long)periodField.getValue()).intValue();
        } catch (Exception e){
            return e.getMessage();
        }
        if(period <=0){
            return "Period must be positive integer";
        }
        return "";
    }

    @Override
    protected String getCaption() {
        return "Add QEMA";
    }
}
