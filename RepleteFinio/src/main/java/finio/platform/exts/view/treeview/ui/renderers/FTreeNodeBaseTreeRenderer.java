package finio.platform.exts.view.treeview.ui.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicGraphicsUtils;

import finio.core.FConst;
import finio.core.FStrings;
import finio.core.FUtil;
import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.platform.exts.view.treeview.ui.FNode;
import finio.platform.exts.view.treeview.ui.FTree.EditedNodeInfo;
import finio.platform.exts.view.treeview.ui.FTreeConstant;
import finio.platform.exts.view.treeview.ui.nodes.NodeFTree;
import finio.platform.exts.view.treeview.ui.nodes.NodeNonTerminal;
import finio.platform.exts.view.treeview.ui.nodes.NodeTerminal;
import finio.ui.images.FinioImageModel;
import replete.text.StringUtil;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.tree.NodeBaseTreeRenderer;

// https://forums.oracle.com/forums/thread.jspa?messageID=10132870

public class FTreeNodeBaseTreeRenderer extends NodeBaseTreeRenderer {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    protected static final Color HOVER_BG_CLR       = Lay.clr("FFFD9B");
    protected static final Color WS_BG_CLR          = Lay.clr("FFCDCC");
    protected static final Color ANCHOR_BG_CLR      = Lay.clr("FFCDCC");
    protected static final Color ZOOMED_BG_CLR      = Lay.clr("FFCDCC");
    protected static final Color EDITING_BG_CLR     = Lay.clr("FFE399");
    protected static final String TERM_VAL_FG_COLOR = "3143A5";
    protected static final Color editBorder         = Lay.clr("DBA730");

    // Core

    protected FNode nCurrent;
    protected boolean hover;
    protected boolean ws;
    protected boolean anchor;
    protected boolean zoomed;
    protected boolean editing;
    protected Map<FNode, EditedNodeInfo> editedNodes;   // Shared with FTree
    protected boolean baseOpacity = true;               // Replacement of UI property Tree.rendererFillBackground
    protected boolean customOpacity = false;            // Overriding based on content


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FTreeNodeBaseTreeRenderer(Map<FNode, EditedNodeInfo> editedNodes) {
        this.editedNodes = editedNodes;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean sel,
            boolean expanded, boolean leaf, int row,
            boolean hasFocus1) {

        // Call base class for initial JLabel rendering.
        super.getTreeCellRendererComponent(
                tree, value, sel,
                expanded, leaf, row,
                hasFocus1);

        nCurrent = (FNode) value;

        if(nCurrent.getUserObject() instanceof NodeNonTerminal) {
            NodeNonTerminal uMap = nCurrent.getObject();
            if(uMap.isRealm()) {
                String keyString = StringUtil.cleanXmlCdata(uMap.renderKey());
                keyString = modifyDisplayString(uMap, keyString);
                String text = "<html><u>" + keyString + "</u>";
                if(uMap.getSimplified() != null) {
                    String valueString = StringUtil.cleanXmlCdata(uMap.getSimplified());
                    text += " = <b>" + valueString + "</b>";
                } else if(FUtil.isSemiTerminal(uMap.getV())) {
                    NonTerminal M = (NonTerminal) uMap.getV();
                    String SvalHtml = createValueStringWithHtml(M.get(FConst.SYS_VALUE_KEY));
                    text += " = " + SvalHtml;
                }
                setText(text + "</html>");

            } else {
                String keyString = StringUtil.cleanXmlCdata(uMap.renderKey());
                keyString = modifyDisplayString(uMap, keyString);

                if(FUtil.isSysMetaKey(uMap.getK())) {
                    keyString = "<i>" + keyString + "</i>";
                }

                Color keyColor = uMap.getForegroundColor();
                String text = "<html><b style='color: #" + keyColor + "'>" + keyString;
                if(uMap.getSimplified() != null) {
                    String valueString = StringUtil.cleanXmlCdata(uMap.getSimplified());
                    text += " = " + valueString;
                } else if(FUtil.isSemiTerminal(uMap.getV())) {
                    NonTerminal M = (NonTerminal) uMap.getV();
                    String SvalHtml = createValueStringWithHtml(M.get(FConst.SYS_VALUE_KEY));
                    text += " = " + SvalHtml;
                }

                // Set the final computed text of the label.
                setText(text + "</b></html>");
            }

        } else if(nCurrent.getUserObject() instanceof NodeTerminal) {
            NodeTerminal uTerm = (NodeTerminal) nCurrent.getUserObject();
            String SkeyHtml = StringUtil.cleanXmlCdata(uTerm.renderKey());
            if(FUtil.isSysMetaKey(uTerm.getK())) {
                SkeyHtml = "<i>" + SkeyHtml + "</i>";
            }

            Object V = uTerm.getV();
            String SvalHtml = createValueStringWithHtml(V);

            // Set the final computed text of the label.
            setText(
                "<html>" + SkeyHtml + " = " + SvalHtml + "</html>"
            );
        }

        // Save various aspects of this value for background and
        // border painting.  Might be null on initial tree creation.
        NodeFTree nATree = (NodeFTree) nCurrent.getUserObject();
        if(nATree != null) {
            hover   = nATree.isHover();
            ws      = nATree.isWorkingScope();
            anchor  = nATree.isAnchor();
            zoomed  = nATree.isZoomed();
            editing = nATree.isEditing();
        }

        if(editing) {
            setIcon(ImageLib.get(FinioImageModel.EDIT_ACTIVE));
        }

        setOpaque(false);
//System.out.println(getText());
        return this;
    }


    private String modifyDisplayString(NodeNonTerminal uMap, String Skey) {
        try {
            if(FUtil.isNonTerminal(uMap.getV())) {
                NonTerminal M = (NonTerminal) uMap.getV();
                if(FUtil.isString(M.getSysMeta(FConst.SYS_DISPLAY))) {
                    Skey = (String) M.getSysMeta(FConst.SYS_DISPLAY);
                    int i = Skey.indexOf(FConst.SYS_KV_SPECIAL_2 + "{");
                    while(i != -1) {
                        int j = Skey.indexOf('}', i);
                        if(j == -1) {
                            break;
                        }
                        String content = Skey.substring(i + 3, j);
                        KeyPath P = KeyPath.KP(content, "/");
                        Skey = Skey.substring(0, i) + M.getByPath(P) + Skey.substring(j + 1);
                        i = Skey.indexOf(FConst.SYS_KV_SPECIAL_2 + "{");
                    }
                    Skey = Skey.replaceAll(Pattern.quote(FConst.SYS_KV_SPECIAL_2), uMap.getK().toString());    // Matcher.quoteReplacement??
                }
            }
        } catch(Exception e) {
            return "(ERROR generating display string)";
        }
        return Skey;
    }

    private String createValueStringWithHtml(Object V) {
        String Svalue = FStrings.createMarkedUpValueString(V);
        return
            "<b style='color: #" + TERM_VAL_FG_COLOR + "'>" +
            Svalue +
            "</b>";
    }

    @Override
    protected Color choosePaintBackgroundColor() {
        Color bColor;
        //boolean paint = true;

        customOpacity = true;
        EditedNodeInfo info = editedNodes.get(nCurrent);
        if(info != null) {
            int countdown = info.countdown;
            bColor = new Color(162, 229, 187, 255 -
                (FTreeConstant.EDITED_COUNTDOWN - countdown) * 12);
//System.out.print("1");
        } else if(isDropCell) {
            bColor = UIManager.getColor("Tree.dropCellBackground");
            if(bColor == null) {
                bColor = getBackgroundSelectionColor();
            }
//System.out.print("2");
        } else if(hover) {
            bColor = HOVER_BG_CLR;
//System.out.print("3");

        } else if(ws) {
            bColor = WS_BG_CLR;
//System.out.print("4");

        } else if(selected) {
            bColor = getBackgroundSelectionColor();
//System.out.print("5");

        } else if(editing) {
            bColor = EDITING_BG_CLR;
//System.out.print("6");

        } else {
            bColor = getBackgroundNonSelectionColor();
            customOpacity = false;
            //paint = false;
//System.out.print("7");
        }

        if(bColor == null) {

            bColor = getBackground();
            //paint = false;
//System.out.print("8");
        }
//System.out.println(bColor + " " + nCurrent);
        //ReflectionUtil.set("fillBackground", this, paint);

        return bColor;
    }
    @Override
    protected int getLabelStart() {
        return 0;
    }
    @Override
    protected void paintBorder(Graphics g, Color bColor, int imageOffset) {

        EditedNodeInfo info = editedNodes.get(nCurrent);
        if(info != null) {
            int countdown = info.countdown;
            if (drawsFocusBorderAroundIcon) {
                imageOffset = 0;
            }
            else if (imageOffset == -1) {
                imageOffset = getLabelStart();
            }
            if(getComponentOrientation().isLeftToRight()) {
                paintEditedBorder(g, imageOffset, 0, getWidth() - imageOffset, getHeight(), countdown);
            } else {
                paintEditedBorder(g, 0, 0, getWidth() - imageOffset, getHeight(), countdown);
            }

        } else if(hasFocus) {
            super.paintBorder(g, bColor, imageOffset);

        } else if(ws) {
            if (drawsFocusBorderAroundIcon) {
                imageOffset = 0;
            }
            else if (imageOffset == -1) {
                imageOffset = getLabelStart();
            }
            if(getComponentOrientation().isLeftToRight()) {
                paintWSBorder(g, imageOffset, 0, getWidth() - imageOffset, getHeight());
            } else {
                paintWSBorder(g, 0, 0, getWidth() - imageOffset, getHeight());
            }

        } else if(editing) {
            if (drawsFocusBorderAroundIcon) {
                imageOffset = 0;
            }
            else if (imageOffset == -1) {
                imageOffset = getLabelStart();
            }
            if(getComponentOrientation().isLeftToRight()) {
                paintEditingBorder(g, imageOffset, 0, getWidth() - imageOffset, getHeight());
            } else {
                paintEditingBorder(g, 0, 0, getWidth() - imageOffset, getHeight());
            }
        }
    }

    @Override
    protected void paintBody(Graphics g) {

        Color bColor = choosePaintBackgroundColor();

        int imageOffset = -1;
        if (bColor != null && (baseOpacity || customOpacity)) {
            imageOffset = getLabelStart();
            g.setColor(bColor);
            if(getComponentOrientation().isLeftToRight()) {
                g.fillRect(imageOffset, 0, getWidth() - imageOffset,
                           getHeight());
            } else {
                g.fillRect(0, 0, getWidth() - imageOffset,
                           getHeight());
            }
        }

        paintBorder(g, bColor, imageOffset);
    }


    //////////
    // MISC //
    //////////

    private void paintWSBorder(Graphics g, int x, int y, int w, int h) {
        g.setColor(Color.red);
        BasicGraphicsUtils.drawDashedRect(g, x, y, w, h);
    }

    private void paintEditingBorder(Graphics g, int x, int y, int w, int h) {
        g.setColor(editBorder);
        g.drawRect(x, y, w - 1, h - 1);
    }

    private void paintEditedBorder(Graphics g, int x, int y, int w, int h, int countdown) {
        g.setColor(new Color(36, 127, 70, 255 -
            (FTreeConstant.EDITED_COUNTDOWN - countdown) * 12));
        g.drawRect(x, y, w - 1, h - 1);
    }
}
