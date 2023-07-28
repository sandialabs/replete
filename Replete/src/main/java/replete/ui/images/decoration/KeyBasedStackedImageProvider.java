package replete.ui.images.decoration;

import java.util.function.BiConsumer;

// K should be a simple information holder class
// with good hashCode & equals methods.

public class KeyBasedStackedImageProvider<K> extends StackedImageProvider<K> {


    ////////////
    // FIELDS //
    ////////////

    private BiConsumer<K, StackedImageBuilder> buildAction;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public KeyBasedStackedImageProvider(Integer baseWidth, Integer baseHeight,
                                        BiConsumer<K, StackedImageBuilder> buildAction) {
        super(baseWidth, baseHeight);
        this.buildAction = buildAction;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected void populateBuilder(K key, StackedImageBuilder builder) {
        buildAction.accept(key, builder);
    }
}
