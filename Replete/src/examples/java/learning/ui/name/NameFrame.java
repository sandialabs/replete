package learning.ui.name;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import replete.threads.ThreadUtil;
import replete.ui.button.RButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.table.DefaultUiHintedTableModel;
import replete.ui.table.RTable;
import replete.ui.table.RTableModel;
import replete.ui.text.RLabel;
import replete.ui.windows.Dialogs;
import replete.ui.windows.common.RWindowClosingEvent;
import replete.ui.windows.common.RWindowClosingListener;
import replete.ui.windows.notifications.NotificationClickAction;
import replete.ui.windows.notifications.NotificationFrame;
import replete.ui.windows.notifications.msg.NotificationCommon;
import replete.ui.windows.notifications.msg.NotificationTask;
import replete.ui.worker.RWorker;

public class NameFrame extends NotificationFrame {

//    private int iAge;
//    private int mAge;
//    private int _Age;

    public NameFrame(/*int pAge*/) {
        super("Elliot's Learning Frame");

//        initClassicSwingGui(); // Pure Swing
//        initRComponentGui();   // Shows come changes to RButton, Table
        initLayGui();
    }

    public void initLayGuiSimple() {
        Lay.BLtg(this,
            "N", Lay.lb("asdfasf"),
            "W", Lay.sp(Lay.lst()),
            "C", Lay.sp(Lay.tbl("bg=yellow")),
            "S", Lay.FL("R",
                Lay.btn("&Close", (ActionListener) e -> close()),
                "bg=[100,22,44]"
            ),
            "size=[600,600],center"
        );
    }

    private void initLayGui() {

        JButton btnGo, btnClose;
        JPanel pnlButtons = Lay.FL("R",
            btnGo    = Lay.btn("&Go",    ImageLib.get(CommonConcepts.PLAY),  (ActionListener) e -> launchMyTask()),
            btnClose = Lay.btn("&Close", ImageLib.get(CommonConcepts.CLOSE), (ActionListener) e -> close())
        );

        Object[] names = new Object[] {"Joey", "Barthalamew Jesus Romanus the IIIrd", "Tony", "Danza"};

        Lay.BLtg(this,
            "N", Lay.FL("L",
                Lay.lb(
                    "<html>Hi there <u>this is</u> a test window!</html>",
                    "fg=white", new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            throw new RuntimeException();
                        }
                    }
                ),
                "bg=100,mb=[1,black]"
            ),
            "W", Lay.sp(Lay.lst((Object) names), "prefw=100"),
            "C", Lay.sp(Lay.tbl(new EventBetterProductTableModel())),
            "size=600,center"
        );

//        Lay.GBLtg(this,
//            Lay.btn("test button!"),
//            "size=600,center"
//        );

        btnGo.setPreferredSize(btnClose.getPreferredSize());

        // Any component can use these hints:
        //    "min=[w,h],pref=[w,h],max=[w,h],dim=[w,h],minw=w,minh=h,prefw=w,prefh=h,maxw=w,maxh=h,dimw=w,dimh=h"

        setShowStatusBar(true);
        getStatusBar().setRightComponent(pnlButtons);

        addAttemptToCloseListener(e -> {
            if(!Dialogs.showConfirm(NameFrame.this, "Are you sure?")) {
                e.cancelClose();
            }
        });
    }

    private void launchMyTask() {
        RWorker<Void, Void> worker = new RWorker<Void, Void>() {
            @Override
            protected Void background(Void gathered) throws Exception {
                ThreadUtil.sleep(5000);
                return null;
            }
            @Override
            protected void complete() {
                try {
                    getResult();
                    // Code you execute in good case
                } catch(Exception e) {
                    Dialogs.showDetails(
                        NameFrame.this, "An error occurred processing this task.",
                        "Error", e);
                    // Code you execute in base case
                }
                // Code you execute in both cases
            }
        };

        NotificationTask task = new NotificationTask()
            .setTitle("My Task")
            .setUseWaitCursor(true)
            .setClickAction(new NotificationClickAction() {
                @Override
                public void clicked(NotificationCommon notif) {
                    NotificationTask task = (NotificationTask) notif;
                    Dialogs.showMessage(NameFrame.this, "You clicked task for " + task.getTitle());
                }
            })
            .setAction(worker)
        ;
        getNotificationModel().getTasks().add(task);

        worker.execute();
    }

    private void initRComponentGui() {
        JPanel pnlButtons = new JPanel();
        pnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT/*, 0, 0*/));

        JButton btnGo    = new RButton("&Go",    ImageLib.get(CommonConcepts.PLAY));
        JButton btnClose = new RButton("&Close", ImageLib.get(CommonConcepts.CLOSE));

        btnGo.setPreferredSize(btnClose.getPreferredSize());

        btnGo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RWorker<Void, Void> worker = new RWorker<Void, Void>() {
                    @Override
                    protected Void background(Void gathered) throws Exception {
                        ThreadUtil.sleep(5000);
                        return null;
                    }
                    @Override
                    protected void complete() {
                        try {
                            getResult();
                            // Code you execute in good case
                        } catch(Exception e) {
                            Dialogs.showDetails(
                                NameFrame.this, "An error occurred processing this task.",
                                "Error", e);
                            // Code you execute in base case
                        }
                        // Code you execute in both cases
                    }
                };

                NotificationTask task = new NotificationTask()
                    .setTitle("My Task")
                    .setUseWaitCursor(true)
                    .setClickAction(new NotificationClickAction() {
                        @Override
                        public void clicked(NotificationCommon notif) {
                            NotificationTask task = (NotificationTask) notif;
                            Dialogs.showMessage(NameFrame.this, "You clicked task for " + task.getTitle());
                        }
                    })
                    .setAction(worker)
                ;
                getNotificationModel().getTasks().add(task);

                worker.execute();
            }
        });

        pnlButtons.add(btnGo);
        pnlButtons.add(btnClose);

        JPanel pnlHeader = new RPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel lblTitle = new RLabel("<html>Hi there <u>this is</u> a test window!</html>");
        lblTitle.setForeground(Color.white);
//        lblTitle.setHorizontalAlignment(SwingConstants.LEFT);    // Correct, assuming the width of the label is larger than width of text.
//        lblTitle.setAlignmentX(0F);                              // Is only used for BoxLayout
//        lblTitle.setHorizontalTextPosition(SwingConstants.LEFT); // Is the position of the icon in relation to the text

        pnlHeader.setBackground(new Color(100, 100, 100));
        pnlHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
        pnlHeader.add(lblTitle);

        lblTitle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                throw new RuntimeException();
            }
        });

        addAttemptToCloseListener(new RWindowClosingListener() {
            public void stateChanged(RWindowClosingEvent e) {
                if(!Dialogs.showConfirm(NameFrame.this, "Are you sure?")) {
                    e.cancelClose();
                }
            }
        });

        btnClose.addActionListener(e -> close());

        JList lstNames = new JList(new Object[] {"Joey", "Barthalamew Jesus Romanus the IIIrd", "Tony", "Danza"});
        JScrollPane scrNames = new JScrollPane(lstNames);
        scrNames.setPreferredSize(new Dimension(100, 1));   // 1 (height) is ignored

        RTable tblProducts = new RTable(new ProductTableModel());
        JScrollPane scrProducts = new JScrollPane(tblProducts);

        tblProducts.setColumnWidths(
            new int[][] {
                { 30,  30, 100},
                {200, 200, 600},
                { -1,  -1,  -1}
            }
        );

//        tblProducts.getColumnModel().getColumn(0).setMinWidth(30);
//        tblProducts.getColumnModel().getColumn(0).setPreferredWidth(30);
//        tblProducts.getColumnModel().getColumn(0).setMaxWidth(100);
//
//        tblProducts.getColumnModel().getColumn(1).setMinWidth(200);
//        tblProducts.getColumnModel().getColumn(1).setPreferredWidth(200);
//        tblProducts.getColumnModel().getColumn(1).setMaxWidth(600);

        DefaultTableCellRenderer myRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {

                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if(column == 0) {
                    lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                } else if(column == 1) {
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                }

                Color CLR_INVALID = Lay.clr("255,205,205");

                if(row == 2) {
                    lbl.setBackground(CLR_INVALID);
                } else {
                    lbl.setBackground(Color.white);
                }

                return lbl;
            }
        };

        tblProducts.setDefaultRenderer(Object.class, myRenderer);

//        Container cp = getContentPane();
        setLayout(new BorderLayout());

        add(pnlHeader,   BorderLayout.NORTH);
        add(scrNames,    BorderLayout.WEST);
        add(scrProducts, BorderLayout.CENTER);
//        add(pnlButtons,  BorderLayout.SOUTH);

        setShowStatusBar(true);

        getStatusBar().setRightComponent(pnlButtons);

        setSize(600, 600);
        setLocationRelativeTo(null);
    }


    private void initClassicSwingGui() {
        JPanel pnlButtons = new JPanel();
        pnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT/*, 0, 0*/));

        JButton btnGo = new JButton ("Go");
        JButton btnClose = new JButton("Close");

        btnGo.setMnemonic('G');
        btnClose.setMnemonic('C');

        btnGo.setIcon(ImageLib.get(CommonConcepts.PLAY));
        btnClose.setIcon(ImageLib.get(CommonConcepts.CLOSE));

        btnGo.setPreferredSize(btnClose.getPreferredSize());

        btnGo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RWorker<Void, Void> worker = new RWorker<Void, Void>() {
                    @Override
                    protected Void background(Void gathered) throws Exception {
                        ThreadUtil.sleep(5000);
                        return null;
                    }
                    @Override
                    protected void complete() {
                        try {
                            getResult();
                            // Code you execute in good case
                        } catch(Exception e) {
                            Dialogs.showDetails(
                                NameFrame.this, "An error occurred processing this task.",
                                "Error", e);
                            // Code you execute in base case
                        }
                        // Code you execute in both cases
                    }
                };

                NotificationTask task = new NotificationTask()
                    .setTitle("My Task")
                    .setUseWaitCursor(true)
                    .setClickAction(new NotificationClickAction() {
                        @Override
                        public void clicked(NotificationCommon notif) {
                            NotificationTask task = (NotificationTask) notif;
                            Dialogs.showMessage(NameFrame.this, "You clicked task for " + task.getTitle());
                        }
                    })
                    .setAction(worker)
                ;
                getNotificationModel().getTasks().add(task);

                worker.execute();
            }
        });

        pnlButtons.add(btnGo);
        pnlButtons.add(btnClose);

        JPanel pnlHeader = new RPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel lblTitle = new RLabel("<html>Hi there <u>this is</u> a test window!</html>");
        lblTitle.setForeground(Color.white);
        lblTitle.setHorizontalAlignment(SwingConstants.LEFT);    // Correct, assuming the width of the label is larger than width of text.
        lblTitle.setAlignmentX(0F);                              // Is only used for BoxLayout
        lblTitle.setHorizontalTextPosition(SwingConstants.LEFT); // Is the position of the icon in relation to the text

        pnlHeader.setBackground(new Color(100, 100, 100));
        pnlHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
        pnlHeader.add(lblTitle);

        lblTitle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                throw new RuntimeException();
            }
        });

        addAttemptToCloseListener(new RWindowClosingListener() {
            public void stateChanged(RWindowClosingEvent e) {
                if(!Dialogs.showConfirm(NameFrame.this, "Are you sure?")) {
                    e.cancelClose();
                }
            }
        });

        btnClose.addActionListener(e -> close());

        JList lstNames = new JList(new Object[] {"Joey", "Barthalamew Jesus Romanus the IIIrd", "Tony", "Danza"});
        JScrollPane scrNames = new JScrollPane(lstNames);
        scrNames.setPreferredSize(new Dimension(100, 1));   // 1 (height) is ignored

        JTable tblProducts = new JTable(new ProductTableModel());
        JScrollPane scrProducts = new JScrollPane(tblProducts);

        tblProducts.getColumnModel().getColumn(0).setMinWidth(30);
        tblProducts.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblProducts.getColumnModel().getColumn(0).setMaxWidth(100);

        tblProducts.getColumnModel().getColumn(1).setMinWidth(200);
        tblProducts.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblProducts.getColumnModel().getColumn(1).setMaxWidth(600);

        DefaultTableCellRenderer myRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {

                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if(column == 0) {
                    lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                } else if(column == 1) {
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                }

                Color CLR_INVALID = Lay.clr("255,205,205");

                if(row == 2) {
                    lbl.setBackground(CLR_INVALID);
                } else {
                    lbl.setBackground(Color.white);
                }

                return lbl;
            }
        };

        tblProducts.setDefaultRenderer(Object.class, myRenderer);

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        cp.add(pnlHeader,   BorderLayout.NORTH);
        cp.add(scrNames,    BorderLayout.WEST);
        cp.add(scrProducts, BorderLayout.CENTER);
//        cp.add(pnlButtons,  BorderLayout.SOUTH);

        setShowStatusBar(true);

        getStatusBar().setRightComponent(pnlButtons);

        setSize(600, 600);
        setLocationRelativeTo(null);
    }

    class ProductTableModel extends DefaultTableModel {

        @Override
        public int getRowCount() {
            return 3;
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch(columnIndex) {
                case 0: return "#";
                case 1: return "Name";
                case 2: return "Description";
            }
            return null;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch(columnIndex) {
                case 0: return String.class;
                case 1: return String.class;
                case 2: return String.class;
            }
            return null;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch(columnIndex) {
                case 0: return rowIndex;
                case 1: return "Name" + rowIndex;
                case 2: return "Description" + rowIndex;
            }
            return null;
        }
    }

    private class BetterProductTableModel extends RTableModel {
        @Override
        protected void init() {
            addColumn("#",           String.class, new int[] { 30,  30, 100});
            addColumn("Name",        String.class, new int[] {200, 200, 600});
            addColumn("Description", String.class, new int[] { -1,  -1,  -1});
        }
        @Override
        public int getRowCount() {
            return 3;
        }
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch(columnIndex) {
                case 0: return rowIndex;
                case 1: return "Name" + rowIndex;
                case 2: return "Description" + rowIndex;
            }
            return null;
        }
    }

    private class EventBetterProductTableModel extends DefaultUiHintedTableModel {
        private final Color CLR_INVALID = Lay.clr("255,205,205");

        @Override
        protected void init() {
            addColumn("#",           String.class, new int[] { 30,  30, 100});
            addColumn("Name",        String.class, new int[] {200, 200, 600});
            addColumn("Description", String.class, new int[] { -1,  -1,  -1});
        }
        @Override
        public int getRowCount() {
            return 3;
        }
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch(columnIndex) {
                case 0: return rowIndex;
                case 1: return "Name" + rowIndex;
                case 2: return "Description" + rowIndex;
            }
            return null;
        }

        @Override
        public int getAlignment(int row, int col) {
            if(col == 0) {
                return SwingConstants.RIGHT;
            } else if(col == 1) {
                return SwingConstants.CENTER;
            }
            return super.getAlignment(row, col);
        }

        @Override
        public Color getBackgroundColor(int row, int col) {
            if(row == 2) {
                return CLR_INVALID;
            }
            return super.getBackgroundColor(row, col);
        }
    }
}
