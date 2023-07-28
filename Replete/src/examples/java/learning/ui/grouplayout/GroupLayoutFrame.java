package learning.ui.grouplayout;
import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import replete.ui.button.RButton;
import replete.ui.lay.Lay;
import replete.ui.windows.notifications.NotificationFrame;

public class GroupLayoutFrame extends NotificationFrame {

    public GroupLayoutFrame() {
        super("Group Layout Frame");
        //initClassicSwingGui();
        initLay();
    }

    private void initLay() {
        RButton btn1 = Lay.btn("Component 1");
        RButton btn2 = Lay.btn("Component 2");
        RButton btn3 = Lay.btn("Component 3");
        RButton btn4 = Lay.btn("Component 4");

        Lay.BLtg(this,
            "C", Lay.GPL(
                "H", Lay.SG(
                    btn1,
                    btn2,
                    Lay.PG(
                        btn3,
                        btn4,
                        "leading"
                    )
                ),
                "V", Lay.SG(
                    Lay.PG(
                        btn1,
                        btn2,
                        btn3,
                        "baseline"
                    ),
                    btn4
                ),
                "agaps=true, acgaps=true"
            ),
            "size=600,center"
        );
    }

    private void initClassicSwingGui() {
        JPanel pnlButtons = new JPanel();

        GroupLayout layout = new GroupLayout(pnlButtons);
        pnlButtons.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JButton btnC1 = new JButton ("Component 1");
        btnC1.setPreferredSize(getMaximumSize());
        JButton btnC2 = new JButton ("Component 2");
        btnC2.setPreferredSize(getMaximumSize());
        JButton btnC3 = new JButton ("Component 3");
        btnC3.setPreferredSize(getMaximumSize());
        JButton btnC4 = new JButton ("Component 4");
        btnC4.setPreferredSize(getMaximumSize());

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                    .addComponent(btnC1)
                    .addComponent(btnC2)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(btnC3)
                            .addComponent(btnC4))
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(btnC1)
                            .addComponent(btnC2)
                            .addComponent(btnC3))
                    .addComponent(btnC4));


        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        cp.add(pnlButtons);

        setShowStatusBar(false);

        setSize(600, 600);
        setLocationRelativeTo(null);
    }
}
