package replete.ui.label;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;

import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;

public class DatumDescriptor {


    ////////////
    // FIELDS //
    ////////////

    private String text;
    private String hoverText;
    private Integer labelWidth;
    private ImageIcon icon;
    private Font font;
    private Color foregroundColor;
    private Color backgroundColor;
    private Color borderColor;
    private DatumClickListener listener;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getText() {
        return text;
    }
    public String getHoverText() {
        return hoverText;
    }
    public Integer getLabelWidth() {
        return labelWidth;
    }
    public ImageIcon getIcon() {
        return icon;
    }
    public Font getFont() {
        return font;
    }
    public Color getForegroundColor() {
        return foregroundColor;
    }
    public Color getBackgroundColor() {
        return backgroundColor;
    }
    public Color getBorderColor() {
        return borderColor;
    }
    public DatumClickListener getListener() {
        return listener;
    }

    // Mutators

    public DatumDescriptor setText(String text) {
        this.text = text;
        return this;
    }
    public DatumDescriptor setHoverText(String hoverText) {
        this.hoverText = hoverText;
        return this;
    }
    public DatumDescriptor setLabelWidth(Integer width) {
        labelWidth = width;
        return this;
    }
    public DatumDescriptor setIcon(ImageIcon icon) {
        this.icon = icon;
        return this;
    }
    public DatumDescriptor setFont(Font font) {
        this.font = font;
        return this;
    }
    public DatumDescriptor setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
        return this;
    }
    public DatumDescriptor setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }
    public DatumDescriptor setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        return this;
    }
    public DatumDescriptor setListener(DatumClickListener listener) {
        this.listener = listener;
        return this;
    }

    // Misc

    public DatumDescriptor setIcon(ImageModelConcept concept) {
        return setIcon(ImageLib.get(concept));
    }
}
