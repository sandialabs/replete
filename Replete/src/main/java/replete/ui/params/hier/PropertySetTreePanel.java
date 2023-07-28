package replete.ui.params.hier;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;

import replete.params.hier.Criteria;
import replete.params.hier.PropertyGroup;
import replete.params.hier.PropertyParams;
import replete.params.hier.PropertySet;
import replete.params.hier.PropertySetRootException;
import replete.params.hier.PropertySetSpecification;
import replete.params.hier.PropertySetTree;
import replete.params.hier.PropertySetUnsatisfiedSpecException;
import replete.text.StringUtil;
import replete.ui.BeanPanel;
import replete.ui.ColorLib;
import replete.ui.GuiUtil;
import replete.ui.button.RToggleButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.label.BorderedLabel;
import replete.ui.lay.Lay;
import replete.ui.params.hier.images.HierParamsImageModel;
import replete.ui.params.hier.nodes.NodeGroup;
import replete.ui.params.hier.nodes.NodeProperty;
import replete.ui.tabbed.RTabbedPane;
import replete.ui.tree.NodeEmptyRoot;
import replete.ui.tree.RTree;
import replete.ui.tree.RTreeNode;
import replete.ui.tree.state.VisualStateSavingNoFireTree;
import replete.ui.validation.ValidationContext;
import replete.ui.windows.Dialogs;

public class PropertySetTreePanel<T> extends BeanPanel<PropertySetTree<T>> {


    ////////////
    // FIELDS //
    ////////////

    private PropertySetSpecification spec;
    private RTreeNode nRoot;
    private VisualStateSavingNoFireTree tre;
    private BeanPanel<T> pnlTest;    // Optional
    private JPanel pnlGlobalOuter;
    private SingleGlobalPanel pnlGlobal;
    private JPanel pnlHier;
    private JPanel pnlHierAndTest;
    private RToggleButton btnToggleTest;
    private CriteriaBeanPanel<T, ?> pnlCriteria;
    private Criteria<T> blankCriteria;
    private BorderedLabel lblGlobal, lblHier;
    private BorderedLabel lblSelectedAction;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PropertySetTreePanel(PropertySetSpecification spec, BeanPanel<T> pnlTest,
                                CriteriaBeanPanel<T, ?> pnlCriteria, Criteria<T> blankCriteria) {
        this.spec = spec;
        this.pnlTest = pnlTest;
        this.pnlCriteria = pnlCriteria;
        this.blankCriteria = blankCriteria;

        JButton btnAddGroup;
        JButton btnAddProperty;
        JButton btnEdit, btnEditAll, btnDelete;
        JButton btnMoveUp, btnMoveDown;

        pnlGlobalOuter = Lay.BL("C", pnlGlobal = new SingleGlobalPanel(spec));

        pnlHier = Lay.BL(
            "N", Lay.BxL("X",
                Lay.BL(
                    btnAddGroup = Lay.btn(HierParamsImageModel.GROUP_ADD, 2, "ttt=Add-Subgroup..."),
                    "eb=5r,alignx=0.5,maxW=20"
                ),
                Lay.BL(
                    btnMoveUp = Lay.btn(CommonConcepts.MOVE_UP, 2, "ttt=Move-Group-Up"),
                    "eb=5r,alignx=0.5,maxW=20"
                ),
                Lay.BL(
                    btnMoveDown = Lay.btn(CommonConcepts.MOVE_DOWN, 2, "ttt=Move-Group-Down"),
                    "eb=5r,alignx=0.5,maxW=20"
                ),
                Lay.BL(
                    btnAddProperty = Lay.btn(HierParamsImageModel.PROPERTY_ADD, 2, "ttt=Add-Property..."),
                    "eb=5r,alignx=0.5,maxW=20"
                ),
                Lay.BL(
                    btnEdit = Lay.btn(CommonConcepts.EDIT, 2, "ttt=Edit..."),
                    "eb=5r,alignx=0.5,maxW=20"
                ),
                Lay.BL(
                    btnEditAll = Lay.btn(HierParamsImageModel.EDIT_ALL, 2, "ttt=Edit-All..."),
                    "eb=5r,alignx=0.5,maxW=20"
                ),
                Lay.BL(
                    btnDelete = Lay.btn(CommonConcepts.DELETE, 2, "ttt=Remove"),
                    "eb=5r,alignx=0.5,maxW=20"
                ),
                pnlTest == null ? null : Lay.BL(
                    btnToggleTest = Lay.btnt(HierParamsImageModel.TEST, 2, "ttt=Toggle-Test"),
                    "eb=5r,alignx=0.5,maxW=20"
                ),
                Box.createHorizontalGlue(),
                "eb=5tb"
            ),
            "C", Lay.sp(tre = (VisualStateSavingNoFireTree) Lay.tr("vss")),
            "eb=5lrb"
        );

        pnlHierAndTest = Lay.BL(
            "C", pnlHier
        );

        Lay.BLtg(this,
            "N", Lay.FL("L",
                Lay.lb("Property Set Type:"),
                lblGlobal = makeActionLabel("Global", HierParamsImageModel.GLOBAL),
                lblHier   = makeActionLabel("Hierarchical", HierParamsImageModel.HIERARCHICAL),
                "mb=1b"
            ),
            "C", pnlHierAndTest
        );

        btnAddGroup.addActionListener(e -> doAddGroup());
        btnAddProperty.addActionListener(e -> doAddProperty());
        btnEdit.addActionListener(e -> doEdit());
        btnEditAll.addActionListener(e -> doEditAll());
        btnDelete.addActionListener(e -> doDelete());
        btnMoveUp.addActionListener(e -> moveUp());
        btnMoveDown.addActionListener(e -> moveDown());

        if(btnToggleTest != null) {
            btnToggleTest.addActionListener(e -> toggleTest());
        }

        tre.setToggleClickCount(0);
        tre.setRootVisible(true);
        tre.addDoubleClickListener(e -> doEdit());
    }

    private void setSelectedLabel(BorderedLabel lblAction) {
        if(lblSelectedAction != null) {
            lblSelectedAction.setBubbleBorderColor(BorderedLabel.DEFAULT_BUBBLE_BORDER_COLOR);
            lblSelectedAction.setBubbleBackgroundColor(BorderedLabel.DEFAULT_BUBBLE_BACKGROUND_COLOR);
        }
        lblSelectedAction = lblAction;
        lblSelectedAction.setBubbleBorderColor(ColorLib.YELLOW_DARK);
        lblSelectedAction.setBubbleBackgroundColor(ColorLib.YELLOW_LIGHT);
    }

    private BorderedLabel makeActionLabel(String label, ImageModelConcept concept) {
        BorderedLabel lbl = Lay.lb(label, concept, "bordered,cursor=hand,center");
        lbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(lblSelectedAction != lbl) {
                    if(lblSelectedAction == lblHier || lblSelectedAction == lblGlobal && pnlGlobal.checkValidationPass()) {
                        setSelectedLabel(lbl);
                        updateGlobalHier();
                    }
                }
            }
        });
        return lbl;
    }

    private void moveUp() {
        RTreeNode nGroup = getEnclosingGroupNode();
        if(nGroup == null) {
            return;
        }

        if(nGroup.moveUp(NodeGroup.class)) {
            tre.saveState();
            tre.getModel().nodeStructureChanged(nGroup.getParent());
            tre.restoreState();
        }
    }
    private void moveDown() {
        RTreeNode nGroup = getEnclosingGroupNode();
        if(nGroup == null) {
            return;
        }

        if(nGroup.moveDown(NodeGroup.class)) {
            tre.saveState();
            tre.getModel().nodeStructureChanged(nGroup.getParent());
            tre.restoreState();
        }
    }

    private void updateGlobalHier() {
        remove(pnlGlobalOuter);
        remove(pnlHierAndTest);
        if(lblSelectedAction == lblGlobal) {
            pushHierToGlobal();
            add(pnlGlobalOuter, BorderLayout.CENTER);
        } else {
            pushGlobalToHier();
            add(pnlHierAndTest, BorderLayout.CENTER);
        }
        updateUI();
    }

    private void pushHierToGlobal() {
        if(nRoot != null) {                  // This will be null on construction
            PropertyGroup<T> rootNode = convert(tre.getRoot());
            pnlGlobal.set(rootNode);
        }
    }

    private void pushGlobalToHier() {
        PropertyGroup<T> rootNode = pnlGlobal.get();
        populateTreeNodeWithProperties(tre.getRoot(), rootNode.getProperties());
    }

    private void toggleTest() {
        if(pnlHierAndTest.getLayout() instanceof GridLayout) {
            setSingleMode();
            btnToggleTest.setSelected(false);
        } else {
            setDoubleMode();
            btnToggleTest.setSelected(true);
        }
        btnToggleTest.focus();
    }

    private void setSingleMode() {
        pnlHierAndTest.removeAll();
        Lay.BLtg(pnlHierAndTest, "C", pnlHier);
        pnlHierAndTest.updateUI();
    }

    private void setDoubleMode() {
        pnlHierAndTest.removeAll();
        JButton btnTest, btnReturn;
        RTabbedPane tabs;
        RTree treResults;
        Lay.GLtg(pnlHierAndTest, 2, 1,
            pnlHier,
            Lay.BL(
                "C", tabs = Lay.TBL(
                    "Target",
                        CommonConcepts.TARGET,
                        Lay.BL(
                            "C", pnlTest,
                            "S", Lay.FL("R",
                                btnTest = Lay.btn("&Test", CommonConcepts.PLAY)
                            )
                        ),
                    "Results",
                        CommonConcepts.OPTIONS,
                        Lay.BL(
                            "C", Lay.p(Lay.sp(treResults = Lay.tr()), "eb=5lrt"),
                            "S", Lay.FL("L",
                                btnReturn = Lay.btn("Re&turn", CommonConcepts.PREV)
                            )
                        )
                ),
                "eb=5lrb"
            )
        );

        btnTest.addActionListener(e -> {
            try {
                PropertySet properties = get().resolve(spec, pnlTest.get());
                RTreeNode nRoot = new RTreeNode(new NodeEmptyRoot());   // will never be seen
                for(String key : properties.keySet()) {
                    PropertyParams params = properties.get(key);
                    nRoot.add(new NodeProperty(spec, key, params));
                }
                tabs.setSelectedTab(1);
                treResults.setModel(nRoot);

            } catch(PropertySetRootException ex) {
                Dialogs.showWarning(
                    getWindow(),
                    "The criteria for the root group of an property tree must ALWAYS apply to all targets.  " +
                    "However, the critera did not apply to the given target.",
                    "Resolution Problem"
                );

            } catch(PropertySetUnsatisfiedSpecException ex) {
                String keyText = "";
                if(!ex.getMissingKeys().isEmpty()) {
                    keyText +=
                        "Resolving the target against the property tree has resulted " +
                        "in a property set that does not contain a property of every type.  " +
                        "The following properties were missing from the resolution:\n\n      ";
                    for(String key : ex.getMissingKeys()) {
                        keyText += spec.getSlot(key).getName() + "      \n";
                    }
                    keyText = StringUtil.removeEnd(keyText, "      \n");
                    keyText += "\n\n";
                    keyText +=
                        "This indicates that the tree does have sufficient defaults at the root level or " +
                        "is improperly constructed.\n\n";
                }
                if(!ex.getExtraKeys().isEmpty()) {
                    keyText += "The following property keys were returned but do not exist in the specification:\n\n      ";
                    keyText += StringUtil.join(ex.getExtraKeys(), "      \n");
                    keyText += "\n\n";
                    keyText += "This indicates an internal system error has occurred.\n\n";
                }

                Dialogs.showWarning(getWindow(), keyText.trim(), "Resolution Problem");

            } catch(Exception ex) {
                Dialogs.showDetails(
                    getWindow(),
                    "An unexpected error has occurred while resolving the properties for the given target.",
                    "Resolution Error",
                    ex
                );
            }
        });
        btnReturn.addActionListener(e -> tabs.setSelectedTab(0));

        pnlHierAndTest.updateUI();
    }

    @Override
    public PropertySetTree<T> get() {
        if(lblSelectedAction == lblGlobal) {
            PropertyGroup<T> updatedRootNode = pnlGlobal.get();
            PropertyGroup<T> updatedRootNodeWithoutChildren = updatedRootNode.copyWithoutChildren();
            return new PropertySetTree<>(updatedRootNodeWithoutChildren);
        }
        PropertyGroup<T> rootNode = convert(tre.getRoot());
        return new PropertySetTree<>(rootNode);
    }

    private PropertyGroup<T> convert(RTreeNode node) {
        PropertyGroup<T> aNode = new PropertyGroup<>();
        NodeGroup uNode = node.get();
        aNode.setLabel(uNode.getGroup().getLabel());
        aNode.setCriteria(uNode.getGroup().getCriteria());
        for(RTreeNode child : node) {
            if(child.get() instanceof NodeGroup) {
                aNode.getChildren().add(convert(child));
            } else if(child.get() instanceof NodeProperty) {
                NodeProperty uProperty = child.get();
                aNode.getProperties().put(uProperty.getKey(), uProperty.getParams());
            }
        }
        return aNode;
    }

    @Override
    public void set(PropertySetTree<T> tree) {
        NodeGroup uRoot = new NodeGroup(tree.getRoot());
        tre.setModel(nRoot = new RTreeNode(uRoot));

        populate(nRoot, tree.getRoot());
        tre.expandAll();

        pushHierToGlobal();
        if(tree.getRoot().getChildren().isEmpty()) {
            setSelectedLabel(lblGlobal);
        } else {
            setSelectedLabel(lblHier);
        }
        GuiUtil.safe(() -> updateGlobalHier());
    }

    private void populate(RTreeNode nNode, PropertyGroup<T> node) {
        for(String key : node.getProperties().keySet()) {
            PropertyParams params = node.getProperties().get(key);
            RTreeNode nProperty = new RTreeNode(new NodeProperty(spec, key, params));
            nNode.add(nProperty);
        }
        for(PropertyGroup<T> child : node.getChildren()) {
            RTreeNode nChild = new RTreeNode(new NodeGroup(child));
            nNode.add(nChild);
            populate(nChild, child);
        }
    }

    private RTreeNode getEnclosingGroupNode() {
        RTreeNode nSel = tre.getTSelectionNode();
        if(nSel == null || nSel.get() instanceof NodeGroup) {
            return nSel;
        }
        return nSel.getRParent();
    }
    private String getGroupLabel(RTreeNode nGroup) {
        String groupLabel = ((NodeGroup) nGroup.get()).getGroup().getLabel();
        if(groupLabel == null) {
            groupLabel = "(Group)";
        }
        return groupLabel;
    }

    private void doAddGroup() {
        RTreeNode nGroup = getEnclosingGroupNode();
        if(nGroup == null) {
            return;
        }

        PropertyGroup<T> node = new PropertyGroup<>();
        node.setCriteria(blankCriteria);
        GroupEditDialog dlg = new GroupEditDialog(getWindow(), node, true, pnlCriteria);
        dlg.setVisible(true);
        if(dlg.getResult() == GroupEditDialog.SAVE) {
            try {
                RTreeNode nNew = nGroup.add(new NodeGroup(dlg.getGroup()));
                tre.expand(nGroup);
                tre.select(nNew);
            } catch(Exception e) {
                Dialogs.showDetails(getWindow(), "An error has occurred adding this subgroup.", "Error", e);
            }
        }
    }

    private void doAddProperty() {
        RTreeNode nGroup = getEnclosingGroupNode();
        if(nGroup == null) {
            return;
        }

        Map<String, PropertyParams> usedSlots = findUsedSlots(nGroup);

        if(usedSlots.size() == spec.getKeys().size()) {
            Dialogs.showMessage(getWindow(),
                "This group has already added all possible properties.  " +
                "You may choose to edit the existing ones instead.", "Information");
        } else {
            IndividualPropertyParamsAddDialog dlg =
                new IndividualPropertyParamsAddDialog(
                    getWindow(), spec, usedSlots, getGroupLabel(nGroup));
            dlg.setVisible(true);
            if(dlg.getResult() == IndividualPropertyParamsAddDialog.SAVE) {
                NodeProperty uProperty = new NodeProperty(spec, dlg.getKey(), dlg.getParams());
                int index = nGroup.indexBefore(NodeGroup.class);
                RTreeNode nNew = nGroup.insert(uProperty, index);
                tre.expand(nGroup);
                tre.select(nNew);
            }
        }
    }

    private Map<String, PropertyParams> findUsedSlots(RTreeNode nGroup) {
        Map<String, PropertyParams> usedSlots = new HashMap<>();
        for(RTreeNode child : nGroup) {
            if(child.get() instanceof NodeProperty) {
                NodeProperty uProperty = child.get();
                usedSlots.put(uProperty.getKey(), uProperty.getParams());
            }
        }
        return usedSlots;
    }

    private void doEdit() {
        RTreeNode nSel = tre.getTSelectionNode();
        if(nSel == null) {
            return;
        }
        RTreeNode nGroup = getEnclosingGroupNode();

        if(nSel.get() instanceof NodeGroup) {
            NodeGroup uNode = nSel.get();
            GroupEditDialog dlg = new GroupEditDialog(getWindow(), uNode.getGroup(), false, pnlCriteria);
            dlg.setVisible(true);
            if(dlg.getResult() == GroupEditDialog.SAVE) {
                uNode.setGroup(dlg.getGroup());
                tre.updateUI();
            }

        } else if(nSel.get() instanceof NodeProperty) {
            NodeProperty uProperty = nSel.get();
            IndividualPropertyParamsEditDialog dlg =
                new IndividualPropertyParamsEditDialog(
                    getWindow(), spec, uProperty.getKey(), uProperty.getParams(), getGroupLabel(nGroup));
            dlg.setVisible(true);
            if(dlg.getResult() == IndividualPropertyParamsEditDialog.SAVE) {
                uProperty.setParams(dlg.getParams());
                tre.updateUI();
            }
        }
    }

    private void doEditAll() {
        RTreeNode nGroup = getEnclosingGroupNode();
        if(nGroup == null) {
            return;
        }

        Map<String, PropertyParams> usedSlots = findUsedSlots(nGroup);

        GroupPropertyParamsAddEditDialog dlg =
            new GroupPropertyParamsAddEditDialog(
                getWindow(), spec, usedSlots, getGroupLabel(nGroup));
        dlg.setVisible(true);
        if(dlg.getResult() == GroupPropertyParamsAddEditDialog.SAVE) {
            PropertySet selectedProperties = dlg.getSelectedProperties();
            populateTreeNodeWithProperties(nGroup, selectedProperties);
        }
    }

    private void populateTreeNodeWithProperties(RTreeNode nGroup, PropertySet properties) {
        nGroup.removeAll(NodeProperty.class);
        for(String key : properties.keySet()) {
            PropertyParams params = properties.get(key);
            NodeProperty uProperty = new NodeProperty(spec, key, params);
            int index = nGroup.indexBefore(NodeGroup.class);
            nGroup.insert(uProperty, index);
        }
        tre.expand(nGroup);
        tre.select(nGroup);
    }

    private void doDelete() {
        RTreeNode nSel = tre.getTSelectionNode();
        if(nSel == null || nSel.isRoot()) {
            return;
        }
        tre.remove(nSel);   // Doesn't always properly select afterwards
    }

    @Override
    public void validateInput(ValidationContext context) {
        NodeGroup uGroup = nRoot.get();
        PropertyGroup group = uGroup.getGroup();
        String label = group.getLabel();
        if(lblSelectedAction == lblGlobal) {
            context.check(pnlGlobal);
            if(!context.hasError()) {
                PropertySetTree<T> tree = get();
                context.connect("Root Group" + StringUtil.suffixIf(label, ": "), tree.validate(spec));
                if(!pnlGlobal.getRootNode().getChildren().isEmpty()) {
                    context.warn("Since a global property set is selected, all property subgroups will be discarded");
                }
            }
        } else {
            PropertySetTree<T> tree = get();
            context.connect("Root Group" + StringUtil.suffixIf(label, ": "), tree.validate(spec));
            PropertyGroup<T> rootNode = convert(tre.getRoot());
            Set<String> rootKeys = rootNode.getProperties().keySet();
            Set<String> specKeys = spec.getKeys();
            if(!rootKeys.equals(specKeys)) {
                context.warn(
                    "When using a hierarchical property set, it is recommended that default values " +
                    "for all properties are provided in the root (All) group.  This ensures that " +
                    "regardless which subgroup, if any, a target ultimately corresponds to, a " +
                    "fully-satisfied property set is always produced.");
            }
        }
    }

//    public void testValid() {   // Can be uncommented and invoked by a new button for panel testing
//        PropertySetTree<T> tree = get();   // Tests tree construction works
//        set(tree);                         // Tests tree is placed back into panel just how it was
//
//        ValidationContext context = validatePanel();
//        if(context.hasMessage()) {
//            ValidationCheckDialog dlg = new ValidationCheckDialog(
//                getWindow(), context, false, "Property Tree", null);
//            dlg.setVisible(true);
//        }
//    }
}
