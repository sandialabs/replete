package replete.ui.sdplus.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import replete.ui.sdplus.events.ScalePanelChangedEvent;
import replete.ui.sdplus.events.ScaleSetChangedListener;
import replete.ui.sdplus.events.ValueChangedEvent;
import replete.ui.sdplus.panels.ContScalePanel;
import replete.ui.sdplus.panels.ContScalePanelModel;
import replete.ui.sdplus.panels.DateScalePanel;
import replete.ui.sdplus.panels.DateScalePanelModel;
import replete.ui.sdplus.panels.EnumScaleMultiPanel;
import replete.ui.sdplus.panels.EnumScaleMultiPanelModel;
import replete.ui.windows.escape.EscapeFrame;


/**
 * Demonstrates how scale panels can be used without a
 * scale set panel.  This demo's purpose is only to show
 * how scale panels can be laid out in a UI without a
 * scale set panel and still behave the same as if they
 * were in one.
 *
 * @author Derek Trumbo
 */

public class AutonomousFrame extends EscapeFrame {

    public static void main(String[] args) {
        new AutonomousFrame().setVisible(true);
    }

    // UI
    protected static EnumScaleMultiPanel esp;
    protected static ContScalePanel csp;
    protected static DateScalePanel dsp;

    protected static EnumScaleMultiPanelModel espm;
    protected static ContScalePanelModel cspMdl;
    protected static DateScalePanelModel dspMdl;

    public AutonomousFrame() {
        super("Autonomous ScalePanel Demo");
        buildUI();
    }

    protected void buildUI() {

        JLabel lblInfo = new JLabel("<html>All ScalePanel's are fully autonomous - capable of existing in the UI independent of a ScaleSetPanel.</html>");

        // Option buttons panel.
        JPanel pnlOptionButtons = buildOptionButtonsPanel();
        pnlOptionButtons.setBackground(Color.darkGray);

        JPanel pnlOptions = new JPanel(new BorderLayout());
        pnlOptions.add(lblInfo, BorderLayout.NORTH);
        pnlOptions.add(pnlOptionButtons, BorderLayout.CENTER);

        // Scale panels.
        JPanel pnlScalePanels = buildScalePanels();

        // Split pane.
        JSplitPane pnlSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        pnlSplit.setLeftComponent(pnlOptions);
        pnlSplit.setRightComponent(pnlScalePanels);
        pnlSplit.setDividerLocation(400);

        add(pnlSplit, BorderLayout.CENTER);
        setSize(900, 400);
        setLocationRelativeTo(null);
    }

    protected JPanel buildScalePanels() {
        espm = new EnumScaleMultiPanelModel("ss", "Something",
            "Pounds", "Test Note...",
            Arrays.asList(new Object[] {null, "ABC", "DEF", "GHI", null}));
        esp = new EnumScaleMultiPanel(null, espm);
        esp.addScalePanelChangedListener(new ScaleSetChangedListener() {
            public void valueChanged(ValueChangedEvent e) {
                System.out.println(e);
            }
        });
        esp.setOpen(true);
        Object[] o = new Object[] {4, 5, 6, 7};
        List<Object> oo = new ArrayList<Object>();
        for(Object ooo : o) {
            oo.add(ooo);
        }
        cspMdl = new ContScalePanelModel("ss2", "Something2",
            "Pounds2", "Test Note...2", oo);
        csp = new ContScalePanel(null, cspMdl);
        csp.addScalePanelChangedListener(new ScaleSetChangedListener() {
            public void valueChanged(ValueChangedEvent e) {
                ScalePanelChangedEvent se = (ScalePanelChangedEvent) e;
                System.out.println("MODEL="+se.getScalePanelModel());
            }
        });
        csp.setOpen(true);

        oo = new ArrayList<Object>();
        o = new Object[] {getT("1974/12/13"), getT("1985/6/17"), getT("1999/1/3"), getT("2001/4/30")};
        for(Object ooo : o) {
            oo.add(ooo);
        }
        dspMdl = new DateScalePanelModel("ss3", "Something2",
            "Pounds2", "Test Note...2", oo, getT("1975/12/13"), Double.NaN, false);
        dsp = new DateScalePanel(null, dspMdl);
        dsp.addScalePanelChangedListener(new ScaleSetChangedListener() {

            public void valueChanged(ValueChangedEvent e) {
                ScalePanelChangedEvent se = (ScalePanelChangedEvent) e;
                System.out.println("MODEL="+se.getScalePanelModel());
            }
        });
        dsp.setOpen(true);

        JPanel pnlScalePanels = new JPanel();
        BoxLayout bl = new BoxLayout(pnlScalePanels, BoxLayout.Y_AXIS);
        pnlScalePanels.setLayout(bl);
        addComponentInJPanel(pnlScalePanels, esp);
        addComponentInJPanel(pnlScalePanels, csp);
        addComponentInJPanel(pnlScalePanels, dsp);
        return pnlScalePanels;
    }

    protected static SimpleDateFormat dateF = new SimpleDateFormat("yyyy/M/d");
    private long getT(String n) {
        try {
            return dateF.parse(n).getTime();
        } catch(ParseException e) {
            return -1;
        }
    }

    protected void addComponentInJPanel(JPanel p, Component c) {
        JPanel p2 = new JPanel();
        p2.add(c);
        p.add(p2);
    }

    protected JPanel buildOptionButtonsPanel() {

        JButton btnGetSelKey = buildOptionButton("Change Models", new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                // Change the filter criteria on the enumerated panel.
                espm.getSelectedValues().remove("ABC");
                esp.updateUIFromModel();

                // Change the filter criteria and remove a value from the
                // continuous panel's value list.
                cspMdl.setFilterLowerValue(5);
                if(cspMdl.getAllValues().size() != 0) {
                    cspMdl.getAllValues().set(0, -1000);
                    cspMdl.getAllValues().remove(cspMdl.getAllValues().size() - 1);
                }
                csp.updateUIFromModel();

                // Remove a value from the date panel's value list.
                if(dspMdl.getAllValues().size() != 0) {
                    dspMdl.getAllValues().remove(dspMdl.getAllValues().size() - 1);
                }
                dsp.updateUIFromModel();
            }
        });
        JButton btnAuto = buildOptionButton("Clear Enum Model", new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                // Remove all the values from the enumerated panel.
                espm.setAllValues(new ArrayList<Object>());
                esp.updateUIFromModel();
            }
        });

        int rows = 1;
        int cols = 2;

        initOptionButtons(rows, cols);
        setOptionButton(btnGetSelKey, 0, 0);
        setOptionButton(btnAuto, 0, 1);

        JPanel pnl = new JPanel(new GridLayout(rows, cols));
        for(int r = 0; r < optionButtons.length; r++) {
            for(int c = 0; c < optionButtons[0].length; c++) {
                Component cmp = optionButtons[r][c];
                if(cmp == null) {
                    JPanel pnlBlank = new JPanel();
                    pnlBlank.setOpaque(false);
                    pnl.add(pnlBlank);
                } else {
                    pnl.add(cmp);
                }
            }
        }

        return pnl;
    }

    // Option button management.
    protected Component[][] optionButtons;
    protected void initOptionButtons(int rows, int cols) {
        optionButtons = new Component[rows][cols];
    }
    protected void setOptionButton(JButton btn, int row, int col) {
        optionButtons[row][col] = btn;
    }
    protected JButton buildOptionButton(String title, ActionListener listener) {
        JButton btn = new JButton("<html><center>" + title + "</center></html>");
        btn.addActionListener(listener);
        return btn;
    }

    public Font getUserFont(Font curFont) {

      FontChooser dlg = new FontChooser(new EscapeFrame());
      SimpleAttributeSet a = new SimpleAttributeSet();
      StyleConstants.setFontFamily(a, curFont.getFamily());
      StyleConstants.setFontSize(a, curFont.getSize());
      dlg.setAttributes(a);
      dlg.setLocationRelativeTo(null);
      dlg.setVisible(true);

      if(dlg.getOption() == JOptionPane.CANCEL_OPTION) {
          return null;
      }

      SimpleAttributeSet attrs = (SimpleAttributeSet) dlg.getAttributes();

      int style = Font.PLAIN;

      if((Boolean) attrs.getAttribute(StyleConstants.Italic)) {
          style = style | Font.ITALIC;
      }
      if((Boolean) attrs.getAttribute(StyleConstants.Bold)) {
          style = style | Font.BOLD;
      }

      Font f = new Font((String) attrs.getAttribute(StyleConstants.Family), style,
          (Integer) attrs.getAttribute(StyleConstants.Size));

      return f;
    }

    protected Color getColor(Color initialColor) {
        return JColorChooser.showDialog(this, "Choose Color", initialColor);
    }
}
