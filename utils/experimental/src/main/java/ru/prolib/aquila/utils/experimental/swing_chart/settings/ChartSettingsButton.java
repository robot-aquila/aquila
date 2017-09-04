package ru.prolib.aquila.utils.experimental.swing_chart.settings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static ru.prolib.aquila.utils.experimental.swing_chart.ChartConstants.CHART_CAPTION_SIZE;

/**
 * Created by TiM on 31.08.2017.
 */
public class ChartSettingsButton {
    private static final int SIZE = 24;
    private static final String ICON = "gear.png";
    private static final String ICON_OVER = "gear_over.png";
    private final JPanel parent;
    private JPopupMenu menu = new ChartSettingsPopup();
    BufferedImage icon, activeIcon;

    private int x, y;
    private int mouseX, mouseY;

    public ChartSettingsButton(JPanel parent) {
        this.parent = parent;
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
                    menu.show(parent, x, y);
                }
            }
        });
    }

    public void paint(Graphics2D g2, double x){
        y = CHART_CAPTION_SIZE/2 - SIZE/2;
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
