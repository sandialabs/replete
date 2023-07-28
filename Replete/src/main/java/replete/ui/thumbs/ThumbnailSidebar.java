package replete.ui.thumbs;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionListener;

import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.util.MemoryUtil;

public class ThumbnailSidebar extends JPanel {

    private JButton btnHide;
    private ThumbnailList lstThumbnails;
    //private ThumbnailListModel mdlThumbnails;
    private ThumbnailSidebarEntry[] unscaledEntries;
    private ThumbnailSidebarEntry[] scaledEntries;

    // Two error fields so that while the rescale thread is
    // looking for a memory error (it resets the local
    // error boolean), a repaint of the list doesn't show
    // a portion of the list items as not being in error.
    // The list items only key off of the global error
    // boolean, which is reset only after the rescale
    // thread is completely done.
    private boolean localMemoryError;

    public ThumbnailSidebar(boolean showHideButton, int location) {

        Lay.BLtg(this,
            "C", Lay.sp(lstThumbnails = new ThumbnailList())
        );

        btnHide = new JButton("Hide Thumbnails");
        if(location == SwingConstants.WEST) {
            btnHide.setIcon(ImageLib.get(CommonConcepts.CLOSE));
            btnHide.setHorizontalTextPosition(SwingConstants.RIGHT);
        } else if(location == SwingConstants.EAST) {
            btnHide.setIcon(ImageLib.get(RepleteImageModel.KEY));
            btnHide.setHorizontalTextPosition(SwingConstants.LEFT);
        }
        btnHide.setIconTextGap(10);
        if(showHideButton) {
            add(btnHide, BorderLayout.NORTH);
        }

        clear();
    }

    public void clear() {
        lastRescaleWidth = -1;
        desiredIndex = -1;
        //mdlThumbnails = new ThumbnailListModel();
        unscaledEntries = new ThumbnailSidebarEntry[0];
        scaledEntries = new ThumbnailSidebarEntry[0];
        //lstThumbnails.setModel(mdlThumbnails);
        lstThumbnails.setListData(scaledEntries);
        setPreferredSize(getMinimumSize());
    }

    public void addHideListener(ActionListener listener) {
        btnHide.addActionListener(listener);
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        lstThumbnails.addListSelectionListener(listener);
    }

    protected int desiredIndex = -1;
    public void setSelectedThumbnail(int index) {

        // Only allow the list to have its selected index change
        // if it's not currently in 'update' mode.
        if(!lstThumbnails.isUpdating()) {
            lstThumbnails.setSelectedIndex(index);
            lstThumbnails.ensureSelectedVisible();
        }

        // However, always save the value that was supposed
        // to be set as the selected index.
        desiredIndex = index;
    }

    //    public void setThumbnails(ThumbnailListModel newModel) {
    //        mdlThumbnails = newModel;
    //        lstThumbnails.setModel(mdlThumbnails);
    //        rescaleEntries();
    //    }

    public void setThumbnails(ThumbnailSidebarEntry[] newUnscaledEntries) {
        unscaledEntries = newUnscaledEntries;
        //        mdlThumbnails = newModel;
        //        lstThumbnails.setModel(mdlThumbnails);

        lastRescaleWidth = -1;      // Make sure the rescale occurs.
        int rows = (int) Math.ceil(newUnscaledEntries.length / 4.0);
        lstThumbnails.setVisibleRowCount(rows);

        rescaleEntries();
    }

    public int[] getSelectedThumbnails() {
        return lstThumbnails.getSelectedIndices();
    }

    public boolean willRescaleAtCurrentWidth() {
        final int minPanelWidth = getMinimumSize().width;
        final int curPanelWidth = getWidth();
        int maxThumbnailWidth = Math.max(curPanelWidth, minPanelWidth) - 30;
        if(maxThumbnailWidth == lastRescaleWidth || unscaledEntries.length == 0 /*mdlThumbnails.size() == 0*/) {
            return false;
        }
        return true;
    }

    protected int lastRescaleWidth = -1;
    public void rescaleEntries() {
        final int minPanelWidth = getMinimumSize().width;
        final int curPanelWidth = getWidth();
        int maxThumbnailWidth = Math.max(curPanelWidth, minPanelWidth) - 30;
        int maxThumbnailHeight = (int) (maxThumbnailWidth * 0.8);
        float maxRatio = (float) maxThumbnailWidth / maxThumbnailHeight;

        // If we haven't changed the width at which
        // we last scaled the thumbnails or we don't
        // have any images curently, don't do anything.
        if(maxThumbnailWidth == lastRescaleWidth || unscaledEntries.length == 0 /*mdlThumbnails.size() == 0*/) {
            return;
        }

        lstThumbnails.setListData(new ThumbnailSidebarEntry[0]);  // does this fix stuff?
        lstThumbnails.setUpdating(true);

        // Rescale the images in a data thread.
        //        CommonThread rescaleThread = new CommonThread() {
        //            @Override
        //            public void runThread() {


        scaledEntries = new ThumbnailSidebarEntry[unscaledEntries.length];

        localMemoryError = false;
        //DU.pa("List", minWidth, currentWidth, maxWidth, maxHeight, maxRatio);
        for(int entryNum = 0; entryNum < unscaledEntries.length/*mdlThumbnails.size()*/; entryNum++) {
            ThumbnailSidebarEntry entry = unscaledEntries[entryNum];//(ThumbnailSidebarEntry) mdlThumbnails.get(entryNum);
            BufferedImage srcImage = (BufferedImage) entry.getImage();
            float srcRatio = (float) srcImage.getWidth() / srcImage.getHeight();
            //DU.pa("Source " + entryNum, srcImage.getWidth(), srcImage.getHeight(), srcRatio);
            int scaleWidth;
            int scaleHeight;
            if(srcRatio > maxRatio) {
                scaleWidth = maxThumbnailWidth;
                scaleHeight = (int)((float) maxThumbnailWidth * srcImage.getHeight() / srcImage.getWidth());
            } else {
                scaleHeight = maxThumbnailHeight;
                scaleWidth = (int)((float) maxThumbnailHeight * srcImage.getWidth() / srcImage.getHeight());
            }
            //DU.pa("  Chosen", scaleWidth, scaleHeight);
            BufferedImage scaledImage;
            if(srcImage.getWidth() <= scaleWidth && srcImage.getHeight() <= scaleHeight) {
                scaledImage = srcImage;
            } else {
                scaledImage = getScaledImage(srcImage, scaleWidth, scaleHeight);

                // In this case, possibly much more memory will be needed.
                // Stop iterating if there was a memory error.
                if(localMemoryError) {
                    break;
                }
            }
            scaledEntries[entryNum] = new ThumbnailSidebarEntry(entry.getTitle(), new ImageIcon(scaledImage));
            //entry.setIcon(new ImageIcon(scaledImage));
        }

        if(localMemoryError) {

            // Free the memory used by the scaled images.
            for(int entryNum = 0; entryNum < unscaledEntries.length /*mdlThumbnails.size()*/; entryNum++) {
                if(scaledEntries[entryNum] != null) {
                    scaledEntries[entryNum].setIcon(null);
                } else {
                    scaledEntries[entryNum] = new ThumbnailSidebarEntry(unscaledEntries[entryNum].getTitle(), (ImageIcon) null);
                }
                //                        ThumbnailSidebarEntry entry = (ThumbnailSidebarEntry) mdlThumbnails.get(entryNum);
                //                        entry.setIcon(null);
            }
        }

        //                commonResult.setComplete(true);
        //                fireProgressUpdate();
        //            }


        //        };
        //
        //        rescaleThread.addProgressListener(new ChangeListener() {
        //            public void stateChanged(ChangeEvent arg0) {
        lstThumbnails.setGlobalMemoryError(localMemoryError);
        lstThumbnails.setListData(scaledEntries);
        lstThumbnails.setUpdating(false);
        lastRescaleWidth = maxThumbnailWidth;
        setSelectedThumbnail(desiredIndex);
        MemoryUtil.attemptToReclaim();
        //            }
        //        });
        //
        //        rescaleThread.start();
    }

    protected BufferedImage getScaledImage(BufferedImage srcImg, int w, int h) {
        int type = srcImg.getType();

        if(type == 0) {

            // This area could be made much more complete.
            // First of all, why does getType return 0 some-
            // times?  Secondly, there's also
            // img.getColorModel().getColorSpace().getType().
            // For example, what if there are 3 components.
            // How do we know the type is not one of the other
            // "RGB" types:
            //   TYPE_INT_BGR    4
            //   TYPE_INT_RGB    1
            //   TYPE_USHORT_555_RGB     9
            //   TYPE_USHORT_565_RGB     8
            // Should be able to diagnose why type is zero in
            // the first place.
            switch(srcImg.getColorModel().getNumComponents()) {
                case 1: type = BufferedImage.TYPE_BYTE_GRAY; break;
                case 3: type = BufferedImage.TYPE_3BYTE_BGR; break;
                case 4: type = BufferedImage.TYPE_4BYTE_ABGR; break;
                default: type = BufferedImage.TYPE_INT_BGR; break;
            }
        }

        try {
            System.out.println(w + " - " + h);
            BufferedImage resizedImg = new BufferedImage(w, h, type);
            Graphics2D g2 = resizedImg.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(srcImg, 0, 0, w, h, null);
            g2.dispose();
            return resizedImg;
        } catch(OutOfMemoryError e) {
            localMemoryError = true;
            return null;
        }
    }

    /*    public void print() {
    DU.p("--unscaled");
    for(ThumbnailSidebarEntry e : unscaledEntries) {
        DU.pa(e.getTitle(), ((BufferedImage) e.getImage()).getWidth(), ((BufferedImage) e.getImage()).getHeight());
    }
    DU.p("--scaled");
    for(ThumbnailSidebarEntry e : scaledEntries) {
        DU.pa(e.getTitle(), e.getIcon().getIconWidth(), e.getIcon().getIconHeight());
    }
    ListModel lm = lstThumbnails.getModel();
    for(int x = 0; x < lm.getSize(); x++) {
        Object o = lm.getElementAt(x);
        if(o != scaledEntries[x]) {
            System.out.println("Error " + x + " ");
            DU.printObjectDetails(o);
        }
    }
    lstThumbnails.doLayout();
    lstThumbnails.setListData(new Object[0]);
    lstThumbnails.setListData(scaledEntries
                    );
}*/
}
