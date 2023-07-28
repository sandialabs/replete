package replete.ui.diff;

import replete.diff.DiffResult;
import replete.diff.Importance;
import replete.diff.ListMapComparison;
import replete.diff.ObjectComparison;

public class TreeTestData {
    public static DiffResult createTestData1() {

        ListMapComparison subSubDiff = new ListMapComparison();
        subSubDiff.addDifference(false, "Same", (String) null); //TODO should be green
        subSubDiff.addDifference(true, "Sub sub Difference 1", "Left was CCCC but right was DDDD");
        subSubDiff.addDifference(true, "Sub sub Difference 2", "Left was AAAA but right was EEEE");

        ListMapComparison emptyDiff = new ListMapComparison();
        ListMapComparison fullNonDiff = new ListMapComparison();

        fullNonDiff.addDifference(false, "Same 1", (String) null);
        fullNonDiff.addDifference(false, "Same 2", (String) null);
        fullNonDiff.addDifference(false, "Same 3", (String) null);

        ListMapComparison subDiff = new ListMapComparison();
        subDiff.addDifference(true, "Sub Difference 1", "Left was 1234 but right was abcd");
        subDiff.addDifference(true, "Sub Difference 2", "Left was efgh but right was 5678");
        subDiff.addDifference("Sub Sub difference", subSubDiff);

        ObjectComparison objectDifference = new ObjectComparison();
        objectDifference.addDifference(true, Importance.LOW, "Difference 1", "left 'Difference 1' was #### but right 'Difference 1' was ****", "####", "****");
        objectDifference.addDifference(true, Importance.MEDIUM, "Difference 2", "left 'Difference 2' was %%%% but right 'Difference 2' was $$$$", "%%%%", "$$$$");
        objectDifference.addDifference("Sub Difference", subDiff);
        objectDifference.addDifference("Empty ListMap Diff", emptyDiff);
        objectDifference.addDifference("Non diff", fullNonDiff);

        DiffResult result = new DiffResult();
        result.setComparison(objectDifference);

        return result;
    }

    public static DiffResult createTestData2() {

        ListMapComparison subSubDiff = new ListMapComparison();
        subSubDiff.addDifference(false, "Same", (String) null); //TODO should be green
        subSubDiff.addDifference(true, "Sub sub Difference A", "Left was 1111 but right was 2222");
        subSubDiff.addDifference(true, "Sub sub Difference B", "Left was 3333 but right was 4444");

        ListMapComparison emptyDiff = new ListMapComparison();
        ListMapComparison fullNonDiff = new ListMapComparison();

        fullNonDiff.addDifference(false, "Sghdghsghs", (String) null);
        fullNonDiff.addDifference(false, "Same 2", (String) null);
        fullNonDiff.addDifference(false, "Same 3", (String) null);

        ListMapComparison subDiff = new ListMapComparison();
        subDiff.addDifference(true, "Sub Difference 1", "Left was 1234 but right was abcd");
        subDiff.addDifference(true, "Sub Difference 2", "Left was efgh but right was 5678");
        subDiff.addDifference("Sub Sub difference", subSubDiff);

        ObjectComparison objectDifference = new ObjectComparison();
        objectDifference.addDifference(true, Importance.LOW, "Difference 1", "Different", "####", "****");
        objectDifference.addDifference(true, Importance.MEDIUM, "Difference 2", "left 'Difference 2' was %%%% but right 'Difference 2' was $$$$", "%%%%", "$$$$");
        objectDifference.addDifference("Sub Difference", subDiff);
        objectDifference.addDifference("Empty ListMap Diff", emptyDiff);
        objectDifference.addDifference("Non diff", fullNonDiff);

        DiffResult result = new DiffResult();
        result.setComparison(objectDifference);

        return result;
    }
}
