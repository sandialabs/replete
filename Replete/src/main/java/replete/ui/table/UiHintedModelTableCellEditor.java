package replete.ui.table;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

public class UiHintedModelTableCellEditor extends DefaultCellEditor {


    ///////////
    // FIELD //
    ///////////

    private TableCellEditor activeEditor;
    private Component prevComponent;
    private FocusAdapter tableComponentFocusAdapter = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
            // https://tips4java.wordpress.com/2008/12/12/table-stop-editing/
            stopCellEditing();
        }
    };

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public UiHintedModelTableCellEditor() {
        super(new JTextField());
    }

    public UiHintedModelTableCellEditor(JCheckBox chk) {
        super(chk);
    }
    public UiHintedModelTableCellEditor(JComboBox cbo) {
        super(cbo);
    }
    public UiHintedModelTableCellEditor(JTextField txt) {
        super(txt);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int visualRow, int col) {
        int modelRow = table.convertRowIndexToModel(visualRow);

        Component component;
        TableModel model = table.getModel();
        if(model instanceof UiHintedTableModel) {
            UiHintedTableModel uiModel = (UiHintedTableModel) model;
            DefaultCellEditor editor = uiModel.getEditor(modelRow, col);
            if(editor != null) {
                activeEditor = editor;
                component = activeEditor.getTableCellEditorComponent(table, value, isSelected, visualRow, col);
            } else {
                activeEditor = null;
                component = super.getTableCellEditorComponent(table, value, isSelected, visualRow, col);
            }
        } else {
            activeEditor = null;
            component = super.getTableCellEditorComponent(table, value, isSelected, visualRow, col);
        }

        if (prevComponent != null) {
            prevComponent.removeFocusListener(tableComponentFocusAdapter);
        }
        prevComponent = component;

        component.addFocusListener(tableComponentFocusAdapter);
        return component;
    }
    @Override
    public Object getCellEditorValue() {
        return activeEditor != null ? activeEditor.getCellEditorValue() : super.getCellEditorValue();
    }
    @Override
    public boolean stopCellEditing() {
        return activeEditor != null ? activeEditor.stopCellEditing() : super.stopCellEditing();  // true if ok, false if invalid data and no ...
    }
    @Override
    public void cancelCellEditing() {
        if(activeEditor != null) {
            activeEditor.cancelCellEditing();
        }
        super.cancelCellEditing();
    }
    @Override
    public void addCellEditorListener(CellEditorListener listener) {
        if (activeEditor != null) {
            activeEditor.addCellEditorListener(listener);
        }
        super.addCellEditorListener(listener);
    }
    @Override
    public void removeCellEditorListener(CellEditorListener listener) {
        if (activeEditor != null) {
            activeEditor.removeCellEditorListener(listener);
        }
        super.removeCellEditorListener(listener);
    }
}
