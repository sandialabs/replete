package replete.ui.images.concepts;

import javax.swing.ImageIcon;

import replete.ui.images.shared.SharedImage;

public class SharedImageProducer implements ImageProducer {
    private String fileName;

    public SharedImageProducer(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public ImageIcon produce() {
        return SharedImage.get(fileName);
    }
}
