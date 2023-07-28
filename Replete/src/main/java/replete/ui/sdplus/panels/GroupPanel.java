package replete.ui.sdplus.panels;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.ui.GuiUtil;
import replete.ui.panels.GradientPanel;
import replete.ui.sdplus.UiDefaults;
import replete.util.OsUtil;


/**
 * A panel which serves as the header & separator for a
 * group of scale panels in the scale set panel.  This
 * panel is constructed very similarly to ScalePanel.
 *
 * @author Derek Trumbo
 */

public class GroupPanel extends GradientPanel {

    ////////////
    // Fields //
    ////////////

    // Model
    protected GroupPanelModel model;

    // UI
    protected JPanel pnlNotes;
    protected JPanel pnlFilter;
    protected JLabel lblIcon;
    protected JLabel lblTitle;
    protected JLabel lblNotes;
    protected JPanel pnlTitle;
    protected JLabel lblTitleCounts;

    // UI Settings
    protected Font titleFont = UiDefaults.GRP_TITLE_FONT;
    protected Font notesFont = UiDefaults.GRP_NOTES_FONT;
    protected int titleMargin = UiDefaults.GRP_TITLE_MARGIN;
    protected ImageIcon openIcon = UiDefaults.GRP_OPEN_ICON;
    protected ImageIcon closedIcon = UiDefaults.GRP_CLOSED_ICON;
    protected Color initBackground = UiDefaults.GRP_BACKGROUND_COLOR;
    protected ChangeNotifier openCloseNotifier = new ChangeNotifier(this);

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public GroupPanel(GroupPanelModel m) {
        model = m;
        build();
    }

    ///////////
    // Build //
    ///////////

    public void build() {
        setColors(initBackground, initBackground.darker());
        setBackground(initBackground);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        buildScaleNameLabel();

        buildTitlePanel();

        pnlTitle.addMouseListener(new TitlePanelMouseListener());

        add(pnlTitle);

        pnlNotes = buildNotesPanel();
        if(pnlNotes != null) {
            add(pnlNotes);
        }

        setOpen(model.isOpen());

        updateBorder();
    }

    protected void buildTitlePanel() {

        lblIcon = new JLabel(model.isOpen() ? openIcon : closedIcon);

        pnlTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlTitle.setAlignmentX(LEFT_ALIGNMENT);
        pnlTitle.setOpaque(false);

        pnlTitle.add(lblIcon);
        pnlTitle.add(lblTitle);

        pnlTitle.setBorder(BorderFactory.createEmptyBorder(titleMargin,
            titleMargin, titleMargin, titleMargin));
    }

    public JLabel buildScaleNameLabel() {
        lblTitle = new JLabel(" " + model.getName() + " ");
        lblTitle.setOpaque(false);
        lblTitle.setFont(titleFont);
        return lblTitle;
    }

    public JPanel buildNotesPanel() {
        String notesStr = "";
        String someNote = model.getNote();

        if(someNote != null && !someNote.equals("")) {
            notesStr = "<html>" + someNote + "</html>";
        }

        lblNotes = new JLabel(notesStr);
        lblNotes.setFont(notesFont);
        lblNotes.setOpaque(false);

        JPanel pnlNotes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JPanel pnlNotes2 = GuiUtil.addBorderedComponent(pnlNotes, lblNotes,
            BorderFactory.createEmptyBorder(0, (titleMargin - 1), 0, 0));
        pnlNotes2.setOpaque(false);
        pnlNotes.setAlignmentX(LEFT_ALIGNMENT);
        pnlNotes.setOpaque(false);
        return pnlNotes;
    }

    //////////////////////////
    // Accessors / Mutators //
    //////////////////////////

    // Accessors

    public GroupPanelModel getGroupPanelModel() {
        return model;
    }

    // UI Settings

    public Font getTitleFont() {
        return titleFont;
    }
    public Font getNotesFont() {
        return notesFont;
    }
    public int getTitleMargin() {
        return titleMargin;
    }
    public ImageIcon getOpenIcon() {
        return openIcon;
    }
    public ImageIcon getClosedIcon() {
        return closedIcon;
    }

    // Other

    public String getTitle() {
        if(lblTitle == null) {
            return null;
        }
        return lblTitle.getText().trim();
    }

    // Mutators

    // UI Settings

    public void setTitleFont(Font font) {
        titleFont = font;
        if(lblTitle != null) {
            lblTitle.setFont(font);
        }
    }
    public void setNotesFont(Font font) {
        notesFont = font;
        if(lblNotes != null) {
            lblNotes.setFont(font);
        }
    }
    public void setTitleMargin(int margin) {
        titleMargin = margin;
        pnlTitle.setBorder(BorderFactory.createEmptyBorder(
            margin, margin, margin, margin));
    }
    public void setOpenIcon(ImageIcon i) {
        openIcon = i;
        updateIcon();
    }
    public void setClosedIcon(ImageIcon i) {
        closedIcon = i;
        updateIcon();
    }
    @Override
    public void setBackground(Color clr) {
        setColors(clr, clr.darker());
        super.setBackground(clr);
    }

    ////////////
    // Update //
    ////////////

    // Updates the group panel's icon based on:
    // - Open icon
    // - Closed icon
    // - Whether or not the panel is open
    // This should be called any time one of the above is changed.
    protected void updateIcon() {
        lblIcon.setIcon(model.isOpen() ? openIcon : closedIcon);
    }

    protected void updateBorder() {
        Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED,
            getBackground().darker(), getBackground().darker());
        setBorder(border);
    }

    //////////
    // Misc //
    //////////

    public void setOpen(boolean open) {
        model.setOpen(open);
        if(pnlNotes != null) {
            pnlNotes.setVisible(open);
        }
        if(pnlFilter != null) {
            pnlFilter.setVisible(open);
        }
        updateIcon();
        openCloseNotifier.fireStateChanged();
    }

    public void addHighlightedListener(ChangeListener listener) {
        openCloseNotifier.addListener(listener);
    }

    /////////////////////
    // Mouse Listeners //
    /////////////////////

    // Handles expanding/collapsing for the title panel.
    protected class TitlePanelMouseListener extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {

            // If the regular click button was clicked and we're
            // in Windows, toggle the open-state.
            if(OsUtil.isWindows()) {
                setOpen(!model.isOpen());
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

            // If the regular click button was clicked and we're
            // not in Windows, toggle the open-state.
            if(!OsUtil.isWindows()) {
                setOpen(!model.isOpen());
            }
        }
    }
}
