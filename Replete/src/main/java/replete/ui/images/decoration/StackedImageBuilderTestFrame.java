package replete.ui.images.decoration;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;

import replete.ui.DrawUtil;
import replete.ui.button.RButton;
import replete.ui.button.RCheckBox;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.text.DocumentChangeListener;
import replete.ui.text.RTextField;
import replete.ui.windows.Dialogs;
import replete.ui.windows.escape.EscapeFrame;

public class StackedImageBuilderTestFrame extends EscapeFrame {


    ////////////
    // FIELDS //
    ////////////

    // Controls
    private RCheckBox          chkDrawGrid;
    private RCheckBox          chkOutline;
    private RButton            btnColorChooser;
    private RTextField         txtBaseXY;
    private RTextField         txtBaseWidth;
    private RTextField         txtBaseHeight;
    private ImageConfigPanel[] imagePanels;

    // Jpanel settings
    private Color   backgroundColor =  Color.white;

    // Composite Base
    private int     baseX      =  0;
    private int     baseY      =  0;
    private int     lastBaseX  =  0;
    private int     lastBaseY  =  0;
    private Integer baseWidth  = 16;
    private Integer baseHeight = 16;

    private final int numImages = 4;

    // Image
    private Image decoratedImage;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    //arbitrary hints like align go at the end, on their own line
    public StackedImageBuilderTestFrame() {
        super("Decorated Image Builder Demo", CommonConcepts._PLACEHOLDER);

        imagePanels = new ImageConfigPanel[numImages];
        JPanel pnlImageConfigs = Lay.BxL();
        for(int i = 0; i < numImages; i++) {
            imagePanels[i] = new ImageConfigPanel(i)
                .setScale(1.0)// / (i + 1))
                .setAlpha(255)/// (i + 1))
                .setAnchor(Anchor.values()[i % (Anchor.values().length - 1)])
                .setAlignmentX(0)
            ;
            pnlImageConfigs.add(imagePanels[i]);
        }
        JPanel pnlCenter;
        RButton btnBuild;
        Lay.BLtg(this,
            "N", Lay.BxL(
                Lay.FL("L",
                    Lay.lb("Base:"),
                    Lay.lb("Width"),  txtBaseWidth  = Lay.tx(baseWidth,  "cols=3,selectall"),
                    Lay.lb("Height"), txtBaseHeight = Lay.tx(baseHeight, "cols=3,selectall"),
                    "alignx=0"
                ),
                pnlImageConfigs,
                Lay.FL("L",
                    Lay.lb("Canvas:"),
                    Lay.lb("Draw X, Y"), txtBaseXY = Lay.tx(baseX + ", " + baseY, "cols=5"),
                    chkDrawGrid     = Lay.chk("Draw Grid"),
                    chkOutline      = Lay.chk("Outline", "selected"),
                    btnColorChooser = Lay.btn("Change Background..."),
                    "alignx=0"
                ),
                Lay.FL("L" ,
                    btnBuild = Lay.btn("Buil&d It", CommonConcepts.TOOLS),
                    Lay.btn("&Close", CommonConcepts.CANCEL, "closer"),
                    "alignx=0,bg=100"
                ),
                "aligny=1.0"
            ),
            "C", pnlCenter = new MyCustomDrawPanel(backgroundColor),
            "size=[700,381],center"
        );

        // LISTENERS
        chkDrawGrid.addItemListener(e -> pnlCenter.repaint());
        chkOutline.addItemListener(e -> pnlCenter.repaint());
        txtBaseXY.getDocument().addDocumentListener(new DocumentChangeListener() {
            @Override
            public void documentChanged(DocumentEvent e) {
                String text = txtBaseXY.getText();
                if(text != null && !text.equals("") && text.contains(",")) {
                    String[] split = text.replace(" ","").split(",");
                    if(split.length > 1) {
                        baseX = Integer.parseInt(split[0]);
                        baseY = Integer.parseInt(split[1]);
                    }
                }
            }
        });
        btnColorChooser.addActionListener(e -> {
            Color currentBackground = backgroundColor;
            Color picked = JColorChooser.showDialog(null, "JColorChooser Sample", currentBackground);
            if(picked != null) {
                backgroundColor = picked;
                pnlCenter.setBackground(backgroundColor);
                pnlCenter.repaint();
            }
        });
        btnBuild.addActionListener(e -> {
            try {
                Integer extractedWidth = extractInteger(txtBaseWidth);
                if(extractedWidth != null) {
                    baseWidth = extractedWidth;
                }
                Integer extractedHeight = extractInteger(txtBaseHeight);
                if(extractedHeight != null) {
                    baseHeight = extractedHeight;
                }
                lastBaseX = baseX;
                lastBaseY = baseY;
                StackedImageBuilder decorator = new StackedImageBuilder(baseWidth, baseHeight);
                for(int i = 0; i < imagePanels.length; i++) {
                    Image image = imagePanels[i].getImage();
                    if(image != null) {
                        int xOffset = imagePanels[i].getXPos();
                        int yOffset = imagePanels[i].getYPos();
                        Anchor anchor = imagePanels[i].getAnchor();
                        Double imageScale = imagePanels[i].getScale();
                        double imageAlpha = imagePanels[i].getAlpha();
                        decorator.addImage(
                            new ImagePlacement()
                                .setImage(image)
                                .setXOffset(xOffset)
                                .setYOffset(yOffset)
                                .setAnchor(anchor)
                                .setScale(imageScale)
                                .setAlpha(imageAlpha)
                        );
                    }
                }
                decoratedImage = decorator.create();
                pnlCenter.repaint();
            } catch(IllegalStateException ex) {
                Dialogs.showDetails(StackedImageBuilderTestFrame.this, "There was an error", "Error", ex);
            }
        });
    }
    private Integer extractInteger(RTextField txtBaseWidth2) {
        if(txtBaseWidth2.getText() != null && ! txtBaseWidth2.getText().isEmpty()) {
            return Integer.parseInt(txtBaseWidth2.getText());
        }
        return null;
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class MyCustomDrawPanel extends JPanel {

        public MyCustomDrawPanel(Color backgroundColor) {
            setBackground(backgroundColor);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if(chkDrawGrid.isSelected()) {
                DrawUtil.grid(g, getWidth(), getHeight());
            }

            if(decoratedImage != null) {
                if(chkOutline.isSelected()) {
                    g.setColor(new Color(200, 200, 200));
                    g.drawRect(
                        lastBaseX, lastBaseY,
                        decoratedImage.getWidth(null) - 1, decoratedImage.getHeight(null) - 1
                    );
                }
                g.drawImage(
                    decoratedImage,
                    lastBaseX, lastBaseY,
                    decoratedImage.getWidth(null), decoratedImage.getHeight(null),
                    null
                );
            }
        }
    }
}
