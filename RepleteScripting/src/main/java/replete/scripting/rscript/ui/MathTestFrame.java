package replete.scripting.rscript.ui;

import java.awt.CardLayout;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;

import replete.scripting.rscript.inspection.RScriptInspector;
import replete.scripting.rscript.parser.RScript;
import replete.scripting.rscript.parser.RScriptParser;
import replete.scripting.rscript.parser.gen.ASTConstant;
import replete.scripting.rscript.parser.gen.ASTFunction;
import replete.scripting.rscript.parser.gen.ASTKeyValuePair;
import replete.scripting.rscript.parser.gen.ASTListOrMap;
import replete.scripting.rscript.parser.gen.ASTNode;
import replete.scripting.rscript.parser.gen.ASTOperator;
import replete.scripting.rscript.parser.gen.ASTStatement;
import replete.scripting.rscript.parser.gen.ASTUnit;
import replete.scripting.rscript.parser.gen.ASTVariable;
import replete.text.StringUtil;
import replete.threads.ActionDescriptor;
import replete.threads.SwingTimerManager;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.text.DocumentChangeListener;
import replete.ui.text.RTextArea;
import replete.ui.text.editor.REditor;
import replete.ui.tree.NodeBase;
import replete.ui.tree.NodeSimpleLabel;
import replete.ui.tree.RTree;
import replete.ui.tree.RTreeNode;
import replete.ui.windows.notifications.NotificationFrame;

public class MathTestFrame extends NotificationFrame {

    private REditor txtInput;
    private RTextArea txtError;
    private RPanel pnlParse;
    private CardLayout layParse;
    private RTree treParse;
    private JLabel lblSymbols;

    public MathTestFrame() {
        super("Math Test Frame");

        Lay.BLtg(this,
            "N",
            "C", Lay.GL(2, 1,
                Lay.SPL(
                    Lay.BL(
                        "N", Lay.lb("Input", "fg=white,bg=100,size=14,eb=2"),
                        "C", txtInput = Lay.ed("", "font=Monospaced,size=14,ruler")
                    ),
                    Lay.BL(
                        "N", Lay.lb("Parse", "fg=white,bg=100,size=14,eb=2"),
                        "C", pnlParse = (RPanel) Lay.CL(
                            "error", Lay.sp(txtError = Lay.txa("", "font=Monospaced,size=14,fg=red,editable=false")),
                            "tree", Lay.sp(treParse = Lay.tr())
                        ),
                        "S", lblSymbols = Lay.lb()
                    ),
                    "divpixel=650"
                ),
                Lay.BL(
                    "N", Lay.lb("Evaluation", "fg=white,bg=100,size=14,eb=2"),
                    "C", Lay.sp(Lay.txa("", "font=Monospaced,size=14"))
                )
            ),
            "S", Lay.FL("R", Lay.btn("&Close", CommonConcepts.CANCEL), "bg=100"),
            "size=[1000,800],center"
        );

        treParse.setRootVisible(false);
        layParse = (CardLayout) pnlParse.getLayout();

        txtInput.getTextPane().getDocument().addDocumentListener(new DocumentChangeListener() {
            @Override
            public void documentChanged(DocumentEvent e) {
                SwingTimerManager.delayedAction(
                    new ActionDescriptor()
                        .setId("parsePanelTransparency")
                        .setImmediateAction(() -> pnlParse.setTransparency(0.5))
                        .setDelay(1500)
                        .setDelayedAction(() -> {
                            reparse();
                            pnlParse.setTransparency(null);
                        })
                );
            }
        });
    }

    protected void reparse() {
        if(StringUtil.isBlank(txtInput.getText())) {
            txtError.clear();
            treParse.setModel((RTreeNode) null);
        } else {
            try {
                String source = txtInput.getText();
                RScriptParser parser = new RScriptParser();
                RScript script = parser.parse(source);
                rebuildParseTree(script);
                layParse.show(pnlParse, "tree");
                treParse.expandAll();
                RScriptInspector inspector = new RScriptInspector();
                Set<String> set = inspector.getSymbols(script);  // Currently: All Symbols
                String vars;
                if(set.isEmpty()) {
                    vars = "(None)";
                } else {
                    vars = set.toString().replaceAll("[\\[\\]]", "");
                }
                lblSymbols.setText("<html>Symbols: " + vars + "</html>");
            } catch(Exception ex) {
                txtError.setText(ex.getMessage());
                layParse.show(pnlParse, "error");
                txtError.setCaretPosition(0);
            }
        }
    }

    private void rebuildParseTree(RScript eq) {
        RTreeNode nRoot = new RTreeNode(new NodeSimpleLabel("root"));
        int s = 0;
        for(ASTStatement statement : eq.getStatements()) {
            RTreeNode nStatement = nRoot.add(wrap(statement, s));
            populate(nStatement, statement);
            s++;
        }
        treParse.setModel(nRoot);
    }

    private NodeBase wrap(ASTNode node, int index) {
        if(node instanceof ASTStatement) {
            return new NodeStatement((ASTStatement) node, index);
        }
        if(node instanceof ASTConstant) {
            return new NodeConstant((ASTConstant) node);
        }
        if(node instanceof ASTFunction) {
            return new NodeFunction((ASTFunction) node);
        }
        if(node instanceof ASTListOrMap) {
            return new NodeListOrMap((ASTListOrMap) node);
        }
        if(node instanceof ASTKeyValuePair) {
            return new NodeKeyValuePair((ASTKeyValuePair) node);
        }
        if(node instanceof ASTOperator) {
            return new NodeOperator((ASTOperator) node);
        }
        if(node instanceof ASTUnit) {
            return new NodeUnit((ASTUnit) node);
        }
        if(node instanceof ASTVariable) {
            return new NodeVariable((ASTVariable) node);
        }
        return null;
    }

    private void populate(RTreeNode nRoot, ASTNode root) {
        for(int c = 0; c < root.getCount(); c++) {
            ASTNode child = root.getChild(c);
            RTreeNode nChild = nRoot.add(wrap(child, -1));
            populate(nChild, child);
        }
    }
}
