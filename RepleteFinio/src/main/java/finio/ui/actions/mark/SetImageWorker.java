package finio.ui.actions.mark;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;

import finio.core.FConst;
import finio.core.FUtil;
import finio.core.NonTerminal;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.images.ImageStoreSelectionDialog;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;
import replete.io.FileUtil;
import replete.text.StringUtil;

public class SetImageWorker extends FWorker<ImageIcon, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public SetImageWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected ImageIcon gather() {
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
//            System.out.println(dialog.getSelectedName() + "/" + dialog.getSelectedImage());
            return dialog.getSelectedImage();
        }
        return null;
    }

    @Override
    protected boolean proceed(ImageIcon gathered) {
        return gathered != null;
    }

    @Override
    protected Void background(ImageIcon gathered) throws Exception {
        for(SelectionContext C : getValidSelected()) {
            if(FUtil.isNonTerminal(C.getV())) {
                NonTerminal M = (NonTerminal) C.getV();
                M.putSysMeta(FConst.SYS_IMAGES, gathered);
            } else {
                NonTerminal M = (NonTerminal) C.getParentV();
                M.describe(C.getK());
                NonTerminal M2 = (NonTerminal) M.get(C.getK());
                M2.putSysMeta(FConst.SYS_IMAGES, gathered);
            }
//            select(
//                new SelectRequest()
//                    .setContext(C)
//                    .setAction(SelectAction.CHILDREN)
//            );
        }
        return null;
    }

    @Override
    public String getActionVerb() {
        return "setting the images";
    }
}
