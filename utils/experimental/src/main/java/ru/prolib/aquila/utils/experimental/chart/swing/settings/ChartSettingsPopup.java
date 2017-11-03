package ru.prolib.aquila.utils.experimental.chart.swing.settings;

import ru.prolib.aquila.utils.experimental.chart.BarChartLayer;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.*;
import java.util.List;

/**
 * Created by TiM on 31.08.2017.
 */
public class ChartSettingsPopup extends JPopupMenu implements ActionListener {

    private static DecimalFormat df = new DecimalFormat("0.#############", new DecimalFormatSymbols(Locale.US));
    private JButton btOk, btCancel;
    private JTextField minVal, maxVal;
    private final ChartSettings<?> settings;
    private final Map<BarChartLayer<?>, JCheckBox> checkBoxMap = new HashMap<>();

    public ChartSettingsPopup(ChartSettings<?> settings) {
        this.settings = settings;

        final JPanel valuesIntervalForm = createValuesIntervalForm();
        final JPanel buttons = createButtons();

        addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                checkBoxMap.clear();
                ChartSettingsPopup.this.removeAll();

                minVal.setText(settings.getMinValue()==null?"":df.format(settings.getMinValue()));
                maxVal.setText(settings.getMaxValue()==null?"":df.format(settings.getMaxValue()));

                add(valuesIntervalForm);
                add(new JSeparator());
                JPanel layers = new JPanel(new BorderLayout());
                layers.add(new JLabel("Layers", SwingConstants.CENTER), BorderLayout.NORTH);

                JPanel items = new JPanel();
                items.setLayout(new BoxLayout(items, BoxLayout.Y_AXIS));
                for (BarChartLayer<?> layer : settings.getLayers()) {
                    if (!layer.getId().startsWith("___")) {
                        JCheckBox cb = new JCheckBox(layer.getId(), layer.isVisible());
                        checkBoxMap.put(layer, cb);
                        items.add(cb);
                    }
                }
                layers.add(items);
                layers.add(buttons, BorderLayout.SOUTH);
                add(layers);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(btOk)){
            save();
            setVisible(false);
        }
        if(e.getSource().equals(btCancel)){
            setVisible(false);
        }
    }

    private void save(){
        for(BarChartLayer<?> layer: checkBoxMap.keySet()){
            layer.setVisible(checkBoxMap.get(layer).isSelected());
        }
        settings.setMinValue(parseDouble(minVal.getText()));
        settings.setMaxValue(parseDouble(maxVal.getText()));
    }

    private JPanel createButtons() {
        final Dimension buttonsDimension = new Dimension(75, 16);
        final JPanel result = new JPanel(new GridLayout(1, 2));
        btOk = new JButton("OK");
        btOk.setPreferredSize(buttonsDimension);
        btOk.addActionListener(this);
        result.add(btOk);
        btCancel = new JButton("Cancel");
        btCancel.setPreferredSize(buttonsDimension);
        btCancel.addActionListener(this);
        result.add(btCancel);
        return result;
    }

    private JPanel createValuesIntervalForm(){
        JPanel result = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Values interval");
        title.setHorizontalAlignment(JLabel.CENTER);
        result.add(title, BorderLayout.NORTH);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        result.add(main, BorderLayout.CENTER);

        JPanel options = new JPanel(new GridLayout(1, 2));
        JRadioButton btAuto = new JRadioButton("Auto", settings.getMinValue()==null && settings.getMaxValue()==null);
        JRadioButton btManual = new JRadioButton("Manual", settings.getMinValue()!=null || settings.getMaxValue()!=null);
        ButtonGroup group = new ButtonGroup();
        group.add(btAuto);
        group.add(btManual);
        options.add(btAuto);
        options.add(btManual);
        main.add(options);
        JPanel values = new JPanel(new GridLayout(2, 2));
        values.add(new JLabel("Min. value"));
        minVal = new JTextField();
        values.add(minVal);
        values.add(new JLabel("Max. value"));
        maxVal = new JTextField();
        values.add(maxVal);
        main.add(values);

        ItemListener itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                minVal.setEnabled(btManual.isSelected());
                maxVal.setEnabled(btManual.isSelected());
                if(btAuto.isSelected()){
                    minVal.setText(null);
                    maxVal.setText(null);
                }
            }
        };
        btAuto.addItemListener(itemListener);
        btManual.addItemListener(itemListener);
        itemListener.itemStateChanged(null);
        return result;
    }

    private Double parseDouble(String str){
        str = str.replace(",", ".");
        try {
            return df.parse(str).doubleValue();
        } catch (ParseException e) {
            return null;
        }
    }

}
