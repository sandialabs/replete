package replete.ui.images.concepts;

public class ImageModelConcept {
    private ImageProducer producer;
    public ImageModelConcept(ImageProducer producer) {
        this.producer = producer;
    }
    public ImageProducer getProducer() {
        return producer;
    }
}

