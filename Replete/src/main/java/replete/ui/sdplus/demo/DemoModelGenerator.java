package replete.ui.sdplus.demo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates test data that will make up a demo model
 * for the scale set panel.
 *
 * @author Derek Trumbo
 */

public class DemoModelGenerator {

    protected static List<DataElement> data;
    protected static List<DataScale> scales;

    protected static SimpleDateFormat dateF = new SimpleDateFormat("yyyy/M/d");
    protected static SimpleDateFormat dateTimeF = new SimpleDateFormat("yyyy/M/d HH:mm:ss");

    public static DemoModel generate() {

        DataScale sclFn = new DataScale("fn", "First Name", null, "The base class scale panel has no filter panel nor title count label." +
            "<br>Although the model has data for this column, the scale panel<br>was set up as a base class scale panel as<br>opposed to an enum scale panel so it shows up like this.<br>(done here just for testing/demonstration)");
        DataScale sclLn = new DataScale("ln", "Last Name", null, "Enum scale panel with 5 non-null values, 2 values the same");
        DataScale sclBd = new DataScale("bd", "Birth Date", "UTC", "Date scale panel with 5 non-null values.");
        DataScale sclTs = new DataScale("ts", "Time Stamp", "UTC", "Date scale with a full date & time date format.");
        DataScale sclAge = new DataScale("age", "Age", "Years", "Integer continuous scale only accepts integer values.");
        DataScale sclH = new DataScale("height", "Height", "Feet", "Regular continuous scale panel with 4 non-null values.");
        DataScale sclW = new DataScale("weight", "Weight", "Pounds", "Continuous scale with all 6 values.");
        DataScale sclA = new DataScale("arts", "Arts", null, "Enum scale panel without any values in the model.");
        DataScale sclL = new DataScale("length", "Length", "mm", "Continuous scale panel without any values in the model.");
        DataScale sclSt = new DataScale("st", "State", null, "Enum scale panel with neither the title counts or enum counts showing.");
        DataScale sclTO = new DataScale("to", "Test Object Panel", null, "Enum scale panel showing that arbitrary objects can<br>exist in an enum panel (below shows the toString() of each object)");
        DataScale sclZ = new DataScale("z", "Special Scale Example", null,
            "This scale shows that all the scales don't have to<br>have same number of data elements.<br>" +
            "This scale only has 4 data elements from which these unique<br>values were chosen. Most other " +
            "scales are backed by a data<br>source that contains 6 data elements - demonstrating<br>" +
            "that the ScaleSetPanel does not have to be based off <br>of a single rigid data source (" +
            "M cols x N rows)");
        DataScale sclRB = new DataScale("rb", "Radio Button Single Select Example", null,
            "This scale panel demonstrates an experimental capability in the<br>ScaleSetPanel.  It's experimental " +
            "in that it can spark some ideas<br>of what is possible with these panels as it did not exist in " +
            "the<br>previous Wave Slice &amp; Dice, not as in it's half implemented.<br>This scale panel is backed " +
            "by separate model so as not to<br>affect the selected state of other data elements.  Scale panels<br>like these " +
            "work well with the title counts and enum<br>value counts turned off.");

        scales = new ArrayList<DataScale>();
        scales.add(sclFn);
        scales.add(sclLn);
        scales.add(sclBd);
        scales.add(sclTs);
        scales.add(sclAge);
        scales.add(sclH);
        scales.add(sclW);
        scales.add(sclA);
        scales.add(sclL);
        scales.add(sclSt);
        scales.add(sclTO);
        scales.add(sclZ);
        scales.add(sclRB);

        data = new ArrayList<DataElement>();
        data.add(new DataElement());
        data.add(new DataElement());
        data.add(new DataElement());
        data.add(new DataElement());
        data.add(new DataElement());
        data.add(new DataElement());

        data.get(0).values.put(sclFn.key, "Tony");
        data.get(1).values.put(sclFn.key, "Jason");
        data.get(2).values.put(sclFn.key, "Fred");
        data.get(3).values.put(sclFn.key, "Jared");
        data.get(4).values.put(sclFn.key, "Jason");
        data.get(5).values.put(sclFn.key, "Sally");

        data.get(0).values.put(sclLn.key, "Johnson");
        data.get(1).values.put(sclLn.key, "Andersen");
        data.get(2).values.put(sclLn.key, null);
        data.get(3).values.put(sclLn.key, "Wolfe");
        data.get(4).values.put(sclLn.key, "Smith");
        data.get(5).values.put(sclLn.key, "Smith");

        try {
            data.get(0).values.put(sclBd.key, dateF.parse("1983/3/2").getTime());
            data.get(1).values.put(sclBd.key, dateF.parse("1986/6/7").getTime());
            data.get(2).values.put(sclBd.key, dateF.parse("1981/9/21").getTime());
            data.get(3).values.put(sclBd.key, null);
            data.get(4).values.put(sclBd.key, dateF.parse("1965/1/31").getTime());
            data.get(5).values.put(sclBd.key, dateF.parse("1974/12/13").getTime());

            data.get(0).values.put(sclTs.key, dateTimeF.parse("1950/2/6 09:32:03").getTime());
            data.get(1).values.put(sclTs.key, null);
            data.get(2).values.put(sclTs.key, dateTimeF.parse("1951/3/17 02:17:45").getTime());
            data.get(3).values.put(sclTs.key, dateTimeF.parse("1949/12/24 16:40:13").getTime());
            data.get(4).values.put(sclTs.key, null);
            data.get(5).values.put(sclTs.key, dateTimeF.parse("1952/6/29 22:55:55").getTime());
        } catch(Exception e) {
             e.printStackTrace();
        }

        data.get(0).values.put(sclAge.key, 27);
        data.get(1).values.put(sclAge.key, 24);
        data.get(2).values.put(sclAge.key, 12);
        data.get(3).values.put(sclAge.key, 29);
        data.get(4).values.put(sclAge.key, 40);
        data.get(5).values.put(sclAge.key, 36);

        data.get(0).values.put(sclH.key, 6.2);
        data.get(1).values.put(sclH.key, 5.9);
        data.get(2).values.put(sclH.key, null);
        data.get(3).values.put(sclH.key, 6.3);
        data.get(4).values.put(sclH.key, 5.7);
        data.get(5).values.put(sclH.key, null);

        data.get(0).values.put(sclW.key, 225.0);
        data.get(1).values.put(sclW.key, 182.2);
        data.get(2).values.put(sclW.key, 130.9);
        data.get(3).values.put(sclW.key, 190.83);
        data.get(4).values.put(sclW.key, 250.75);
        data.get(5).values.put(sclW.key, 113.23);

        data.get(0).values.put(sclSt.key, "NM");
        data.get(1).values.put(sclSt.key, "NM");
        data.get(2).values.put(sclSt.key, null);
        data.get(3).values.put(sclSt.key, "CO");
        data.get(4).values.put(sclSt.key, "CO");
        data.get(5).values.put(sclSt.key, "WY");

        data.get(0).values.put(sclSt.key, "NM");
        data.get(1).values.put(sclSt.key, "NM");
        data.get(2).values.put(sclSt.key, null);
        data.get(3).values.put(sclSt.key, "CO");
        data.get(4).values.put(sclSt.key, "CO");
        data.get(5).values.put(sclSt.key, "WY");

        // Prove that any objects can appear in an enumerated scale.
        data.get(0).values.put(sclTO.key, new TestObject(1, 1));
        data.get(1).values.put(sclTO.key, new TestObject(2, 2));
        data.get(2).values.put(sclTO.key, new TestObject(1, 1));
        data.get(3).values.put(sclTO.key, new TestObject(3, 3));
        data.get(4).values.put(sclTO.key, null);
        data.get(5).values.put(sclTO.key, null);

        String[] zSpecialValues = new String[] {"North", "East", "South", "West"};
        String[] rbSpecialValues = new String[] {"Waveforms", "Scatter Plot",
                                                 "Histogram", "Bar Plot", "Parallel Coordinate"};

        return new DemoModel(data, scales, zSpecialValues, rbSpecialValues);
    }

    // Prove that any objects can appear in an enumerated scale.
    protected static class TestObject {
        protected int x, y;

        public TestObject(int nx, int ny) {
            x = nx; y = ny;
        }

        @Override
        public String toString() {
            return "TestObject{x=" + x + ",y=" + y + "}";
        }

        @Override
        public int hashCode() {
            return  x + y;
        }

        @Override
        public boolean equals(Object o) {
            if(!(o instanceof TestObject)) {
                return false;
            }
            TestObject to = (TestObject) o;
            return x == to.x && y == to.y;
        }
    }
}
