package gotstyle;

import java.io.File;

import gotstyle.model.Ex;
import gotstyle.model.GotStyleModel;
import gotstyle.model.Pg;
import gotstyle.ui.AppData;
import gotstyle.ui.GotStyleFrame;
import replete.io.FileUtil;
import replete.ui.fc.RFileChooser;
import replete.util.AppMain;
import replete.util.User;
import replete.xstream.XStreamWrapper;

public class GotStyleAppMain extends AppMain {


    ////////////
    // FIELDS //
    ////////////'

    private static final File appData = User.getHome(".gotstyle");
    private static File cp;


    //////////
    // MAIN //
    //////////

    public static void main(String[] args) {

        XStreamWrapper.addAlias(GotStyleModel.class);
        XStreamWrapper.addAlias(Ex.class);
        XStreamWrapper.addAlias(Pg.class);

        AppData savedData;
        if(FileUtil.isReadableFile(appData)) {
            try {
                savedData = XStreamWrapper.loadTarget(appData);
            } catch(Exception e) {
                savedData = new AppData();
            }
        } else {
            savedData = new AppData();
        }

        cp = savedData.chooserPath;

        if(savedData.chooserPath != null) {
            File curDir = savedData.chooserPath;
            while(curDir != null && !curDir.exists()) {
                curDir = curDir.getParentFile();
            }
            if(curDir != null) {
                RFileChooser.getChooserAsIs().setCurrentDirectory(curDir);
            }
        }

        RFileChooser.addFileSelectedListener(e -> {
            File f = RFileChooser.getChooserAsIs().getSelectedFile();
            cp = f.isDirectory() ? f : f.getParentFile();
        });

        GotStyleFrame win = new GotStyleFrame(savedData.albumPath);

        win.addClosingListener(e -> {
            File albumPath = win.getAlbumPath();
            try {
                AppData newSavedData = new AppData();
                newSavedData.albumPath = albumPath;
                newSavedData.chooserPath = cp;
                XStreamWrapper.writeToFile(newSavedData, appData);
            } catch(Exception e1) {
                e1.printStackTrace();
            }

            if(albumPath != null) {
                GotStyleModel model = win.getModel();
                try {
                    XStreamWrapper.writeToFile(model, albumPath);
                } catch(Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        win.setVisible(true);
    }
}
