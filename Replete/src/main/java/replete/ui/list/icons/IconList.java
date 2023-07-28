package replete.ui.list.icons;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.ListModel;

import replete.ui.Iconable;
import replete.ui.button.RButton;
import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.list.RList;
import replete.ui.windows.ExampleFrame;


public class IconList extends RList {


    ////////////
    // FIELDS //
    ////////////

    protected IconListCellRenderer renderer = new IconListCellRenderer();
    protected Map<?, Icon> iconMap = null;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public IconList() {
        init();
    }
    public IconList(ListModel dataModel) {
        super(dataModel);
        init();
    }
    public IconList(Object[] listData) {
        super(listData);
        init();
    }
    public IconList(Vector<?> listData) {
        super(listData);
        init();
    }
    public IconList(Map<?, Icon> map) {
        init(map);
    }
    public IconList(ListModel dataModel, Map<?, Icon> map) {
        super(dataModel);
        init(map);
    }
    public IconList(Object[] listData, Map<?, Icon> map) {
        super(listData);
        init(map);
    }
    public IconList(Vector<?> listData, Map<?, Icon> map) {
        super(listData);
        init(map);
    }


    ////////////////////
    // INITIALIZATION //
    ////////////////////

    protected void init() {
        init(null);
    }
    protected void init(Map<?, Icon> map) {
        renderer.setIconMap(map);
        iconMap = map;
        setCellRenderer(renderer);
    }


    ////////////////////////
    // ACCESSOR / MUTATOR //
    ////////////////////////

    public void setIconInsets(Insets insets) {
        renderer.setInsets(insets);
        updateUI();              // Must be called instead of repaint.
    }
    public Insets getIconInsets() {
        return renderer.getInsets();
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        ExampleFrame frame = new ExampleFrame();
        Object[] data = new Object[] {new IconObject("Apples"), "Orange", "Banana"};
        Map<String, Icon> iconMap = new HashMap<>();
        iconMap.put("Orange", ImageLib.get(RepleteImageModel.ORANGE));
        iconMap.put("Banana", ImageLib.get(RepleteImageModel.BANANA));
        final IconList lst = new IconList(data, iconMap);
        JButton btnChange = new RButton("&Change");
        btnChange.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lst.setIconInsets(new Insets(3, 10, 20, 15));
                lst.setEnabled(false);
                lst.setBackground(Color.green);
            }
        });
        Lay.BLtg(frame,
            "W", Lay.sp(lst, "prefW=200"),
            "E", Lay.FL(btnChange),
            "visible=true"
        );
    }

    public static class IconObject implements Iconable {
        private String str;
        public IconObject(String s) {
            str = s;
        }
        @Override
        public Icon getIcon() {
            return ImageLib.get(RepleteImageModel.APPLE);
        }
        @Override
        public String toString() {
            return str;
        }
    }
}
