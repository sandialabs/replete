package replete.ui.images.concepts;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import replete.errors.RuntimeConvertedException;
import replete.text.StringUtil;
import replete.ui.ColorLib;
import replete.ui.GuiUtil;
import replete.ui.images.shared.SharedImage;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.windows.escape.EscapeFrame;
import replete.util.ClassUtil;
import replete.util.ReflectionUtil;

public abstract class ImageModel {
    protected static ImageModelConcept concept(ImageProducer producer) {
        return new ImageModelConcept(producer);
    }
    protected static ImageModelConcept conceptShared(SharedImage sharedImage) {
        return concept(new SharedImageProducer(sharedImage.getFileName()));
    }
    protected static ImageModelConcept conceptLocal(String fileName) {
        Class<?> clazz = ClassUtil.getCallingClass(1);
        ImageProducer producer = () -> GuiUtil.getImage(clazz, fileName);
        return concept(producer);
    }


    ///////////////
    // VISUALIZE //
    ///////////////

    public static void visualize() {
        Class<?> callingClass = ClassUtil.getCallingClass(1);
        if(!ImageModel.class.isAssignableFrom(callingClass) || callingClass.equals(ImageModel.class)) {
            throw new RuntimeException("Invalid class");
        };
        visualizeInternal(callingClass, 4);
    }
    public static void visualize(int columns) {
        Class<?> callingClass = ClassUtil.getCallingClass(1);
        if(!ImageModel.class.isAssignableFrom(callingClass) || callingClass.equals(ImageModel.class)) {
            throw new RuntimeException("Invalid class");
        };
        visualizeInternal(callingClass, columns);
    }
    private static void visualizeInternal(Class<?> callingClass, int columns) {

        try {
            Field[] origFields = ReflectionUtil.getFields(callingClass);
            List<Field> allowedFields = new ArrayList<>();
            for(int i = 0; i < origFields.length; i++) {
                Field field = origFields[i];
                Class<?> fieldType = field.getType();
                if(ImageModelConcept.class.isAssignableFrom(fieldType)) {
                    allowedFields.add(field);
                }
            }
            Field[] fields = allowedFields.toArray(new Field[0]);
            int rows = (int) Math.ceil((double) fields.length / columns);
            RPanel pnl = Lay.GL(rows, columns);
            Color[] colors = {Color.WHITE, ColorLib.BLUE_VERY_LIGHT};
            for(int r = 0; r < rows; r++) {
                Color bg = colors[r % colors.length];
                for(int c = 0; c < columns; c++) {
                    int i = r + c * rows;
                    add(callingClass, pnl, fields, i, bg);
                }
            }

            String extra;
            if(fields.length != origFields.length) {
                int delta = origFields.length - fields.length;
                extra = " (" + delta +
                    " additional non-" +
                    ImageModelConcept.class.getSimpleName() + " field" +
                    StringUtil.s(delta) + " ignored)";
            } else {
                extra = "";
            }

            EscapeFrame fra = Lay.fr(
                "Image Model Visualization for '" + callingClass.getSimpleName() + "'",
                CommonConcepts._PLACEHOLDER
            );

            Lay.BLtg(fra,
                "N", pnl,
                "S", Lay.BL(
                    "W", Lay.lb(fields.length + " Concepts in Image Model" + extra, "fg=white,eb=5l,size=14"),
                    "E", Lay.FL(Lay.btn("&Close", CommonConcepts.CANCEL, "closer")),
                    "chtransp,bg=100,mb=[1t,black]"
                ),
                "pack"
            );

            fra.setSize(fra.getSize().width + 10 * columns, fra.getSize().height);

            Lay.hn(fra, "center,visible");

        } catch(Exception e) {
            throw new RuntimeConvertedException(e);
        }
    }

    private static JLabel add(Class<?> clazz, JPanel pnl, Field[] fields,
                              int index, Color bg) throws Exception {
        JLabel lbl;
        if(index < fields.length) {
            Field field = fields[index];
            ImageModelConcept concept = (ImageModelConcept) field.get(clazz);
            String text = "<b>" + field.getName() + "</b>";
            ImageIcon icon = ImageLib.get(concept);
            if(icon.getIconHeight() > 16 || icon.getIconWidth() > 16) {
                text = text + " (" + icon.getIconWidth() + "x" + icon.getIconHeight() + ")";
                icon = ImageLib.get(CommonConcepts._PLACEHOLDER);
            }
            pnl.add(lbl = Lay.lb("<html>" + text + "</html>", icon, "!bold"));
        } else {
            pnl.add(lbl = Lay.lb());
        }
        Lay.hn(lbl, "bg=" + Lay.clr(bg));
        return lbl;
    }
}
