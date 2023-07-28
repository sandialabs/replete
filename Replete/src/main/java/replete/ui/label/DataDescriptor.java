package replete.ui.label;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import replete.ui.images.concepts.ImageModelConcept;

public class DataDescriptor extends DatumDescriptor {


    ////////////
    // FIELDS //
    ////////////

    private String labelSeparator = ":";
    private boolean suppressLabel = false;
    private DatumDescriptor defaultDatumDescriptor;
    private DatumSeparatorGenerator datumSeparatorCreator;
    private List<DatumDescriptor> data = new ArrayList<>();


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getLabelSeparator() {
        return labelSeparator;
    }
    public boolean isSuppressLabel() {
        return suppressLabel;
    }
    public DatumDescriptor getDefaultDatumDescriptor() {
        return defaultDatumDescriptor;
    }
    public DatumSeparatorGenerator getDatumSeparatorCreator() {
        return datumSeparatorCreator;
    }
    public List<DatumDescriptor> getData() {
        return data;
    }

    // Mutators

    public DataDescriptor setLabelSeparator(String labelSeparator) {
        this.labelSeparator = labelSeparator;
        return this;
    }
    public DataDescriptor setSuppressLabel(boolean suppressLabel) {
        this.suppressLabel = suppressLabel;
        return this;
    }
    public DataDescriptor setDefaultDatumDescriptor(DatumDescriptor defaultDatumDescriptor) {
        this.defaultDatumDescriptor = defaultDatumDescriptor;
        return this;
    }
    public DataDescriptor setDatumSeparatorCreator(DatumSeparatorGenerator datumSeparatorCreator) {
        this.datumSeparatorCreator = datumSeparatorCreator;
        return this;
    }
    public DataDescriptor addDatum(DatumDescriptor descriptor) {
        data.add(descriptor);
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // To support call chaining

    @Override
    public DataDescriptor setText(String text) {
        return (DataDescriptor) super.setText(text);
    }
    @Override
    public DataDescriptor setLabelWidth(Integer width) {
        return (DataDescriptor) super.setLabelWidth(width);
    }
    @Override
    public DataDescriptor setHoverText(String hoverText) {
        return (DataDescriptor) super.setHoverText(hoverText);
    }
    @Override
    public DataDescriptor setIcon(ImageIcon icon) {
        return (DataDescriptor) super.setIcon(icon);
    }
    @Override
    public DataDescriptor setFont(Font font) {
        return (DataDescriptor) super.setFont(font);
    }
    @Override
    public DataDescriptor setForegroundColor(Color foregroundColor) {
        return (DataDescriptor) super.setForegroundColor(foregroundColor);
    }
    @Override
    public DataDescriptor setBackgroundColor(Color backgroundColor) {
        return (DataDescriptor) super.setBackgroundColor(backgroundColor);
    }
    @Override
    public DataDescriptor setBorderColor(Color borderColor) {
        return (DataDescriptor) super.setBorderColor(borderColor);
    }
    @Override
    public DataDescriptor setListener(DatumClickListener listener) {
        return (DataDescriptor) super.setListener(listener);
    }

    // Misc

    @Override
    public DataDescriptor setIcon(ImageModelConcept concept) {
        return (DataDescriptor) super.setIcon(concept);
    }
}
