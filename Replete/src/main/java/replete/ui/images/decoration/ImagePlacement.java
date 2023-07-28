package replete.ui.images.decoration;

import java.awt.Image;

import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;

public class ImagePlacement {


    ////////////
    // FIELDS //
    ////////////

    private Image image;
    private Integer xOffset = 0;
    private Integer yOffset = 0;
    private Anchor anchor = Anchor.UPPER_LEFT;
    private Double scale = 1.0;
    private double alpha = 1.0;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ImagePlacement() {}
    public ImagePlacement(ImageModelConcept concept) {
        this(ImageLib.getImg(concept), 0, 0, Anchor.UPPER_LEFT, null, 1.0);
    }
    public ImagePlacement(ImageModelConcept concept, Anchor anchor) {
        this(ImageLib.getImg(concept), 0, 0, anchor, null, 1.0);
    }
    public ImagePlacement(Image image, Anchor anchor) {
        this(image, 0, 0, anchor, null, 1.0);
    }
    public ImagePlacement(Image image) {
        this(image, 0, 0, Anchor.UPPER_LEFT, null, 1.0);
    }
    public ImagePlacement(ImageModelConcept concept, Integer xOffset, Integer yOffset, Anchor anchor, Double scale, double alpha) {
        this(ImageLib.getImg(concept), xOffset, yOffset, anchor, scale, alpha);
    }
    public ImagePlacement(Image image, Integer xOffset, Integer yOffset, Anchor anchor, Double scale, double alpha) {
        this.image = image;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.anchor = anchor;
        this.scale = scale;
        this.alpha = alpha;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Image getImage() {
        return image;
    }
    public Integer getXOffset() {
        return xOffset;
    }
    public Integer getYOffset() {
        return yOffset;
    }
    public double getAlpha() {
        return alpha;
    }
    public Anchor getAnchor() {
        return anchor;
    }
    public Double getScale() {
        return scale;
    }

    // Mutators

    public ImagePlacement setImage(Image image) {
        this.image = image;
        return this;
    }
    public ImagePlacement setImage(ImageModelConcept concept) {
        image = ImageLib.getImg(concept);
        return this;
    }
    public ImagePlacement setXOffset(Integer xOffset) {
        this.xOffset = xOffset;
        return this;
    }
    public ImagePlacement setYOffset(Integer yOffset) {
        this.yOffset = yOffset;
        return this;
    }
    public ImagePlacement setAlpha(double alpha) {
        this.alpha = alpha;
        return this;
    }
    public ImagePlacement setAnchor(Anchor anchor) {
        this.anchor = anchor;
        return this;
    }
    public ImagePlacement setScale(Double scale) {
        this.scale = scale;
        return this;
    }
}
