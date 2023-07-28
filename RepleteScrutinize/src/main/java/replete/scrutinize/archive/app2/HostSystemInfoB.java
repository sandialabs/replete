package replete.scrutinize.archive.app2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import replete.io.FileUtil;
import replete.pstate2.PersistentStateLoadException;
import replete.pstate2.PersistentStateManager;
import replete.pstate2.XmlFileManager;
import replete.scrutinize.archive.app2.HsiInspector.Group;
import replete.scrutinize.archive.app2.HsiInspector.Groups;
import replete.scrutinize.archive.app2.images.ScrutinizeImageModel;
import replete.ui.fc.RFileChooser;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.list.EmptyMessageList;
import replete.ui.panels.GradientPanel;
import replete.ui.text.RLabel;
import replete.ui.text.RTextPane;
import replete.ui.windows.notifications.NotificationFrame;
import replete.ui.windows.notifications.msg.NotificationTask;
import replete.ui.worker.RWorker;
import replete.util.Application;
import replete.util.DateUtil;
import replete.util.User;


public class HostSystemInfoB extends NotificationFrame {
    private static final String leftImage = "arrow.png";
    private static Color clrTop1 = new Color(109, 133, 174);
    private static Color clrTop2 = new Color(155,176,213);
    private static Color clrDetail1 = new Color(192,192,255);
    private static Color clrDetail2 = new Color(255,192,192);

    private JPanel pnlDetail;
    private JLabel lblExtraInfo;
    private JList lstGroups;
    private JButton btnSaveAs;

    private Groups groups;

    public HostSystemInfoB() {
        super("Host System Information");
        setIcon(ScrutinizeImageModel.SCRUTINIZE_ICON);

        final JLabel lblImage = new JLabel(ImageLib.get(ScrutinizeImageModel.LOOK_LEFT_ARROW));

        lstGroups = new JList();
        lstGroups.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstGroups.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                Group group = (Group) lstGroups.getSelectedValue();
                if(group == null) {
                    pnlDetail.removeAll();
                    pnlDetail.add(lblImage);
                    pnlDetail.updateUI();
                    return;
                }

                pnlDetail.removeAll();
                pnlDetail.add(createGroupDetailPanel(group), BorderLayout.CENTER);
                pnlDetail.updateUI();
            }
        });

        JButton btnLoadLocal = Lay.btn("(&Re)Load Local", ScrutinizeImageModel.LOCAL);
        btnLoadLocal.addActionListener(e -> reloadLocal());

        JButton btnLoadOther = Lay.btn("&Load Other...", CommonConcepts.OPEN,
            (ActionListener) e -> {
                RFileChooser chooser = RFileChooser.getChooser("Open Info Snapshot...");
                if(chooser.showOpen(this)) {
                    groups = (Groups) FileUtil.getObjectContent(chooser.getSelectedFile());
                    update("Loaded " + chooser.getSelectedFile());
                }
            }
        );

        btnSaveAs = Lay.btn("&Save Info As...", CommonConcepts.SAVE_AS,
            (ActionListener) e -> {
                RFileChooser chooser = RFileChooser.getChooser("Save Info As...");
                if(chooser.showSave(this)) {
                    FileUtil.writeObjectContent(groups, chooser.getSelectedFile());
                }
            }
        );

        lblExtraInfo = new JLabel();
        lblExtraInfo.setForeground(Color.white);

        Color brd = new Color(28,56,155);
        JPanel pnlHeader = new GradientPanel(clrTop1, clrTop2);
        Lay.FLtg(pnlHeader, "L",
            btnLoadLocal,
            btnLoadOther,
            Lay.hn(btnSaveAs, "enabled=false"),
            "augb=mb(2b,"+ Lay.clr(brd) + ")"
        );
        JPanel pnlList = new GradientPanel();
        Lay.BLtg(pnlList,
            Lay.sp(lstGroups, "pref=[200,100]"),
            "augb=eb(10tlb),opaque=false"
        );
        pnlDetail = new GradientPanel();
        Lay.BLtg(pnlDetail,
            "C", Lay.GBL(lblImage, "opaque=false"),
            "eb=10"
         );

        Lay.BLtg(this,
            "N", pnlHeader,
            "W", pnlList,
            "C", pnlDetail
        );

        getStatusBar().setGradientEnabled(true);
        getStatusBar().setColors(clrTop1, clrTop2);
        Lay.hn(getStatusBar(), "eb=5brt10l,augb=mb(2t," + Lay.clr(brd) + ")");
        setShowStatusBar(true);

        lstGroups.setSelectedIndex(0);

        AppState state = AppState.getState();
        if(state.getMainFrameSize() != null) {
            setSize(state.getMainFrameSize());
        } else {
            setSize(900,600);
        }
        if(state.getMainFrameLoc() != null) {
            setLocation(state.getMainFrameLoc());
        } else {
            setLocationRelativeTo(null);
        }
        setExtendedState(state.getMainFrameExtState());
    }
    public void reloadLocal() {
        RWorker<Void, Void> worker = new RWorker<Void, Void>() {
            @Override
            protected Void background(Void gathered) throws Exception {
                groups = HsiInspector.inspectSystem();
                return null;
            }
            @Override
            protected void complete() {
                // TODO: Would be nice if can set this onto the NotificationTask
                update("Time to compute local information: " +
                    DateUtil.toElapsedString(groups.getComputeTime()));
            }
        };
        getNotificationModel().getTasks().add(
            new NotificationTask()
                .setAction(worker)
                .setTitle("Update Info")
                .setUseWaitCursor(true));
        worker.execute();
    }

    private void update(String nm) {
        lstGroups.setListData(groups.values().toArray());
        lblExtraInfo.setText(nm);
        btnSaveAs.setEnabled(true);
    }

    private JComponent createGroupDetailPanel(Group group) {
        final JPanel pnlGroupDetail = Lay.BxL("Y");
        boolean flip = false;
        for(Property prop : group.values()) {
            String bg = (flip ? Lay.clr(clrDetail1) : Lay.clr(clrDetail2));

            JComponent cmp;
            if(prop.getType() == ValueType.VALUE_LIST || prop.getType() == ValueType.PATH_LIST) {
                JList lst = new EmptyMessageList(prop.asList().toArray(), "None");
                cmp = Lay.BL(Lay.sp(lst), "opaque=false,augb=eb(10)");
                Lay.hn(cmp, "minH=500");
//                cmp = new JLabel("test");
//                cmp = Lay.sp(new JList(new Object[]{"hey", "what","are","you", "doing"}), "minH=120");
            } else if(prop.getType() == ValueType.KEY_VALUE_MAP) {
                JTable tbl = new JTable(new DefaultTableModel(prop.as2DArray(), new String[] {"Key", "Value"}));
                cmp = Lay.BL(Lay.sp(tbl), "opaque=false,augb=eb(10)");
                Lay.hn(cmp, "minH=300");
            } else {
                JTextField txtValue = new JTextField(prop.getPrettyValStr());
//                JLabel txtValue = new HLabel("hello<br>there");
                cmp = Lay.eb(txtValue, "10lr", "editable=false,bg=" + bg);
            }

//            final JTextField txtCode = new JTextField("Code: " + prop.getCode());
            final RTextPane txtRaw = new RTextPane("Raw: " + prop.getRawValStr());
            JComponent cmp2 = Lay.BL(Lay.sp(txtRaw, "eb=0"), "opaque=false,augb=eb(10l20r)");
//            Lay.hn(cmp2, "minH=100");
//            Lay.hn(cmp2, "maxH=300");
            final JPanel pnlProperty = /*Lay.BL(
                "C", */Lay.BxL("Y",
                    Lay.hn(new RLabel("" +
                            "<html><u><font color='#0000FF'>" +
                                    prop.getName().replaceAll("<", "&lt;") +
                                    "</font></u></html>"), "alignx=0.0"),
                    Lay.hn(cmp, "alignx=0.0"),
//                    Lay.eb(txtCode, "10l", "visible=false,alignx=0.0,editable=false,bg=" + bg),
//                    Lay.hn(cmp2, "alignx=0.0"),
//                    Lay.lb("Raw:", )
                    Lay.hn(cmp2, "alignx=0"),
//                    Lay.p(cmp2, "opaque=false,augb=eb(10l),alignx=0"),
//                    Lay.hn(cmp2, "10l", "visible=false,alignx=0.0,editable=true,bxg=" + bg),
//                    Lay.hn(cmp2, "10l", "visible=false,alignx=0.0,editable=true,bxg=" + bg),
                    Box.createVerticalGlue(),
                    "opaque=false"
                //),
                //"max=[2147483647,20],eb=10,augb=mb(1b,black),bg=" + bg
            );

            pnlProperty.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    if(!pnlProperty.getVisibleRect().contains(e.getPoint())) {
//                        Lay.hn(txtCode, "visible=false");
//                        Lay.hn(txtRaw, "visible=false");
//                        pnlProperty.updateUI();
                    }
                }
                @Override
                public void mouseEntered(MouseEvent e) {
//                    Lay.hn(txtCode, "visible=true");
//                    Lay.hn(txtRaw, "visible=true");
//                    pnlProperty.updateUI();
                }
            });
            pnlGroupDetail.add(pnlProperty);
            flip = !flip;
        }
        pnlGroupDetail.add(Box.createVerticalGlue());

        final JScrollPane p = new JScrollPane(pnlGroupDetail,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        p.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
//                Lay.hn(pnlGroupDetail, "dimW=1000");// + (p.getWidth() - 10));
                Lay.hn(pnlGroupDetail, "dimW=" + (p.getWidth() - 10));
            }
        });

        return p;
    }
    private String insertSpaces(String s) {
        for(int x = s.length() - 100; x >= 0; x-=100) {
            s = s.substring(0, x) + " " + s.substring(x);
        }
        return s;
    }

    /* PREVIOUS VERSION - KEEPING HERE JUST IN CASE THE ABOVE CODE HAS
     * COMPLETE BROKEN STOOF.

    private JScrollPane createGroupDetailPanel(Group group) {
        JPanel pnlGroupDetail = Lay.BxL("Y");
        boolean flip = false;
        for(Property prop : group.values()) {
            String bg = (flip ? Lay.clr(clrDetail1) : Lay.clr(clrDetail2));

            JComponent cmp;
            if(prop.getType() == ValueType.VALUE_LIST || prop.getType() == ValueType.PATH_LIST) {
                JList lst = new EmptyMessageList(prop.asList().toArray(), "None");
                cmp = Lay.BL(Lay.sp((JComponent) Lay.hn(lst)), "opaque=false,augb=eb(10)");
                System.out.println(lst.getMaximumSize());
            } else if(prop.getType() == ValueType.KEY_VALUE_MAP) {
                JTable tbl = new JTable(new DefaultTableModel(prop.as2DArray(), new String[] {"Key", "Value"}));
                cmp = Lay.BL(Lay.sp((JComponent) Lay.hn(tbl)), "opaque=false,augb=eb(10)");
                System.out.println("table: " + tbl.getMaximumSize());
            } else {
                JTextField txtValue = new JTextField(prop.getPrettyValStr());
                cmp = Lay.eb(txtValue, "10lr", "editable=false,bg=" + bg);
            }

            final JTextField txtCode = new JTextField("Code: " + prop.getCode());
            final JTextArea txtRaw = new JTextArea("Raw: " + prop.getRawValStr());
            final JPanel pnlProperty = Lay.BL(
                "C", Lay.BxL("Y",
                    Lay.hn(new DebugLabel("<html><u><font color='#0000FF'>" + prop.getName().replaceAll("<", "&lt;") + "</font></u></html>"), "alignx=0.0"),
                    Lay.hn(cmp, "alignx=0.0"),
                    Lay.eb(txtCode, "10l", "visible=false,alignx=0.0,editable=false,bg=" + bg),
                    Lay.eb(txtRaw, "10l", "visible=false,alignx=0.0,editable=false,bg=" + bg),
                    "opaque=false"
                ),
                "max=[2147483647,20],eb=10,augb=mb(1b,black),bg=" + bg
            );

            pnlProperty.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    if(!pnlProperty.getVisibleRect().contains(e.getPoint())) {
                        Lay.hn(txtCode, "visible=false");
                        Lay.hn(txtRaw, "visible=false");
                        pnlProperty.updateUI();
                    }
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    Lay.hn(txtCode, "visible=true");
                    Lay.hn(txtRaw, "visible=true");
                    pnlProperty.updateUI();
                }
            });
            pnlGroupDetail.add(pnlProperty);
            flip = !flip;
        }
        pnlGroupDetail.add(Box.createVerticalGlue());

        return Lay.sp(pnlGroupDetail);
    }

    /**/

    public static void main(String[] args) {
        Application.setName("Host System Information");
        Application.setVersion("0.1");


        AppState state = null;
        try {
            state = (AppState) stateMgr.load();
        } catch(PersistentStateLoadException e) {
            e.printStackTrace();
        }
        if(state == null) {
            state = new AppState();
        }
        AppState.setState(state);

        frame = new HostSystemInfoB();
        frame.setVisible(true);
        frame.reloadLocal();

        frame.addClosingListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                saveStateX();
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                saveStateX();
            }
        });
    }

    private void saveState() {
        AppState state = AppState.getState();
        state.setMainFrameSize(getSize());
        state.setMainFrameLoc(getLocation());
        state.setMainFrameExtState(getExtendedState());
    }

    private static void saveStateX() {
        frame.saveState();
        AppState state = AppState.getState();
//        state.setLafClassName(LafManager.getCurrentLaf().getCls());
//        state.setLafThemeName(LafManager.getCurrentLaf().getCurTheme());
        stateMgr.save(AppState.getState());
    }

    public static PersistentStateManager stateMgr = new XmlFileManager(new File(User.getHome(), ".hsi"));
    private static HostSystemInfoB frame;

}
