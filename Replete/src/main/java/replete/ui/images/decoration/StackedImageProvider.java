package replete.ui.images.decoration;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import replete.ui.GuiUtil;

// K should have good hashCode & equals methods.

public abstract class StackedImageProvider<K> {


    ////////////
    // FIELDS //
    ////////////

    private Map<K, Image> cache = new HashMap<>();
    private Integer baseWidth;
    private Integer baseHeight;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public StackedImageProvider(Integer baseWidth, Integer baseHeight) {
        this.baseWidth = baseWidth;
        this.baseHeight = baseHeight;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    // Computed

    public Image get(K key) {
        try {
            Image image = cache.get(key);
            if(image != null) {
                return image;
            }
            StackedImageBuilder builder = new StackedImageBuilder(baseWidth, baseHeight);
            populateBuilder(key, builder);
            image = builder.create();
            cache.put(key, image);
            return image;
        } catch(Exception e) {      // Putting this in for early days of the code
            e.printStackTrace();    // to make sure no errors affect during rendering.
            return GuiUtil.createMissingImage().getImage();
        }
    }
    public ImageIcon getAsIcon(K key) {
        return new ImageIcon(get(key));
    }


    //////////////
    // ABSTRACT //
    //////////////

    protected abstract void populateBuilder(K key, StackedImageBuilder builder);
}
