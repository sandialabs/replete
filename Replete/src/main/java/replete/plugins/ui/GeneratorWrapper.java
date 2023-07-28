package replete.plugins.ui;

import javax.swing.Icon;

import replete.plugins.UiGenerator;
import replete.text.StringLib;
import replete.ui.Iconable;

public class GeneratorWrapper<G extends UiGenerator> implements Iconable {    // Useful in combo boxes


    ////////////
    // FIELDS //
    ////////////

    private G generator;
    private String noneLabel;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public GeneratorWrapper(G generator) {
        this(generator, null);
    }
    public GeneratorWrapper(G generator, String noneLabel) {
        this.generator = generator;
        this.noneLabel = noneLabel;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public G getGenerator() {
        return generator;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Icon getIcon() {
        return generator.getIcon();
    }
    @Override
    public String toString() {
        return
            generator == null ?
                StringLib.NONE + (noneLabel != null ? " - " + noneLabel : "") :
                generator.getName();
    }
}

