package ru.prolib.aquila.utils.experimental.swing_chart;


import javax.swing.*;

/**
 * Created by TiM on 06.06.2017.
 */
public class TooltipForm extends JWindow {

    private JLabel label;
    private JTextPane pane;

    public TooltipForm(){
        super();
        pane = new JTextPane();
        pane.setContentType("text/html");
        getContentPane().add(pane);
    }

    public void setText(String text){
        SwingUtilities.invokeLater(() -> {
            pane.setText(text);
            pack();
        });
    }

}
