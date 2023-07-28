package replete.ui.params.hier.test;

import replete.params.hier.PropertyGroup;
import replete.params.hier.PropertyParams;
import replete.params.hier.PropertySetSpecification;
import replete.params.hier.PropertySetTree;
import replete.params.hier.PropertySlot;
import replete.text.StringUtil;
import replete.ui.images.concepts.CommonConcepts;

public class TestPropertyParams extends PropertyParams {


    ////////////
    // FIELDS //
    ////////////

    private boolean value;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isValue() {
        return value;
    }

    // Mutators

    public TestPropertyParams setValue(boolean value) {
        this.value = value;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (value ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(!super.equals(obj)) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        TestPropertyParams other = (TestPropertyParams) obj;
        if(value != other.value) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Value: " + StringUtil.yesNo(value);
    }


    //////////
    // TEST //
    //////////

    public static PropertySetSpecification createTestSpec() {
        return new PropertySetSpecification()
            .add(
                new PropertySlot()
                    .setKey("key1")
                    .setName("The Key")
                    .setDescription("Some description of the param slot. x x x x xxx xxx  x xx x xx x xx x x")
                    .setParamsClass(TestPropertyParams.class)
            )
            .add(
                new PropertySlot()
                    .setKey("key2")
                    .setName("The Other Key")
                    .setDescription(StringUtil.createMissingText())
                    .setIcon(CommonConcepts._PLACEHOLDER)
                    .setParamsClass(TestPropertyParams.class)
            )
        ;
    }

    public static PropertySetTree createTestTree() {
        PropertyGroup<String> root = new PropertyGroup<String>("(All)")
            .setCriteria(obj -> true)
            .addProperty("key1", new TestPropertyParams().setValue(false))
            .addProperty("key2", new TestPropertyParams().setValue(true))
        ;

        PropertyGroup<String> child = new PropertyGroup<String>()
            .setCriteria(obj -> obj.contains("a"))
            .addProperty("key1", new TestPropertyParams().setValue(true))
            .addProperty("key2", new TestPropertyParams().setValue(false))
        ;
        root.getChildren().add(child);

        PropertyGroup<String> child2 = new PropertyGroup<String>()
            .setCriteria(obj -> obj.contains("aa"))
            .addProperty("key1", new TestPropertyParams().setValue(false))
        ;
        child.getChildren().add(child2);

        return new PropertySetTree<>(root);
    }
}
