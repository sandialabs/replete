package replete.ui.tabbed;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.event.ExtChangeNotifier;
import replete.ui.button.IconButton;
import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;


public class RTabHeaderPanel extends JPanel {


    ////////////
    // FIELDS //
    ////////////

    private static Icon dirtyIcon = ImageLib.get(CommonConcepts.DIRTY);

    private JLabel lblIcon;
    private JLabel lblTitle;       // Optionally displays the dirty icon
    private IconButton btnClose;
    private JPanel pnlOptions;

    private List<Component> extraComponents = new ArrayList<>();

    private boolean closeable;
    private Object metadata;       // Extra arbitrary information that can be attached to panes


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RTabHeaderPanel(TabCreationDescriptor desc) {
        metadata = desc.getMetadata();

        Icon icon = desc.getIcon();
        lblIcon = Lay.lb(icon, mouseListener);
        lblTitle = Lay.lb(desc.getTitle(), icon == null ? "" : "eb=4l", mouseListener);

        btnClose = new IconButton(CommonConcepts.CLOSE);
        btnClose.toImageOnly();
        btnClose.setToolTipText(desc.getCloseToolTip());
        btnClose.setRolloverIcon(CommonConcepts.REMOVE);
        btnClose.setPressedIcon(RepleteImageModel.CLOSE_HOV_DOWN);
        btnClose.addActionListener(e -> fireCloseNotifier());

        Lay.BLtg(this,
            "W", lblIcon.getIcon() == null ? null : lblIcon,
            "C", lblTitle,
            "E", pnlOptions = Lay.BxL("X",
                "opaque=false"
            ),
            "opaque=false,eb=2"
        );

        setToolTipText(desc.getTip());
        setFocusable(false);
        addMouseListener(mouseListener);

        closeable = desc.isCloseable() != null && desc.isCloseable();
        setDirty(desc.isDirty() != null && desc.isDirty());
        extraComponents.addAll(desc.getExtraComponents());
        rebuildExtraComponentsPanel();
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    private ChangeNotifier closeNotifier = new ChangeNotifier(this);
    public void addCloseListener(ChangeListener listener) {
        closeNotifier.addListener(listener);
    }
    private void fireCloseNotifier() {
        closeNotifier.fireStateChanged();
    }

    private ChangeNotifier switchTabNotifier = new ChangeNotifier(this);
    public void addSwitchTabListener(ChangeListener listener) {
        switchTabNotifier.addListener(listener);
    }
    private void fireSwitchTabNotifier() {
        switchTabNotifier.fireStateChanged();
    }

    protected ExtChangeNotifier<HeaderContextMenuListener> headerContextMenuNotifier =
        new ExtChangeNotifier<>();
    public void addHeaderContextMenuListener(HeaderContextMenuListener listener) {
        headerContextMenuNotifier.addListener(listener);
    }
    protected void fireHeaderContextMenuNotifier(String key, Component component, int x, int y) {
        HeaderContextMenuEvent e = new HeaderContextMenuEvent(key, component, x, y);
        headerContextMenuNotifier.fireStateChanged(e);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isDirty() {
        return lblTitle.getIcon() != null;
    }
    public boolean isCloseable() {
        return closeable;
    }
    public Icon getIcon() {
        return lblIcon.getIcon();
    }
    public String getTitle() {
        return lblTitle.getText();
    }
    public Object getMetadata() {
        return metadata;
    }
    public JLabel getLabel() {
        return lblTitle;
    }

    // Mutators

    public void setDirty(boolean dirty) {
        if(!dirty) {
            lblTitle.setIcon(null);
        } else {
            lblTitle.setIcon(dirtyIcon);
        }
    }
    public void setCloseable(boolean closeable) {
        this.closeable = closeable;
        rebuildExtraComponentsPanel();
    }
    public void addExtraComponent(Component c) {
        extraComponents.add(c);
        rebuildExtraComponentsPanel();
    }
    public void removeExtraComponent(Component c) {
        extraComponents.remove(c);
        rebuildExtraComponentsPanel();
    }
    public void setIcon(Icon icon) {
        if(icon == null) {
            lblIcon.setIcon(null);
            remove(lblIcon);
        } else {
            lblIcon.setIcon(icon);
            add(lblIcon, BorderLayout.WEST);
        }
    }
    public void setTitle(String title) {
        lblTitle.setText(title);
    }
    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }
    public void setUseBorder(boolean use) {
        if(use) {
            setBorder(Lay.eb("2"));
        } else {
            setBorder(Lay.eb("0"));
        }
    }
    public void setHeight(int height) {
        Lay.hn(this, "prefh=" + height);
    }
    @Override
    public void setToolTipText(String tip) {
        super.setToolTipText(tip);      // A few pixels in the header tab display this one.
        lblTitle.setToolTipText(tip);
    }


    //////////
    // MISC //
    //////////

    private MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            if(SwingUtilities.isRightMouseButton(e) && !(e.getComponent() instanceof AbstractButton)) {
                fireHeaderContextMenuNotifier(null, e.getComponent(), e.getX(), e.getY());
            } else {
                fireSwitchTabNotifier();
            }
        }
    };

    private void rebuildExtraComponentsPanel() {
        pnlOptions.removeAll();
        List<Component> extraComponentsBonus = new ArrayList<>(extraComponents);
        if(closeable) {
            extraComponentsBonus.add(btnClose);
        }
        boolean first = true;
        for(Component c : extraComponentsBonus) {
            c.removeMouseListener(mouseListener);  // Remove so only 1 copy of listener attached
            c.addMouseListener(mouseListener);
            int padding = first ? 3 : 0;
            pnlOptions.add(
                Lay.BL(
                    "C", c,
                    "eb=" + (2 + padding) + "l,!opaque",
                    mouseListener      // So clicking on the thin wrapper panel also causes a tab swtich
                )
            );
            first = false;
        }
        revalidate();
    }
}
