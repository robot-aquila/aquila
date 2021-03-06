package ru.prolib.aquila.utils.experimental.chart.swing.settings;

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
    private static final String ICON = "gear.png";
    private static final String ICON_OVER = "gear_over.png";
    private final JPanel parent;
    private final JPopupMenu menu;
    BufferedImage icon, activeIcon;

    private int x, y;
    private int mouseX, mouseY;

    public ChartSettingsButton(JPanel parent, JPopupMenu menu) {
        this.parent = parent;
        this.menu = menu;
        try {
            icon = ImageIO.read(getClass().getClassLoader().getResource(ICON));
            activeIcon = ImageIO.read(getClass().getClassLoader().getResource(ICON_OVER));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    menu.show(parent, x, y+ SIZE);
                }
            }
        });
    }

    public void paint(Graphics2D g2, double x){
        y = 0;
        this.x = new Double(x).intValue() - SIZE;
        g2.drawImage(isActive()?activeIcon:icon, this.x, y, SIZE, SIZE, parent);
    }

    public boolean isActive(){
        return mouseX > x && mouseX < x+SIZE && mouseY > y && mouseY < y+SIZE;
    }

    private void updateCoords(Rectangle2D chartBounds){
        x = (int) Math.round(chartBounds.getMaxX()) - SIZE;
        y = (int) Math.round(chartBounds.getMinY());
    }
}
