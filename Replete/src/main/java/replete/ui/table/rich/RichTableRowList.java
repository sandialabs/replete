package replete.ui.table.rich;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;

import replete.text.StringUtil;
import replete.ui.lay.Lay;
import replete.ui.table.JButtonTableCellEditor;
import replete.ui.table.JButtonTableCellEditorActionListener;
import replete.ui.table.JButtonTableCellRenderer;

public class RichTableRowList extends ArrayList<RichTableRow> {
    public RichTableRow add(String prop) {
        return add(prop, null, null, false, null);
    }
    public RichTableRow add(String prop, Object value) {
        return add(prop, value, null, false, null);
    }
    public RichTableRow add(String prop, Object value, boolean imp) {
        return add(prop, value, null, imp, null);
    }
    public RichTableRow add(String prop, Object value, Color background) {
        return add(prop, value, null, false, background);
    }
    public RichTableRow add(String prop, Object value, String desc) {
        return add(prop, value, desc, false, null);
    }
    public RichTableRow add(String prop, Object value, String desc, boolean imp) {
        return add(prop, value, desc, imp, null);
    }
    public RichTableRow add(String prop, Object value, String desc, Color background) {
        return add(prop, value, desc, false, background);
    }
    public RichTableRow add(String prop, Object value, String desc, boolean imp, Color background) {
        RichTableRow row = new RichTableRow()
            .setProperty(prop)
            .setValue(value)
            .setDescription(desc)
            .setImportant(imp)
            .setBackground(background)
        ;
        if(value instanceof ValueGenerator) {
            row.setLiveRefresh(true);
        }
        add(row);
        return row;
    }

    // These aggregate row addition methods could be
    // designed better - a little clunky right now.
    public void addNamedSection(String sectionLabel, Map<?, ?> childMap) {
        addNamedSection(sectionLabel, childMap, 0, null, null, false);
    }
    public void addNamedSection(String sectionLabel, Map<?, ?> childMap, int level) {
        addNamedSection(sectionLabel, childMap, level, null, null, false);
    }
    public void addNamedSection(String sectionLabel, Map<?, ?> childMap, int level,
                                RichTableRowObjectTranslator keyTranslator,
                                RichTableRowObjectTranslator valueTranslator) {
        addNamedSection(sectionLabel, childMap, level, keyTranslator, valueTranslator, false);
    }
    public void addNamedSection(String sectionLabel, Map<?, ?> childMap, int level,
                                RichTableRowObjectTranslator keyTranslator,
                                RichTableRowObjectTranslator valueTranslator, boolean useHtml) {
        String sp = useHtml? StringUtil.spacesHtml(level * 4) : StringUtil.spaces(level * 4);
        String sp2 = useHtml ? StringUtil.spacesHtml((level + 1) * 4) : StringUtil.spaces((level + 1) * 4);
        add(sp + sectionLabel + ":").setUseHtml(useHtml);
        for(Object key : childMap.keySet()) {
            Object value = childMap.get(key);
            String desc;
            if(value instanceof ValueDescPair) {    // Need an explicit separate object to be unambiguous
                desc = ((ValueDescPair) value).getDescription();
                value = ((ValueDescPair) value).getValue();
            } else {
                desc = null;
            }
            if(keyTranslator != null) {
                key = keyTranslator.translate(key);
            }
            if(valueTranslator != null) {
                value = valueTranslator.translate(value);
            }
            add(sp2 + key, value, desc).setUseHtml(useHtml);
        }
    }
    public void addNamedSection(String sectionLabel, List<?> childList, int level,
                                RichTableRowObjectTranslator keyTranslator,
                                RichTableRowObjectTranslator valueTranslator) {
        String sp = StringUtil.spaces(level * 4);
        String sp2 = StringUtil.spaces((level + 1) * 4);
        add(sp + sectionLabel + ":");
        for(Object value : childList) {
            String valueStr = null;
            if(value != null) {
                if(value instanceof Number) {
                    valueStr = StringUtil.commas(value.toString());
                } else {
                    valueStr = value.toString();
                }
            }
            add(sp2 + valueStr, "");
        }
    }

    // TODO: Consider merging this with existing DefaultUiHintedTableModel.getButton/getRenderer/getEditor
    // Seems to be some overlap.
    public void addButton(String property, Icon icon, String text,
                          final ActionListener actionListener) {
        addButton(property, icon, text, actionListener, null);
    }
    public void addButton(String property, Icon icon, String text,
                          final ActionListener actionListener, Color fg) {
          JButton btn = Lay.btn(text, icon);
          JButtonTableCellRenderer renderer = new JButtonTableCellRenderer(btn);
          JButtonTableCellEditorActionListener editorActionListener =
                                      new JButtonTableCellEditorActionListener() {
              @Override
              public void actionPerformed(Object value, int row, int col) {
                  actionListener.actionPerformed(null);
              }
          };
          JButtonTableCellEditor editor = new JButtonTableCellEditor(btn, editorActionListener);
          Lay.hn(editor.getButton(), "cursor=hand");
          add(
              new RichTableRow()
                  .setProperty(property)
                  .setValueRenderer(renderer)
                  .setValueEditor(editor)
                  .setForeground(fg)
          );
      }
}
