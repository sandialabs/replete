
package finio.platform.exts.view.treeview.ui;

import finio.core.FConst;
import finio.core.FUtil;
import finio.core.NonTerminal;
import finio.core.events.KeyAddedEvent;
import finio.core.events.KeyAddedListener;
import finio.core.events.KeyChangedEvent;
import finio.core.events.KeyChangedListener;
import finio.core.events.KeyRemovedEvent;
import finio.core.events.KeyRemovedListener;
import finio.core.events.MapBatchChangedEvent;
import finio.core.events.MapBatchChangedListener;
import finio.core.events.MapClearedEvent;
import finio.core.events.MapClearedListener;
import finio.core.events.ValueChangedEvent;
import finio.core.events.ValueChangedListener;
import finio.platform.exts.view.treeview.ui.nodes.NodeExpandable;
import finio.platform.exts.view.treeview.ui.nodes.NodeFTree;
import finio.platform.exts.view.treeview.ui.nodes.NodeNonTerminal;
import finio.platform.exts.view.treeview.ui.nodes.NodeTerminal;
import finio.platform.exts.view.treeview.ui.nodes.NodeWorld;
import replete.collections.Pair;
import replete.ui.GuiUtil;
import replete.ui.tree.RTreeNode;
import replete.ui.windows.Dialogs;

public class FNode extends RTreeNode {


    ////////////
    // FIELDS //
    ////////////

    // These exist here as static references so that every
    // node knows the context in which it lives.  Nodes
    // can properly tell the tree model that things are
    // changing and ask the tree to update its visual
    // state using these references.

    private FTree tree;

    // Book-keeping (temporary debugging)

    private static int constructedNodes = 0;
    public static int getConstructedNodes() {
        return constructedNodes;
    }


    ////////////
    // FIELDS //
    ////////////

    // Listeners null for a tree node that wraps a terminal value.
    //private ANodeMapChangedListener mapChangedListener;   Not needed right now
    private ANodeMapBatchChangedListener mapBatchChangedListener;
    private ANodeMapClearedListener mapClearedListener;
    private ANodeKeyAddedListener keyAddedListener;
    private ANodeKeyRemovedListener keyRemovedListener;
    private ANodeKeyChangedListener keyChangedListener;
    private ANodeValueChangedListener valueChangedListener;
    private boolean paused = false;
    private String simplified = null;
    private boolean meta = true;
    private int depth = 0;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FNode() {
        // Necessary so ATree can construct a blank node
        // for its initial root node.
    }
    public FNode(NodeFTree uObj, int populateLevels, int depth, FTree tree, boolean meta) {
        super(uObj);
        this.depth = depth;
        this.tree = tree;
        this.meta = meta;

        if(uObj instanceof NodeNonTerminal) {
            paused = (populateLevels <= 0);
            ((NodeNonTerminal) uObj).setPaused(paused);
        }

        initialize(populateLevels);

        synchronized(FNode.class) {
            constructedNodes++;
        }
    }

    public void setPaused(boolean paused) {
        if(paused == this.paused) {
            return;
        }
        this.paused = paused;

        updateAfterChangedPausedSimplified();
    }

    public void setSimplified(String simplified) {
        if(FUtil.equals(simplified, this.simplified)) {
            return;
        }
        this.simplified = simplified;

        updateAfterChangedPausedSimplified();
    }

    private void updateAfterChangedPausedSimplified() {
        if(!paused && simplified == null) {
            if(getCount() != 0) {
                throw new RuntimeException(FNode.class.getSimpleName() + ": this node in a bad state.");
            }
            if(getV() instanceof NonTerminal) {
                subscribe();
                populate(this, getM(), Integer.MAX_VALUE);
            }
        } else {
            detach();
        }

        if(getObject() instanceof NodeExpandable) {
            ((NodeExpandable) getObject()).setPaused(paused);
            ((NodeExpandable) getObject()).setSimplified(simplified);
        }

        GuiUtil.safe(new Runnable() {
            @Override
            public void run() {
                tree.getModel().nodeStructureChanged(FNode.this);
            }
        });
    }

    public void setShowAlphaMeta(boolean show, boolean recursive) {
        String K = FConst.SYS_META_KEY;
        FNode nMeta = getChildByKey(K);
        meta = show;

        if(!show && nMeta != null) {
            int position = getIndex(nMeta);
            remove(position);
            GuiUtil.safe(new Runnable() {
                @Override
                public void run() {
                    tree.getModel().nodeStructureChanged(FNode.this);
                }
            });

        } else if(show && nMeta == null && !paused && simplified == null) {
            NodeFTree uThis = getObject();
            if(uThis instanceof NodeExpandable) {
                NodeExpandable uExp = (NodeExpandable) uThis;
                NonTerminal M = (NonTerminal) uExp.getV();
                if(M.containsKey(K)) {
                    Object Vchild = M.get(K);
                    // TODO M only provided for NodeRealm reasons
                    createAndAddChildNode(this, M, K, Vchild, Integer.MAX_VALUE);
                    GuiUtil.safe(new Runnable() {
                        @Override
                        public void run() {
                            tree.getModel().nodeStructureChanged(FNode.this);
                        }
                    });
                }
            }
        }

        if(recursive) {
            for(RTreeNode nChild : this) {
                ((FNode) nChild).setShowAlphaMeta(show, recursive);
            }
        }
    }

    // Call this if the node has gotten a new user object / new value.
    private void initialize() {
        initialize(Integer.MAX_VALUE);
    }
    private void initialize(int populateLevels) {
        Object V = getV();

        // If this tree node is wrapping a non-terminal,
        // then listen to changes in the non-terminal.
        if(V instanceof NonTerminal) {
            NonTerminal M = (NonTerminal) V;

            if(!paused && simplified == null) {
                subscribe();

                // Create my children tree nodes depending on the key-value pairs
                // in my non-terminal.  This may become optional.....
                populate(this, M, populateLevels);
            }
        }
    }

    private void subscribe() {
        NonTerminal M = getM();

        if(mapClearedListener != null) {
            throw new RuntimeException(
                FNode.class.getSimpleName() + ": Attempting to subscribe to the non-terminal value again.");
        }

        //M.addMapChangedListener(mapChangedListener = new ANodeMapChangedListener());   Not needed right now
        M.addMapBatchChangedListener(mapBatchChangedListener = new ANodeMapBatchChangedListener());
        M.addMapClearedListener(mapClearedListener = new ANodeMapClearedListener());
        M.addKeyAddedListener(keyAddedListener = new ANodeKeyAddedListener());
        M.addKeyRemovedListener(keyRemovedListener = new ANodeKeyRemovedListener());
        M.addKeyChangedListener(keyChangedListener = new ANodeKeyChangedListener());
        M.addValueChangedListener(valueChangedListener = new ANodeValueChangedListener());
    }

    private void unsubscribe() {
        NonTerminal M = getM();

        //M.removeMapChangedListener(mapChangedListener);   Not needed right now
        M.removeMapBatchChangedListener(mapBatchChangedListener);
        M.removeMapClearedListener(mapClearedListener);
        M.removeKeyAddedListener(keyAddedListener);
        M.removeKeyRemovedListener(keyRemovedListener);
        M.removeKeyChangedListener(keyChangedListener);
        M.removeValueChangedListener(valueChangedListener);

        mapClearedListener = null;
        keyAddedListener = null;
        keyRemovedListener = null;
        keyChangedListener = null;
        valueChangedListener = null;
    }

    private void detach() {
        detach(this);
    }
    private void detach(FNode n) {
        if(n.getV() instanceof NonTerminal) {
            n.unsubscribe();
        }
        for(RTreeNode nChild : n) {
            detach((FNode) nChild);
        }
        n.removeAllChildren();
    }

    // For each key-value pair in the non-terminal, add a child tree node
    // to this tree node that wraps the pair.  It is assumed at this point
    // that the parent tree node is empty.  The order of that the child
    // tree nodes are added to the parent node is the order in which the
    // key-value pairs appear in the non-terminal.
    private void populate(FNode nParent, NonTerminal M, int populateLevels) {
        if(nParent.getCount() != 0) {
            throw new RuntimeException(
                FNode.class.getSimpleName() + ": attempting to populate a tree node that is not empty");
        }
        for(Object Kchild : M.K()) {
            if(Kchild.equals(FConst.SYS_META_KEY) && !meta) {
                continue;
            }
            Object Vchild = M.get(Kchild);
            // TODO M only provided for NodeRealm reasons
            createAndAddChildNode(nParent, M, Kchild, Vchild, populateLevels);
        }
    }

    // NOTE: Assumes that the new child node should be added at the END
    // of the children list.  This may have to change in the future!
    private FNode createAndAddChildNode(FNode nParent, NonTerminal Mparent, Object Kchild, Object Vchild) {
        return createAndAddChildNode(nParent, Mparent, Kchild, Vchild, Integer.MAX_VALUE);
    }

    private FNode createAndAddChildNode(FNode nParent, NonTerminal Mparent, Object Kchild,
                                        Object Vchild, int populateLevels) {
        NodeFTree uChild =
            chooseNodeUserObject(Mparent, Kchild, Vchild, nParent.get() instanceof NodeWorld, null);
        FNode nChild = new FNode(
            uChild, populateLevels - 1,
            nParent.getANodeDepth() + 1,
            nParent.getTree(), meta);
        nParent.add(nChild); // Not going to notify the model here! Just editing the tree hierarchy.
        return nChild;
    }

    // This code should be reviewed.  It might be why some pauses are confused.
    // Also, not sure if can use 'copy' here or not...
    private NodeFTree chooseNodeUserObject(NonTerminal Mparent, Object Kchild, Object Vchild,
                                           boolean realm, NodeFTree uPrevious) {
        NodeFTree uChild;

        if(Vchild instanceof NonTerminal) {
            uChild = new NodeNonTerminal(Kchild, Vchild, realm);

            // Copy non-terminal-specific properties.
            if(uPrevious instanceof NodeNonTerminal) {
                NodeNonTerminal uNT = (NodeNonTerminal) uPrevious;
                ((NodeNonTerminal) uChild).setPaused(uNT.isPaused());
                String simp = uNT.getSimplified();
                ((NodeNonTerminal) uChild).setSimplified(simp == null ? null : "(STALE) " + simp);
            }

        } else {
            uChild = new NodeTerminal(Kchild, Vchild);

        }

        // Copy tree node properties.
        if(uPrevious != null) {
            uChild.setAnchor(uPrevious.isAnchor());
            uChild.setHover(uPrevious.isHover());
            uChild.setWorkingScope(uPrevious.isWorkingScope());
            uChild.setZoomed(uPrevious.isZoomed());
            uChild.setEditing(uPrevious.isEditing());
        }

        return uChild;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isPaused() {
        return paused;
    }
    public String getSimplified() {
        return simplified;
    }
    public int getANodeDepth() {
        return depth;
    }
    public FTree getTree() {
        return tree;
    }

    // Accessors (Computed)

    public Object getK() {
        NodeFTree uNode = getObject();
        return uNode.getK();
    }
    public Object getV() {
        NodeFTree uNode = getObject();
        return uNode.getV();
    }

    public NonTerminal getM() { // For ANode's wrapping a non-terminal value
        return (NonTerminal) getV();
    }
    public FNode getChildByKey(Object K) {
        Pair<FNode, Integer> pair = findChildNodeByKey(K);
        if(pair != null) {
            return pair.getValue1();
        }
        return null;
    }
    @Override
    public FNode getParent() {
        return (FNode) super.getParent();
    }

    // Mutators

    // for remove
//    public void removeSysMeta() {
//        final ANode nSysMeta = getChildByKey(AConst.SYS_META_KEY);
//        if(nSysMeta != null) {
//            remove(nSysMeta);
////            GuiUtil.safe(new Runnable() {
////                public void run() {
////                    treeModel.remove(nSysMeta);
////                }
////            });
//        }
//    }
//    public void addSysMeta() {
//        ANode nSysMeta = getChildByKey(AConst.SYS_META_KEY);
//        if(nSysMeta == null) {
//            Object V = getV();
//            if(V instanceof ANonTerminal) {
//                ANonTerminal M = (ANonTerminal) V;
//                if(M.has(AConst.SYS_META_KEY)) {
//                    final ANode nSysMeta2 = createNewTreeNodeForNewPair(
//                        V, AConst.SYS_META_KEY, M.get(AConst.SYS_META_KEY));
//                    add(nSysMeta2);
//
////                    GuiUtil.safe(new Runnable() {
////                        public void run() {
////                            treeModel.append(ANode.this, nSysMeta2);  // Can this be done in abatch sense?
////                        }
////                    });
//                }
//            }
//        }
//    }


    //////////
    // MISC //
    //////////

    // TODO: Check whether things are properly happening on UI thread.
//    private void expandSelectNode(ANode nChild) {
//        tree.expand(nChild);
//        tree.setSelectionPath(new TreePath(nChild.getPath()));
//        tree.updateUI();
//        tree.requestFocusInWindow();
//    }

    private int[] getIndexArray(int i) {
        return new int[] {i};
    }

    private Pair<FNode, Integer> findChildNodeByKey(Object K) {
        int count = getCount();
        for(int i = 0; i < count; i++) {
            FNode nChild = (FNode) getRChildAt(i);
            if(nChild.getK() == K || nChild.getK().equals(K)) {
                return new Pair<>(nChild, i);
            }
        }
        return null;
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class ANodeMapBatchChangedListener implements MapBatchChangedListener {
        @Override
        public void mapBatchChanged(MapBatchChangedEvent e) {

            // Almost detach.  Destroys all tree connections in this
            // branch and removes all non-terminal listeners
            for(RTreeNode nChild : FNode.this) {
                ((FNode) nChild).detach();
            }
            removeAllChildren();

            try {
                Object V = getV();
                NonTerminal M = (NonTerminal) V;
                populate(FNode.this, M, Integer.MAX_VALUE);
            } catch(Exception x) {
                x.printStackTrace();
            }

            GuiUtil.safe(new Runnable() {
                @Override
                public void run() {
                    tree.getModel().nodeStructureChanged(FNode.this);
                }
            });
        }
        @Override
        public String toString() {
            return "Tree Map Batch Changed Listener";
        }
    }

    private class ANodeMapClearedListener implements MapClearedListener {
        @Override
        public void mapCleared(MapClearedEvent e) {

            // Almost detach (leaves "this" node's listeners attached)
            for(RTreeNode nChild : FNode.this) {
                ((FNode) nChild).detach(); // Destroys all tree connections in this branch and
                                           // removes all non-terminal listeners
            }
            removeAllChildren();

            GuiUtil.safe(new Runnable() {
                @Override
                public void run() {
                    tree.getModel().nodeStructureChanged(FNode.this);
                }
            });
        }
        @Override
        public String toString() {
            return "Tree Map Cleared Listener";
        }
    }

    // Done
    private class ANodeKeyAddedListener implements KeyAddedListener {
        @Override
        public void keyAdded(final KeyAddedEvent e) {

            // Add the new non-terminal key to this tree node.
            createAndAddChildNode(
                FNode.this, getM(), e.getK(), e.getV());   // Assumed key was added to end of map! :(
            final int[] indices = getIndexArray(getCount() - 1);

            // Update the model on the UI thread
            GuiUtil.safe(new Runnable() {
                @Override
                public void run() {
                    tree.getModel().nodesWereInserted(FNode.this, indices);
                }
            });
        }
        @Override
        public String toString() {
            return "Tree Key Added Listener";
        }
    }

    // Done
    private class ANodeKeyRemovedListener implements KeyRemovedListener {
        @Override
        public void keyRemoved(KeyRemovedEvent e) {

            // Remove the child node that corresponds to the key in the
            // non-terminal that was removed.
            final Object Kremoved = e.getK();
            final Pair<FNode, Integer> foundPair = findChildNodeByKey(Kremoved);
            if(foundPair != null) {
                remove(foundPair.getValue2());
                foundPair.getValue1().detach(); // Destroys all tree connections in this branch and removes all non-terminal listeners
            }

            // Update the model on the UI thread
            GuiUtil.safe(new Runnable() {
                @Override
                public void run() {
                    if(foundPair != null) {
                        tree.getModel().nodesWereRemoved(FNode.this,
                            getIndexArray(foundPair.getValue2()),
                            new Object[] {foundPair.getValue1()});
                    } else {
                        Dialogs.showError(tree, FNode.class.getSimpleName() + ": could not find removed key '" + Kremoved +
                            "'.", "Error"); // Error condition
                    }
                }
            });
        }
        @Override
        public String toString() {
            return "Tree Key Removed Listener";
        }
    }

    // Done
    private class ANodeKeyChangedListener implements KeyChangedListener {
        @Override
        public void keyChanged(KeyChangedEvent e) {

            // The following strategy assumes that the new key cannot
            // effect the type of the user object stored in the
            // child tree node.  In other words, a key cannot mean the
            // tree node holds a NodeTerminal instead of a NodeNonTerminal.
            // If that assumption changes, then we need to construct
            // a new user object for the node, and most likely, NodeATree
            // no longer needs a setK method (can revert to being immutable).

            final Object Kchanged = e.getK();
            final Pair<FNode, Integer> foundPair = findChildNodeByKey(Kchanged);
            final FNode nChild;
            if(foundPair != null) {
                nChild = foundPair.getValue1();
                NodeFTree uChild = nChild.getObject();
                uChild.setK(e.getNewK());
            } else {
                nChild = null;
            }

            // Update the model on the UI thread
            GuiUtil.safe(new Runnable() {
                @Override
                public void run() {
                    if(foundPair != null) {
                        tree.getModel().nodeChanged(nChild);
                    } else {
                        Dialogs.showError(tree, FNode.class.getSimpleName() + ": could not find changed key '" + Kchanged +
                            "'.", "Error"); // Error condition
                    }
                }
            });
        }
        @Override
        public String toString() {
            return "Tree Key Changed Listener";
        }
    }

    private class ANodeValueChangedListener implements ValueChangedListener {
        @Override
        public void valueChanged(ValueChangedEvent e) {

            final Object Kaffected = e.getK();
            final Pair<FNode, Integer> foundPair = findChildNodeByKey(Kaffected);
            final FNode nChild;
            if(foundPair != null) {
                // Alternatively could just remove the found ANode and
                // construct and add a new one in its place.  Upside
                // is you remove the duplication of code in setUserObject &
                // initialize but downside is can't just call model's
                // nodeChanged method.
                nChild = foundPair.getValue1();
                nChild.detach();       // Destroys all tree connections in this branch and removes all non-terminal listeners
                NodeFTree uChild = chooseNodeUserObject(getM(), Kaffected, e.getNewV(), get() instanceof NodeWorld, (NodeFTree) nChild.get());
                paused = false;        // TODO What is this for?
                nChild.setUserObject(uChild);
                nChild.initialize();   // Repopulates the branch
            } else {
                nChild = null;
            }

            // Update the model on the UI thread
            GuiUtil.safe(new Runnable() {
                @Override
                public void run() {
                    if(foundPair != null) {
                        tree.getModel().nodeChanged(nChild);
                        tree.getModel().nodeStructureChanged(nChild);
                    } else {
                        Dialogs.showError(tree, FNode.class.getSimpleName() +
                            ": could not find affected key '" + Kaffected + "'.", "Error");   // Error condition
                    }
                }
            });
        }
        @Override
        public String toString() {
            return "Tree Value Changed Listener";
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return super.toString() +
            " {P" + (paused ? "+" : "-") +
            " S" + (simplified != null ? "+" : "-") +
            " D" + depth + "}";
    }
}
