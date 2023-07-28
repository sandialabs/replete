package replete.ui.slider;

import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;
import javax.swing.plaf.metal.MetalSliderUI;

public class RSlider extends JSlider {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RSlider() {}
    public RSlider(int orientation) {
        super(orientation);
    }
    public RSlider(BoundedRangeModel brm) {
        super(brm);
    }
    public RSlider(int min, int max) {
        super(min, max);
    }
    public RSlider(int min, int max, int value) {
        super(min, max, value);
    }
    public RSlider(int orientation, int min, int max, int value) {
        super(orientation, min, max, value);
    }


    //////////////
    // MUTATORS //
    //////////////

    public void enableSnapToClick() {
        setUI(new MetalSliderUI() {
            @Override
            protected void scrollDueToClickInTrack(int direction) {
                // this is the default behaviour, let's comment that out
                //scrollByBlock(direction);
                int value = getValue();
                if(getOrientation() == JSlider.HORIZONTAL) {
                    value = valueForXPosition(getMousePosition().x);
                } else if(getOrientation() == JSlider.VERTICAL) {
                    value = valueForYPosition(getMousePosition().y);
                }
                setValue(value);
            }
        });
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {

    }
}
