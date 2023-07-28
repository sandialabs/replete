package replete.ui.table.rich;

import java.awt.Color;
import java.awt.Font;

import javax.swing.DefaultCellEditor;
import javax.swing.table.TableCellRenderer;

public class RichTableRow {


    ////////////
    // FIELDS //
    ////////////

    private String property;
    private Object value;
    private String description;
    private boolean important;
    private Color foreground;
    private Color background;
    private Font font;
    private boolean bold;
    private boolean liveRefresh;
    private TableCellRenderer valueRenderer;
    private DefaultCellEditor valueEditor;
    private Integer alignment;
    private boolean useHtml;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getProperty() {
        return property;
    }
    public Object getValue() {
        return value;
    }
    public String getDescription() {
        return description;
    }
    public boolean isImportant() {
        return important;
    }
    public Color getForeground() {
        return foreground;
    }
    public Color getBackground() {
        return background;
    }
    public Font getFont() {
        return font;
    }
    public boolean isBold() {
        return bold;
    }
    public boolean isLiveRefresh() {
        return liveRefresh;
    }
    public TableCellRenderer getValueRenderer() {
        return valueRenderer;
    }
    public DefaultCellEditor getValueEditor() {
        return valueEditor;
    }
    public Integer getAlignment() {
        return alignment;
    }
    public boolean isUseHtml() {
        return useHtml;
    }

    // Mutators

    public RichTableRow setProperty(String property) {
        this.property = property;
        return this;
    }
    public RichTableRow setValue(Object value) {
        this.value = value;
        return this;
    }
    public RichTableRow setDescription(String description) {
        this.description = description;
        return this;
    }
    public RichTableRow setImportant(boolean important) {
        this.important = important;
        return this;
    }
    public RichTableRow setForeground(Color foreground) {
        this.foreground = foreground;
        return this;
    }
    public RichTableRow setBackground(Color background) {
        this.background = background;
        return this;
    }
    public RichTableRow setFont(Font font) {
        this.font = font;
        return this;
    }
    public RichTableRow setBold(boolean bold) {
        this.bold = bold;
        return this;
    }
    public RichTableRow setLiveRefresh(boolean liveRefresh) {
        this.liveRefresh = liveRefresh;
        return this;
    }
    public RichTableRow setValueRenderer(TableCellRenderer valueRenderer) {
        this.valueRenderer = valueRenderer;
        return this;
    }
    public RichTableRow setValueEditor(DefaultCellEditor valueEditor) {
        this.valueEditor = valueEditor;
        return this;
    }
    public RichTableRow setAlignment(Integer alignment) {
        this.alignment = alignment;
        return this;
    }
    public RichTableRow setUseHtml(boolean useHtml) {
        this.useHtml = useHtml;
        return this;
    }
}
