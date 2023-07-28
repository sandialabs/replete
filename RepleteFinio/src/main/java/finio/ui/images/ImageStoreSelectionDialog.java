package finio.ui.images;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import replete.io.FileUtil;
import replete.text.StringUtil;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeDialog;

public class ImageStoreSelectionDialog extends EscapeDialog {


    ////////////
    // FIELDS //
    ////////////

    public static final int OK = 0;
    public static final int CANCEL = 1;

    private int result = CANCEL;
    private Map<String, ImageIcon> images;
    private String returnName;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ImageStoreSelectionDialog(JFrame parent, Map<String, ImageIcon> images) {
        super(parent, "Select Image", true);
        this.images = images;

        setIcon(FinioImageModel.SET_IMAGE);

        int c = 8;
        int r = images.size() / c + 1;

        JPanel pnlGrid;
        JButton btnCancel;
        Lay.BLtg(this,
            "C", Lay.sp(pnlGrid = Lay.GL(r, c, "bg=white")),
            "S", Lay.FL("R", btnCancel = Lay.btn("&Cancel", CommonConcepts.CANCEL), "bg=100")
        );

        Lay.hn(this, "dim=[1230,800]");

        btnCancel.addActionListener(e -> close());

        for(final String name : images.keySet()) {
            final JLabel lbl;
            pnlGrid.add(
                lbl = Lay.lb(name, images.get(name),
                    "pref=[120,22],cursor=hand,eb=2")
            );
            lbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if(e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e)) {
                        returnName = name;
                        result = OK;
                        close();
                    }
                }
                @Override
                public void mouseExited(MouseEvent arg0) {
                    Lay.hn(lbl, "opaque=false,eb=2");
                }
                @Override
                public void mouseEntered(MouseEvent arg0) {
                    Lay.hn(lbl, "bg=FFF877,opaque=true,eb=1,augb=mb(1,ADA751)");
                }
            });
        }
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getResult() {
        return result;
    }
    public String getSelectedName() {
        return returnName;
    }
    public ImageIcon getSelectedImage() {
        return images.get(returnName);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
//        EscapeFrame frame = new EscapeFrame("Image Store Selection");
//        File fileDir = new File("C:\\Users\\dtrumbo\\work\\eclipse-main\\Finio\\src\\finio\\ui\\images");
        File fileDir = new File("C:\\Users\\dtrumbo\\work\\eclipse-main\\Images\\eclipse");
        Map<String, ImageIcon> images = new TreeMap<>();
        final List<String> exts = Arrays.asList(new String[] {"jpg", "tif", "gif", "png"});
        FileFilter filter = new FileFilter() {
            public boolean accept(File arg0) {
                if(arg0.isDirectory()) {
                    return true;
                }
                String ext = StringUtil.lcSafe(FileUtil.getExtension(arg0));
                return exts.contains(ext);
            }
        };
        for(File file : FileUtil.find(fileDir, filter)) {
            ImageIcon image = new ImageIcon(file.getAbsolutePath());
            if(image.getIconWidth() == 16 && image.getIconHeight() == 16) {
                images.put(file.getName(), image);
            }
        }
        ImageStoreSelectionDialog dialog = new ImageStoreSelectionDialog(null, images);
        dialog.pack();
        dialog.center();
        dialog.setVisible(true);

        if(dialog.getResult() == ImageStoreSelectionDialog.OK) {
            System.out.println(dialog.getSelectedName() + "/" + dialog.getSelectedImage());
        }
    }
}
