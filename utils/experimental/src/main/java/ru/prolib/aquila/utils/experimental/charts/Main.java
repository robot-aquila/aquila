package ru.prolib.aquila.utils.experimental.charts;

import javax.swing.*;

/**
 * Created by TiM on 22.12.2016.
 */
public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("TEST");
        frame.setSize(700,500);
        TestPanel panel = new TestPanel();
        frame.setJMenuBar(panel.createMenuBar());
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
