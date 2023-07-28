package replete.ui.thumbs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

import replete.ui.lay.Lay;
import replete.ui.windows.notifications.NotificationFrame;

public class ImageTestFrame extends NotificationFrame {
    ThumbnailSidebar sidebar;
    ThumbnailList lstImage;

    public ImageTestFrame() {
        final JSplitPane spl;
        Lay.BLtg(this,
            "C", spl = Lay.SPL("X",
                Lay.hn(sidebar = new ThumbnailSidebar(true, SwingConstants.EAST), "minw=154"),
                Lay.p()
            ),
            "E", Lay.sp(lstImage = new ThumbnailList(3), "prefw=200"),
            "size=[600,600],center"
        );
        setShowStatusBar(true);
        sidebar.addHideListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                spl.setDividerLocation(0);
            }
        });

        File[] files = {
            new File("C:\\Users\\dtrumbo\\Pictures\\a-ele.jpg"),
            new File("C:\\Users\\dtrumbo\\Pictures\\a-owl1.jpg"),
            new File("C:\\Users\\dtrumbo\\Pictures\\a-owl2.jpg")
        };
        for(File f : files) {
            lstImage.add(f, f.getAbsolutePath(), f.getName());
        }

        ThumbnailSidebarEntry[] entries = {
           new ThumbnailSidebarEntry("HI", get(files[0])),
           new ThumbnailSidebarEntry("HI2", get(files[1])),
           new ThumbnailSidebarEntry("HI3", get(files[2]))
        };

        sidebar.setThumbnails(entries);
    }

    private BufferedImage get(File file) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        new ImageTestFrame().setVisible(true);
    }
}
