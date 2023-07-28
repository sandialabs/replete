package replete.ui.fc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import replete.collections.ListUtil;
import replete.ui.lay.Lay;

public class RecentListHelper<T> {

    public static <T> void update(RecentListContext<T> context, JPanel pnlContainer) {

        List<T> list = context.getRecentList();

        // If nothing to monitor, do nothing.
        if(pnlContainer == null || ListUtil.isBlank(list)) {
            return;
        }

        LayoutManager layout = pnlContainer.getLayout();
        if(!(layout instanceof BorderLayout)) {
            return;
        }

        BorderLayout borderLayout = (BorderLayout) layout;

        Component cmp = borderLayout.getLayoutComponent(BorderLayout.SOUTH);
        if(cmp != null) {
            pnlContainer.remove(cmp);
        }

        // If there are recent objects, rebuild the recent panel.
        JPanel pnlList;
        JPanel pnlRecent = Lay.BL(
            "N", Lay.FL("L",
                Lay.lb("Recent " + context.getLinkNamePlural(), "eb=5l,fg=white,font=Verdana"),
                "bg=100,xgradient,xgradclr1=006B0A,xgradclr2=00870B,vgap=2,hgap=2,dimh=20"
            ),
            "C", Lay.sp(pnlList = Lay.BxL("Y", "eb=4tlb10r,bg=white,a=EDFFF3"), "dimh=100"),
            "mb=[1t,black]"
        );

        int i = 0;
        for(T object : list) {
            i++;
            boolean allowed = context.isLinkClickable(object);
            String style = allowed ? "fg=blue,cursor=hand,eb=3lr" : "eb=3lr";
            final JLabel lblFile;
            pnlList.add(
                Lay.BL(
                    "W", Lay.BL("N", Lay.lb(i + ". ", "prefw=20,right")),
                    "C", Lay.BL(
                        "N", lblFile = Lay.lb(
                            "<html>" + object + "</html>",
                            style
                        )
                        // No center
                    ),
                    "dimh=20"
                )
            );
            if(allowed) {
                lblFile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        context.linkClicked(object);
                    }
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        lblFile.setBackground(Lay.clr("FFF06D"));
                        lblFile.setOpaque(true);
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        lblFile.setBackground(Color.blue);
                        lblFile.setOpaque(false);
                    }
                });
            }
        }

        pnlList.add(Box.createVerticalGlue());

        // TODO: How to get the scrollable area to be as small
        // as possible until a maximum is reached.
        pnlContainer.add(pnlRecent, BorderLayout.SOUTH);

        Lay.hn(pnlList, "chtransp");

        pnlContainer.updateUI();
    }
}
