package replete.ui.button;

import java.awt.Color;

import javax.swing.Icon;

import replete.ui.images.concepts.ImageModelConcept;

public class ConfirmButtonUiConfig {


    ////////////
    // FIELDS //
    ////////////

    private String text;
    private Icon icon;
    private ImageModelConcept concept;
    private Color fg;
    private Color bg;
    private String hints;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ConfirmButtonUiConfig() {

    }
    public ConfirmButtonUiConfig(String text, Icon icon,
                                 ImageModelConcept concept,
                                 Color fg, Color bg, String hints) {
        this.text = text;
        this.icon = icon;
        this.concept = concept;
        this.fg = fg;
        this.bg = bg;
        this.hints = hints;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getText() {
        return text;
    }
    public Icon getIcon() {
        return icon;
    }
    public ImageModelConcept getConcept() {
        return concept;
    }
    public Color getFg() {
        return fg;
    }
    public Color getBg() {
        return bg;
    }
    public String getHints() {
        return hints;
    }

    // Mutators

    public ConfirmButtonUiConfig setText(String text) {
        this.text = text;
        return this;
    }
    public ConfirmButtonUiConfig setIcon(Icon icon) {
        this.icon = icon;
        return this;
    }
    public ConfirmButtonUiConfig setConcept(ImageModelConcept concept) {
        this.concept = concept;
        return this;
    }
    public ConfirmButtonUiConfig setFg(Color fg) {
        this.fg = fg;
        return this;
    }
    public ConfirmButtonUiConfig setBg(Color bg) {
        this.bg = bg;
        return this;
    }
    public ConfirmButtonUiConfig setHints(String hints) {
        this.hints = hints;
        return this;
    }
}
