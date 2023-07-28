package replete.ui.images.decoration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// All elements in the List-based keys must have good
// hashCode/equals methods for the caching mechanism.

public class ListBasedManifestStackedImageProvider extends StackedImageProvider<List<Object>> {


    ////////////
    // FIELDS //
    ////////////

    private Map<Object, ImagePlacement> manifest = new HashMap<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ListBasedManifestStackedImageProvider(Integer baseWidth, Integer baseHeight) {
        super(baseWidth, baseHeight);
    }


    //////////////
    // MUTATORS //
    //////////////

    public ListBasedManifestStackedImageProvider addImage(Object key, ImagePlacement tuple) {
        manifest.put(key, tuple);
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected void populateBuilder(List<Object> keyList, StackedImageBuilder builder) {
        for(Object key : keyList) {
            ImagePlacement placement = manifest.get(key);
            builder.addImage(placement);
        }
    }
}
