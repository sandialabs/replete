package replete.ui.params.hier.test;

import javax.swing.JTextField;

import replete.params.hier.Criteria;
import replete.params.hier.PropertyGroup;
import replete.params.hier.PropertySetSpecification;
import replete.params.hier.PropertySetTree;
import replete.plugins.PluginManager;
import replete.plugins.RepletePlugin;
import replete.ui.BeanPanel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.params.hier.PropertySetTreePanel;
import replete.ui.validation.ValidationContext;

public class TestFrame {


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        PluginManager.initialize(RepletePlugin.class);
        ValidationContext context = PluginManager.getInitializationResults().getValidationContext();
        if(context.hasMessage()) {
            System.out.println(context);
        }

        PropertySetSpecification spec = TestPropertyParams.createTestSpec();
        PropertySetTreePanel pnl;
        TestUrlCriteriaPanel pnlCriteria = new TestUrlCriteriaPanel();
        Criteria<String> blankCriteria = new TestUrlCriteria("*");

        Lay.BLtg(Lay.fr("Property Tree Test Frame"),
            "C", pnl = new PropertySetTreePanel(spec, new UrlPanel(), pnlCriteria, blankCriteria),
            "size=600,center,visible=true"
        );

        PropertySetTree<String> propTree = createTestTree();
        pnl.set(propTree);
    }

    public static PropertySetTree createTestTree() {
        PropertyGroup<String> root = new PropertyGroup<String>("(All)")
            .setCriteria(new TestUrlCriteria("*"))
            .addProperty("key1", new TestPropertyParams().setValue(false))
            .addProperty("key2", new TestPropertyParams().setValue(true))
        ;

        PropertyGroup<String> child1 = new PropertyGroup<String>()
            .setCriteria(new TestUrlCriteria("url=*a*"))
            .addProperty("key1", new TestPropertyParams().setValue(true))
            .addProperty("key2", new TestPropertyParams().setValue(false))
        ;
        root.getChildren().add(child1);

        PropertyGroup<String> child2 = new PropertyGroup<String>()
            .setCriteria(new TestUrlCriteria("url=*b*"))
            .addProperty("key1", new TestPropertyParams().setValue(true))
            .addProperty("key2", new TestPropertyParams().setValue(false))
        ;
        root.getChildren().add(child2);

        PropertyGroup<String> grandchild1 = new PropertyGroup<String>()
            .setCriteria(new TestUrlCriteria("url=*aa*"))
            .addProperty("key1", new TestPropertyParams().setValue(false))
        ;
        child1.getChildren().add(grandchild1);

        return new PropertySetTree<>(root);
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private static class UrlPanel extends BeanPanel<String> {
        JTextField txt;
        public UrlPanel() {
            Lay.BLtg(this,
                "N", Lay.BL(
                    "W", Lay.lb("URL:", CommonConcepts.INTERNET, "eb=5r"),
                    "C", txt = Lay.tx("", 10, "selectall,prefh=30"),
                    "eb=5"
                )
            );
        }
        @Override
        public String get() {
            return txt.getText();
        }
    }

}
