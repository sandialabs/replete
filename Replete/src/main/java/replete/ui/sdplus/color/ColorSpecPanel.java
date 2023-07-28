package replete.ui.sdplus.color;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import replete.event.ChangeNotifier;
import replete.ui.GuiUtil;
import replete.ui.button.IconButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;


/**
 * @author Derek Trumbo
 */

class ColorSpecPanel extends JPanel {

    protected JList lstColors;
    protected DefaultListModel model;

    protected ColorMap colorMap;

    public ColorSpecPanel(ColorMap map, boolean allowEdits) {

        colorMap = map;

        mapToListModel();

        lstColors = new JList(model);
        lstColors.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        lstColors.setCellRenderer(new ColorMappingRenderer());

        lstColors.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(lstColors.getSelectedIndex() != -1) {
                    ColorMapping mapping = (ColorMapping) lstColors.getSelectedValue();
                    selectedItemChangeNotifier.setSource(mapping);
                    selectedItemChangeNotifier.fireStateChanged();
                }
            }
        });

        if(lstColors.getModel().getSize() != 0) {
            lstColors.setSelectedIndex(0);
        }

        JScrollPane scr = new JScrollPane(lstColors);

        JPanel pnlAdd = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JButton btnAdd = new IconButton(ImageLib.get(CommonConcepts.ADD), "Add", 2);
        JButton btnRemove = new IconButton(ImageLib.get(CommonConcepts.REMOVE), "Remove", 2);

        btnAdd.addActionListener(e -> {
            String key = "" + (lstColors.getModel().getSize() + 1);
            Color value = Color.black;
            ColorMapping mapping = new ColorMapping(key, value);
            model.addElement(mapping);
            lstColors.setSelectedIndex(model.getSize() - 1);
            fixUpNumericalEntries();
            lstColors.ensureIndexIsVisible(lstColors.getSelectedIndex());
        });

        btnRemove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(lstColors.getModel().getSize() == 1) {
                    return;
                }
                int[] idx = lstColors.getSelectedIndices();
                if(idx.length != 0) {
                    int selIndex = idx[0];
                    for(int i = idx.length - 1; i >= 0; i--) {
                        model.remove(idx[i]);
                    }
                    int size = lstColors.getModel().getSize();
                    if(size > 0) {
                        if(selIndex >= size) {
                            selIndex = size - 1;
                        }
                        lstColors.setSelectedIndex(selIndex);
                        lstColors.ensureIndexIsVisible(lstColors.getSelectedIndex());
                    }
                    fixUpNumericalEntries();
                }
            }
        });

        pnlAdd.add(btnAdd);
        pnlAdd.add(new JLabel("  "));
        pnlAdd.add(btnRemove);

        setLayout(new BorderLayout());
        if(allowEdits) {
            GuiUtil.addBorderedComponent(this, pnlAdd, BorderFactory.createEmptyBorder(5, 0, 0, 0), BorderLayout.SOUTH);
        }
        add(scr, BorderLayout.CENTER);
    }

    // Ensure numerical keys are consecutive integers starting at "1".
    protected void fixUpNumericalEntries() {
        for(int x = 0; x < model.getSize(); x++) {
            ColorMapping listEntry = (ColorMapping) model.get(x);
            try {
                if(listEntry.key != null) {
                    Integer.parseInt(listEntry.key.toString());
                    listEntry.key = "" + (x+1);
                }
            } catch(Exception e) {
                // Do nothing
            }
        }
    }

    public Color getSelectedColor() {
        if(lstColors.getSelectedIndex() != -1) {
            ColorMapping map = (ColorMapping) lstColors.getSelectedValue();
            return map.value;
        }

        return null;
    }

    protected void setSelectedColor(Color c) {
        Object[] selValues = lstColors.getSelectedValues();
        for(Object selValue : selValues) {
            ColorMapping map = (ColorMapping) selValue;
            map.value = c;
        }
        lstColors.repaint();
    }

    /////////////////////////
    // LISTENER / NOTIFIER //
    /////////////////////////

    protected ChangeNotifier selectedItemChangeNotifier = new ChangeNotifier(this);
    public void addChangeColorListener(ChangeListener listener) {
        selectedItemChangeNotifier.addListener(listener);
    }
    protected ChangeListener colorChooserChangeListener = e -> {
        Color c = (Color) e.getSource();
        setSelectedColor(c);
    };

    /////////////////////////
    // TRANSLATION METHODS //
    /////////////////////////

    // A separate data structure is used because
    // the list needs to hold both key and a
    // value together in each entry list the list.

    protected void mapToListModel() {
        model = new DefaultListModel();
        for(Object key : colorMap.keySet()) {
            model.addElement(new ColorMapping(key, colorMap.get(key)));
        }
    }
    protected ColorMap listModelToMap() {
        ColorMap map = new ColorMap();
        for(int e = 0; e < model.getSize(); e++) {
            ColorMapping mapping = (ColorMapping) model.get(e);
            map.put(mapping.key, mapping.value);
        }
        return map;
    }
}
