package replete.ui.label;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import replete.text.StringUtil;
import replete.ui.ColorLib;
import replete.ui.GuiUtil;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.windows.Dialogs;

public class DashboardDataPanel extends RPanel {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DashboardDataPanel() {
        this(null);
    }
    public DashboardDataPanel(DataDescriptor descriptor) {
        rebuild(descriptor);
    }

    public void setData(DataDescriptor descriptor) {
        rebuild(descriptor);
    }

    private void rebuild(DataDescriptor descriptor) {
        if(descriptor == null) {
            Lay.FLtg(this, "L");
            return;
        }

        JLabel lblMain = createMainLabel(descriptor);
        DatumSeparatorGenerator dataSeparatorCreator = descriptor.getDatumSeparatorCreator();
        removeAll();
        Lay.FLtg(this, "L", lblMain);
        for(int d = 0; d < descriptor.getData().size(); d++) {
            DatumDescriptor datum = descriptor.getData().get(d);
            BorderedLabel lblDatum = createDatumLabel(descriptor, datum);
            if(dataSeparatorCreator != null && d == 0) {
                Component cmpSeparator = dataSeparatorCreator.createSeparator(null, datum);
                if(cmpSeparator != null) {
                    add(cmpSeparator);
                }
            }
            add(lblDatum);
            if(dataSeparatorCreator != null) {
                DatumDescriptor datumNext =
                    (d == descriptor.getData().size() - 1) ? null :
                        descriptor.getData().get(d + 1);
                Component cmpSeparator = dataSeparatorCreator.createSeparator(datum, datumNext);
                if(cmpSeparator != null) {
                    add(cmpSeparator);
                }
            }
        }
        updateUI();
    }

    private JLabel createMainLabel(DataDescriptor dataDescriptor) {
        if(dataDescriptor.isSuppressLabel()) {
            return null;
        }
        String text = dataDescriptor.getText();
        if(!StringUtil.isBlank(text)) {
            text += StringUtil.cleanNull(dataDescriptor.getLabelSeparator());
        }
        JLabel lblMain = Lay.lb(text);
        if(dataDescriptor.getLabelWidth() != null) {
            Lay.hn(lblMain, "prefw=" + dataDescriptor.getLabelWidth());
        }
        if(dataDescriptor.getHoverText() != null) {
            lblMain.setToolTipText(dataDescriptor.getHoverText());
        }
        if(dataDescriptor.getIcon() != null) {
            lblMain.setIcon(dataDescriptor.getIcon());
        }
        if(dataDescriptor.getFont() != null) {
            lblMain.setFont(dataDescriptor.getFont());
        }
        if(dataDescriptor.getForegroundColor() != null) {
            lblMain.setForeground(dataDescriptor.getForegroundColor());
        }
        if(dataDescriptor.getBackgroundColor() != null) {
            setBackground(dataDescriptor.getBackgroundColor());
        }
        if(dataDescriptor.getBorderColor() != null) {
            String clr = Lay.clrhex(dataDescriptor.getBorderColor());
            Lay.hn(this, "mb=[1," + clr + "]");
        }
        if(dataDescriptor.getListener() != null) {
            Lay.hn(this, "cursor=hand");
            MouseListener listener = new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    dataDescriptor.getListener().mouseClicked(
                        new DatumMouseClickEvent(e, lblMain, dataDescriptor, null)
                    );
                }
            };
            lblMain.addMouseListener(listener);
            addMouseListener(listener);
        }
        return lblMain;
    }

    private BorderedLabel createDatumLabel(DataDescriptor dataDescriptor,
                                         DatumDescriptor datumDescriptor) {
        BorderedLabel lblDatum = new BorderedLabel(
            datumDescriptor.getText()
        );

        // Warning: Really ugly, overly verbose code below!  Avert thy eyes!

        if(datumDescriptor.getLabelWidth() != null) {
            Lay.hn(lblDatum, "prefw=" + datumDescriptor.getLabelWidth());
        } else if(dataDescriptor.getDefaultDatumDescriptor() != null) {
            if(dataDescriptor.getDefaultDatumDescriptor().getLabelWidth() != null) {
                Lay.hn(lblDatum, "prefw=" + dataDescriptor.getDefaultDatumDescriptor().getLabelWidth());
            }
        }

        if(datumDescriptor.getHoverText() != null) {
            lblDatum.setToolTipText(datumDescriptor.getHoverText());
        } else if(dataDescriptor.getDefaultDatumDescriptor() != null) {
            if(dataDescriptor.getDefaultDatumDescriptor().getHoverText() != null) {
                lblDatum.setToolTipText(dataDescriptor.getDefaultDatumDescriptor().getHoverText());
            }
        }

        if(datumDescriptor.getIcon() != null) {
            lblDatum.setIcon(datumDescriptor.getIcon());
        } else if(dataDescriptor.getDefaultDatumDescriptor() != null) {
            if(dataDescriptor.getDefaultDatumDescriptor().getIcon() != null) {
                lblDatum.setIcon(dataDescriptor.getDefaultDatumDescriptor().getIcon());
            }
        }

        if(datumDescriptor.getFont() != null) {
            lblDatum.setFont(datumDescriptor.getFont());
        } else if(dataDescriptor.getDefaultDatumDescriptor() != null) {
            if(dataDescriptor.getDefaultDatumDescriptor().getFont() != null) {
                lblDatum.setFont(dataDescriptor.getDefaultDatumDescriptor().getFont());
            }
        }

        if(datumDescriptor.getForegroundColor() != null) {
            lblDatum.setBubbleForegroundColor(datumDescriptor.getForegroundColor());
        } else if(dataDescriptor.getDefaultDatumDescriptor() != null) {
            if(dataDescriptor.getDefaultDatumDescriptor().getForegroundColor() != null) {
                lblDatum.setBubbleForegroundColor(dataDescriptor.getDefaultDatumDescriptor().getForegroundColor());
            }
        }

        if(datumDescriptor.getBackgroundColor() != null) {
            lblDatum.setBubbleBackgroundColor(datumDescriptor.getBackgroundColor());
        } else if(dataDescriptor.getDefaultDatumDescriptor() != null) {
            if(dataDescriptor.getDefaultDatumDescriptor().getBackgroundColor() != null) {
                lblDatum.setBubbleBackgroundColor(dataDescriptor.getDefaultDatumDescriptor().getBackgroundColor());
            }
        }

        Color borderColor = datumDescriptor.getBorderColor();
        if(borderColor == null && dataDescriptor.getDefaultDatumDescriptor() != null) {
            borderColor = dataDescriptor.getDefaultDatumDescriptor().getBorderColor();
        }
        if(borderColor != null) {
            lblDatum.setBubbleBorderColor(borderColor);
        }

        DatumClickListener listener = datumDescriptor.getListener();
        if(listener == null && dataDescriptor.getDefaultDatumDescriptor() != null) {
            listener = dataDescriptor.getDefaultDatumDescriptor().getListener();
        }
        if(listener != null) {
            Lay.hn(lblDatum, "cursor=hand");
            DatumClickListener finalListener = listener;
            MouseListener mouseListener = new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    finalListener.mouseClicked(
                        new DatumMouseClickEvent(e, lblDatum, dataDescriptor, datumDescriptor)
                    );
                }
            };
            lblDatum.addMouseListener(mouseListener);
        } else {
            Lay.hn(lblDatum, "cursor=default");
        }

        return lblDatum;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        DataDescriptor desc0 = new DataDescriptor()
            .setText("Some Data")
        ;
        DataDescriptor desc1 = new DataDescriptor()
            .setText("Some Data")
            .addDatum(
                new DatumDescriptor().setText("170")
            )
        ;
        DataDescriptor desc2 = new DataDescriptor()
            .setText("Some Data")
            .setHoverText("HI HOVER")
            .setLabelWidth(150)
            .setIcon(ImageLib.get(CommonConcepts._PLACEHOLDER))
            .setFont(new Font("Courier New", Font.ITALIC, 14))
            .setForegroundColor(Color.red)
            .setBackgroundColor(Color.cyan)
            .setBorderColor(Color.yellow)
            .setListener(e -> Dialogs.notImpl("pbl"))
            .addDatum(
                new DatumDescriptor().setText("170")
            )
            .addDatum(
                new DatumDescriptor()
                    .setText("+145")
                    .setHoverText("Good Stuff!")
                    .setIcon(ImageLib.get(CommonConcepts.ACCEPT))
                    .setForegroundColor(ColorLib.GREEN_STRONG)
                    .setBackgroundColor(ColorLib.GREEN_LIGHT)
                    .setBorderColor(ColorLib.GREEN_STRONG)
            )
            .addDatum(
                new DatumDescriptor()
                    .setText("-25")
                    .setHoverText("Bad Stuff!")
                    .setIcon(ImageLib.get(CommonConcepts.ERROR))
                    .setForegroundColor(ColorLib.RED_BRIGHT)
                    .setBackgroundColor(ColorLib.RED_LIGHT)
                    .setBorderColor(ColorLib.RED_BRIGHT)
            )
            .addDatum(
                new DatumDescriptor()
                    .setText("~5")
                    .setHoverText("Bad Stuff!")
                    .setForegroundColor(ColorLib.YELLOW_DARK)
                    .setBackgroundColor(ColorLib.YELLOW_LIGHT)
                    .setBorderColor(ColorLib.YELLOW_DARK)
                    .setListener(e -> {
                        Window win = GuiUtil.win(e.getLabel());
                        Dialogs.notImpl(win, "~5");
                    })
            )
        ;
        DataDescriptor desc3 = new DataDescriptor()
            .setText("Some Data")
            .setDatumSeparatorCreator((dl, dr) -> {
                return dl == null || dr == null ? null : Lay.lb(CommonConcepts._PLACEHOLDER);
            })
            .setDefaultDatumDescriptor(
                new DatumDescriptor()
                    .setHoverText("170")
                    .setForegroundColor(Color.red)
                    .setListener(e -> Dialogs.notImpl())
                    .setFont(new Font("Arial", Font.PLAIN, 28))
            )
            .addDatum(
                new DatumDescriptor().setText("270")
            )
            .addDatum(
                new DatumDescriptor().setText("270")
            )
            .addDatum(
                new DatumDescriptor().setText("270")
            )
            .addDatum(
                new DatumDescriptor().setText("270")
            )
        ;

        DataDescriptor[] dataDescriptors = new DataDescriptor[] {
            desc0, desc1, desc2, desc3
        };

        JPanel pnlList = Lay.BxL();
        for(DataDescriptor desc : dataDescriptors) {
            DashboardDataPanel pnl = new DashboardDataPanel(desc);
            Lay.hn(pnl, "maxh=1");
            pnlList.add(pnl);
        }
        pnlList.add(Box.createVerticalGlue());

        Lay.BLtg(Lay.fr("Test"),
            "C", pnlList,
            "size=600,visible"
        );
    }
}
