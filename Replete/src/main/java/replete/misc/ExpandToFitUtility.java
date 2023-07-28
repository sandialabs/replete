package replete.misc;

/**
 * A small utility class to maintain proportions when
 * drawing images in a container.
 *
 * @author Derek Trumbo
 */

public class ExpandToFitUtility {

    public static class Inputs {

        // Any blank space desired as padding around the fitted
        // rectangle.
        public int topIndent = 0;
        public int bottomIndent = 0;
        public int leftIndent = 0;
        public int rightIndent = 0;

        // Typically the current width and height of the container
        // collected at the moment of componentResized.
        public int availableWidth = 0;
        public int availableHeight = 0;

        // The width and height of the rectangle area that you
        // desire to display.  You don't have to display something
        // rectangular - this is just the rectangle in which what
        // want to display will be drawn.
        public int desiredWidth = 0;
        public int desiredHeight = 0;
    }

    public static class Outputs {

        // The upper-left hand corner where the fitted rectangle
        // should be drawn in the container.
        public int fittedX;
        public int fittedY;

        // The width and height of the fitted rectangle.
        public int fittedWidth;
        public int fittedHeight;
    }

    public static Outputs fit(Inputs inputs) {

        // Container details.
        int availW = inputs.availableWidth;
        int availH = inputs.availableHeight;
        int availX = 0;
        int availY = 0;

        // Container with padding included.
        int croppedW = availW - inputs.leftIndent - inputs.rightIndent;
        int croppedH = availH - inputs.topIndent - inputs.bottomIndent;
        int croppedX = availX + inputs.leftIndent;
        int croppedY = availY + inputs.topIndent;

        // Desired width and height.
        int desiredW = inputs.desiredWidth;
        int desiredH = inputs.desiredHeight;

        // The values we need to find for the fitted rectangle.
        int fittedX, fittedY, fittedW, fittedH;

        // If the H/W ratio of the cube is the same as the available area,
        // expand the cube to fill the area.
        if( croppedH * desiredW == desiredH * croppedW )
        {
            fittedW = croppedW;
            fittedH = croppedH;
            fittedX = croppedX;
            fittedY = croppedY;
        }

        // Else we much decide which dimension will full fill the available
        // area, and how long the other dimension will be.
        else
        {
            // Try setting the new cube width to the width of the available area.
            fittedW = croppedW;      // = iw * ( bwp / iw );

            // Change the height by the same ratio.
            fittedH = ( int ) ( desiredH * ( ( double ) croppedW / desiredW ) );

            // If the calculated height will fit in the available area
            // just set the upper left coordinate now.
            if( fittedH <= croppedH )
            {
                fittedX = croppedX;    // Same as available area cause width made to match.
                fittedY = croppedY + ( croppedH - fittedH ) / 2;
            }

            // Else if the calculated height is larger than the height of the
            // available area, it must not be the width that will stretch
            // but the height instead.
            else
            {

                // Set the height of the new cube to the height of the available area.
                fittedH = croppedH;      // = ih * ( bhp / ih );

                // Change the width by the same ratio.
                fittedW = ( int ) ( desiredW * ( ( double ) croppedH / desiredH ) );

                if( fittedW > croppedW ) {
                    throw new RuntimeException( "Invalid state.  fittedW > croppedW" );
                }

                fittedY = croppedY;    // Same as available area cause height made to match.
                fittedX = croppedX + ( croppedW - fittedW ) / 2;
            }
        }

        Outputs outputs = new Outputs();
        outputs.fittedX = fittedX;
        outputs.fittedY = fittedY;
        outputs.fittedWidth = fittedW;
        outputs.fittedHeight = fittedH;
        return outputs;
    }
}