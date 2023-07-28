package replete.ui.thumbs;

import java.awt.Image;

import javax.swing.Icon;

public class ThumbnailSidebarEntry {
    private String title;
    private Image image;
    private Icon icon;

    public ThumbnailSidebarEntry(String ttle, Image im) {
        title = ttle;
        image = im;
    }

    public ThumbnailSidebarEntry(String ttle, Icon ic) {
        title = ttle;
        icon = ic;
    }

    @Override
    public String toString() {
        return "Thumb: " + title + ", image=" + image + ", icon=" + icon;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public Image getImage() {
        return image;
    }
    public void setImage(Image image) {
        this.image = image;
    }
    public Icon getIcon() {
        return icon;
    }
    public void setIcon(Icon icon) {
        this.icon = icon;
    }
}