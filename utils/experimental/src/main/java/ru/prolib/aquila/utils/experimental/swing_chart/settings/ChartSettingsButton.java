package ru.prolib.aquila.utils.experimental.swing_chart.settings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by TiM on 31.08.2017.
 */
public class ChartSettingsButton {
    private static final int SIZE = 24;
    private final String icon = "gear.png";
    private final String iconOver = "gear_over.png";
    private final JPanel parent;
    private JPopupMenu menu = new ChartSettingsPopup();

    private int x, y;
    private int mouseX, mouseY;

    public ChartSettingsButton(JPanel parent) {
        this.parent = parent;
        parent.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        parent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(isActive()){
                    menu.show(parent, x, y);
                }
            }
        });
    }

    public void paint(Graphics2D g2, Rectangle2D chartBounds){
        updateCoords(chartBounds);
        try {
            String str = isActive()?iconOver:icon;
            BufferedImage img = ImageIO.read(getClass().getClassLoader().getResource(str));
            g2.drawImage(img, x, y, SIZE, SIZE, parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isActive(){
        return mouseX > x && mouseX < x+SIZE && mouseY > y && mouseY < y+SIZE;
    }

    private void updateCoords(Rectangle2D chartBounds){
        x = (int) Math.round(chartBounds.getMaxX()) - SIZE;
        y = (int) Math.round(chartBounds.getMinY());
    }
}
