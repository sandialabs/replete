package replete.ui.images.decoration;

import java.awt.Image;
import java.util.List;

import javax.swing.ImageIcon;

public abstract class AbstractStackedImageBuilder {


    ////////////
    // FIELDS //
    ////////////

    private double defaultScale = 1.0;
    private Integer baseWidth;
    private Integer baseHeight;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public AbstractStackedImageBuilder() {

    }
    public AbstractStackedImageBuilder(int baseWidth, int baseHeight) {
        this.baseWidth = baseWidth;
        this.baseHeight = baseHeight;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Integer getBaseWidth() {
        return baseWidth;
    }
    public Integer getBaseHeight() {
        return baseHeight;
    }
    public double getDefaultScale() {
        return defaultScale;
    }

    // Mutators

    public AbstractStackedImageBuilder setBaseWidth(Integer baseWidth) {
        this.baseWidth = baseWidth;
        return this;
    }
    public AbstractStackedImageBuilder setBaseHeight(Integer baseHeight) {
        this.baseHeight = baseHeight;
        return this;
    }
    public AbstractStackedImageBuilder setDefaultScale(double defaultScale) {
        this.defaultScale = defaultScale;
        return this;
    }


    ////////////
    // CREATE //
    ////////////

    public Image create() {
        List<ImagePlacement> tuples = getTuples();
        return ImageStacker.createStackedImage(baseWidth, baseHeight, defaultScale, tuples);
    }
    public ImageIcon createAsIcon() {
        return new ImageIcon(create());
    }


    //////////////
    // ABSTRACT //
    //////////////

    protected abstract List<ImagePlacement> getTuples();
}
