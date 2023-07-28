package replete.ui.images.concepts;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

public class ImageLib {
    private static Map<ImageProducer, ImageIcon> cache = new HashMap<>();

    public static synchronized ImageIcon get(ImageModelConcept concept) {
        ImageProducer producer = concept.getProducer();
        ImageIcon icon = cache.get(producer);
        if(icon == null) {
            icon = producer.produce();
            cache.put(producer, icon);
        }
        return icon;
    }

    public static Image getImg(ImageModelConcept concept) {
        return get(concept).getImage();
    }
}
