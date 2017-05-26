package ru.prolib.aquila.ui.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by TiM on 27.05.2017.
 */
public class FDecimalTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if(value!=null){
            setText(value.toString());
            setBorder(BorderFactory.createEmptyBorder(0,0,0,5));
            setHorizontalAlignment(SwingConstants.RIGHT);
        }
        return this;
    }
}
