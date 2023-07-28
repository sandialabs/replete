package replete.ui.images.decoration;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

// This class isolates the actual stacked image creation from
// all the rest of the code surrounding it for a clear
// separation of responsibilities.

public class ImageStacker {
    public static Image createStackedImage(Integer baseWidth, Integer baseHeight, double defaultScale, List<ImagePlacement> imageTuples) {
        if((baseWidth == null || baseHeight == null) && imageTuples == null) {
            throw new IllegalStateException("MUST SPECIFY EITHER BASE WIDTH AND HEIGHT OR AT LEAST ONE IMAGE");
        }
        if(baseWidth == null) {
            baseWidth = (int) (imageTuples.get(0).getImage().getWidth(null) * imageTuples.get(0).getScale());
        }
        if(baseHeight == null) {
            baseHeight = (int) (imageTuples.get(0).getImage().getHeight(null) * imageTuples.get(0).getScale());
        }
        List<ImagePrep> preps = new ArrayList<>();
        if(imageTuples != null) {
            for(ImagePlacement imageTuple : imageTuples) {
                Double scale = imageTuple.getScale();
                if(scale == null) {
                    scale = defaultScale;
                }
                double alpha = imageTuple.getAlpha();
                Image tupleImage = imageTuple.getImage();
                int tupleImageWidth = tupleImage.getWidth(null);
                int tupleImageHeight = tupleImage.getHeight(null);
                double scaledWidth = tupleImageWidth * scale;
                double scaledHeight = tupleImageHeight * scale;

                if(alpha != 1.0) {
                    BufferedImage alpha_edImage = new BufferedImage(tupleImageWidth, tupleImageHeight, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D alpha_edImageGraphics = alpha_edImage.createGraphics();
                    alpha_edImageGraphics.drawImage(tupleImage, 0, 0, null);
                    alpha_edImageGraphics.dispose();
                    alpha_edImage = makeImageTranslucent(alpha_edImage, alpha);
                    tupleImage = alpha_edImage;
                    alpha_edImage = null;
                }

                int xCoord = 0;
                switch(imageTuple.getAnchor()) {
                    case UPPER_LEFT:   case MIDDLE_LEFT:   case LOWER_LEFT:
                        xCoord = 0;
                        break;

                    case UPPER_CENTER: case MIDDLE_CENTER: case LOWER_CENTER:
                        xCoord = (int) (baseWidth / 2 - scaledWidth / 2);
                        break;

                    case UPPER_RIGHT:  case MIDDLE_RIGHT:  case LOWER_RIGHT:
                        xCoord = (int) (baseWidth - scaledWidth);
                        break;
                }

                int yCoord = 0;
                switch(imageTuple.getAnchor()) {
                    case UPPER_LEFT:  case UPPER_CENTER:  case UPPER_RIGHT:
                        yCoord = 0;
                        break;

                    case MIDDLE_LEFT: case MIDDLE_CENTER: case MIDDLE_RIGHT:
                        yCoord = (int) (baseHeight / 2 - scaledHeight / 2);
                        break;

                    case LOWER_LEFT:  case LOWER_CENTER:  case LOWER_RIGHT:
                        yCoord = (int) (baseHeight - scaledHeight);
                        break;
                }

                preps.add(
                    new ImagePrep(
                        tupleImage,
                        xCoord + imageTuple.getXOffset(),
                        yCoord + imageTuple.getYOffset(),
                        (int) scaledWidth,
                        (int) scaledHeight
                    )
                );
            }
        }

        BufferedImage canvas = new BufferedImage(baseWidth, baseHeight, BufferedImage.TYPE_INT_ARGB);
        if(!preps.isEmpty()) {
            Graphics g = canvas.getGraphics();
            for(ImagePrep prep : preps) {
                g.drawImage(prep.image, prep.x, prep.y, prep.w, prep.h, null);
            }
            g.dispose();
        }
        return canvas;
    }

    private static BufferedImage makeImageTranslucent(BufferedImage source, double alpha) {
        BufferedImage translucentImage = new BufferedImage(source.getWidth(),
            source.getHeight(), Transparency.TRANSLUCENT);
        Graphics2D g = translucentImage.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) alpha));
        g.drawImage(source, null, 0, 0);
        g.dispose();
        return translucentImage;
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private static class ImagePrep {
        Image image;
        int x, y, w, h;
        public ImagePrep(Image image, int x, int y, int w, int h) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
}
