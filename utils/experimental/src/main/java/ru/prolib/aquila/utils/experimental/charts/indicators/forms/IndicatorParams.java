package ru.prolib.aquila.utils.experimental.charts.indicators.forms;

import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleSheet;
import ru.prolib.aquila.utils.experimental.charts.indicators.IndicatorSettings;
import ru.prolib.aquila.utils.experimental.charts.indicators.calculator.Calculator;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TiM on 12.02.2017.
 */
public abstract class IndicatorParams extends JDialog {
    protected IndicatorSettings settings;
    protected List<String> lineStyles = new ArrayList<>();
    protected String styleClass;

    public IndicatorParams() {
        getLineStylesFromCSS();
        setResizable(false);
        setModal(true);
        setTitle(getCaption());
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(createContentPanel(), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.SOUTH);
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> {this.setVisible(false);});
        JButton btnOK = new JButton("OK");
        btnOK.addActionListener(e->{
            String msg = checkParams();
            if("".equals(msg)){
                settings = createSettings();
                setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, msg);
            }
        });
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel.add(btnOK);
        panel.add(btnCancel);


        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        getRootPane().setDefaultButton(btnOK);
        pack();
    }

    private void getLineStylesFromCSS() {
        lineStyles.clear();
        InputSource source = new InputSource(IndicatorParams.class.getResource("/charts.css").toExternalForm());
        CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
        try {
            CSSStyleSheet sheet = parser.parseStyleSheet(source, null, null);
            for (int i=0; i<sheet.getCssRules().getLength(); i++){
                CSSRule rule = sheet.getCssRules().item(i);
                String str = rule.getCssText();
                if(str.startsWith(".line-")){
                    lineStyles.add(str.substring(1, str.indexOf("{")).trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract String checkParams();

    protected abstract IndicatorSettings createSettings();

    protected String getCaption(){
        return "Add indicator";
    }

    protected abstract JPanel createContentPanel();

    public IndicatorSettings showDialog() {
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
        setVisible(true);
        return settings;
    }

    public String getStyleClass() {
        return styleClass;
    }
}
