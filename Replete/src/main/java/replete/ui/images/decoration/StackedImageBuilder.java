package replete.ui.images.decoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StackedImageBuilder extends AbstractStackedImageBuilder {


    ////////////
    // FIELDS //
    ////////////

    private Map<Integer, List<ImagePlacement>> imageTupleLayers = new TreeMap<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public StackedImageBuilder() {
        super();
    }
    public StackedImageBuilder(int baseWidth, int baseHeight) {
        super(baseWidth, baseHeight);
    }


    //////////////
    // MUTATORS //
    //////////////

    public StackedImageBuilder addImage(ImagePlacement tuple) {
        return addImage(0, tuple);
    }
    public StackedImageBuilder addImage(int level, ImagePlacement tuple) {
        List<ImagePlacement> layer = imageTupleLayers.get(level);
        if(layer == null) {
            layer = new ArrayList<>();
            imageTupleLayers.put(level, layer);
        }
        layer.add(tuple);
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected List<ImagePlacement> getTuples() {
        List<ImagePlacement> flattened = new ArrayList<>();
        for(Integer level : imageTupleLayers.keySet()) {
            List<ImagePlacement> levelTuples = imageTupleLayers.get(level);
            flattened.addAll(levelTuples);
        }
        return flattened;
    }
}
