package replete.io.fast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;

import replete.cli.ConsoleUtil;
import replete.numbers.BitUtil;

@Ignore
public class FastObjectOutputStreamTest implements FastObjectStreamConstants {

//    @Test
//    public void multiTest() throws Exception {
//    }

    public static void main(String[] args) throws Exception {
        int A = Rand.randInt(100);
        Integer[][] array = new Integer[A][];
        for(int i = 0; i < A; i++) {
            int B = Rand.randInt(100);
            array[i] = new Integer[B];
            for(int j = 0; j < B; j++) {
                array[i][j] = Rand.randInt();
            }
        }

        int size = 10;
        Example2[][] ex = new Example2[size][];
        for(int e = 0; e < size; e++) {
            ex[e] = new Example2[size];
            for(int f = 0; f < size; f++) {
                ex[e][f] = new Example2();
            }
        }

        Example4 ex4 = new Example4();

        int[] intArr = new int[Rand.randInt(1000)];
        int[][] intArr2 = {intArr, intArr};

        Object[] primitiveArraysArray = new Object[] {
            new boolean[] {false, false, true, false, false},
            new short[] {12, 54, 5726},
            new long[] {2, 4,5 ,6 -223, -123123},
            new float[] {1, 4, 5, 7, 4, 2},
            new char[] {'a', '/', 'b', '^', 12},
        };

        int arraySize = 100000;
        Object[] primitiveArraysArrayLarge = new Object[] {
            Rand.makePrimitiveArray(boolean.class, Rand.randInt(arraySize)),
            Rand.makePrimitiveArray(short.class, Rand.randInt(arraySize)),
            Rand.makePrimitiveArray(int.class, Rand.randInt(arraySize)),
            Rand.makePrimitiveArray(long.class, Rand.randInt(arraySize)),
            Rand.makePrimitiveArray(float.class, Rand.randInt(arraySize)),
            Rand.makePrimitiveArray(double.class, Rand.randInt(arraySize)),
        };

        Person marty = new Person("Marty");
        Person jimmy = new Person("Jimmy");
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("a", 1);

        List<Object> list = new ArrayList<Object>();
        for(int x = 0; x < 10000; x++) {
            list.add(123);
            list.add(null);
            list.add("asdlfkj");
            list.add(123.343);
            list.add(jimmy);
            list.add(map);
        }
ConsoleUtil.getLine();
        FastTest[] tests = new FastTest[] {
            new FastTest(new File("C:\\x")),
            new FastTest(map),
            new FastTest(list),
            new FastTest(primitiveArraysArray),
            new FastTest(primitiveArraysArrayLarge),
            new FastTest(intArr2),
            new FastTest(ex),
            new FastTest(ex4),
            new FastTest(new Example2()),  // why does the Fast write take longer than Java write
            new FastTest(array),
            new FastTest(null),
            new FastTest(true),
            new FastTest(false),
            new FastTest((byte) 17),
            new FastTest((short) 42),
            new FastTest(127),
            new FastTest(1003L),
            new FastTest(6532124.63F),
            new FastTest(-12309421098.341123),
            new FastTest('G'),
            new FastTest("Hello World"),
            new FastTest(new Example1()),
            new FastTest(Rand.makeArray(Example1.class, 1000)),
            new FastTest(new Person[] {
                marty, marty, marty, marty, marty, marty, marty, marty,
                marty, marty, marty, marty, marty, marty, marty, marty,
                marty, marty, marty, marty, marty, marty, marty, marty,
                jimmy, jimmy, jimmy, jimmy, jimmy, jimmy, jimmy, jimmy,
                jimmy, jimmy, jimmy, jimmy, jimmy, jimmy, jimmy, jimmy,
                jimmy, jimmy, jimmy, jimmy, jimmy, jimmy, jimmy, jimmy,
            }),
            new FastTest(new Person[] {new Person("A"), new Person("B")}),
            new FastTest(Rand.randString(256))
        };

        File testFile = new File("test.bin");

        for(FastTest test : tests) {
            try {
                runTest(testFile, test);
            } catch(Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
//        FastTestReportPrinter.printReport(tests);
    }

    private static void runTest(File testFile, FastTest test) throws Exception {
//        System.out.println("Testing " + test + "...");
        long T;

        // Java Write
        T = System.currentTimeMillis();
        ObjectOutputStream out =
            new ObjectOutputStream(
                new FileOutputStream(testFile));
        out.writeObject(test.value);
        out.close();
        test.javaSize = testFile.length();
        test.javaOutTime = System.currentTimeMillis() - T;

        // Java Read
        T = System.currentTimeMillis();
        ObjectInputStream in =
            new ObjectInputStream(
                new FileInputStream(testFile));
        test.javaRead = in.readObject();
        in.close();
        test.javaInTime = System.currentTimeMillis() - T;

        // Fast Write
        T = System.currentTimeMillis();
        FastObjectOutputStream fout =
            new FastObjectOutputStream(
                new FileOutputStream(testFile));
        fout.writeObject(test.value);
        fout.close();
//            print(fout.getDebugBuffer());
//            System.out.println(fout.handles.size());
        test.fastSize = testFile.length();
        test.fastOutTime = System.currentTimeMillis() - T;

        // Fast Read
        T = System.currentTimeMillis();
        FastObjectInputStream fin =
            new FastObjectInputStream(
                new FileInputStream(testFile));
        test.fastRead = fin.readObject();
        fin.close();
        test.fastInTime = System.currentTimeMillis() - T;

//        printDebugCounts(fin.debugCounts);

        test.check();
    }

    private static void printDebugCounts(int[] debugCounts) {
        int total = 0;
        for(int i = 0; i < debugCounts.length; i++) {
            total += debugCounts[i];
        }

        List<List<Object>> values = new ArrayList<List<Object>>();
        for(int i = 0; i < debugCounts.length; i++) {
            String name = null;
            switch(i) {
                case TYPE_NULL:          name = "TYPE_NULL"; break;
                case TYPE_BOOL_TRUE:     name = "TYPE_BOOL_TRUE"; break;
                case TYPE_BOOL_FALSE:    name = "TYPE_BOOL_FALSE"; break;
                case TYPE_BYTE:          name = "TYPE_BYTE"; break;
                case TYPE_SHORT:         name = "TYPE_SHORT"; break;
                case TYPE_INTEGER:       name = "TYPE_INTEGER"; break;
                case TYPE_LONG:          name = "TYPE_LONG"; break;
                case TYPE_FLOAT:         name = "TYPE_FLOAT"; break;
                case TYPE_DOUBLE:        name = "TYPE_DOUBLE"; break;
                case TYPE_CHARACTER:     name = "TYPE_CHARACTER"; break;
                case TYPE_STRING1:       name = "TYPE_STRING1"; break;
                case TYPE_STRING2:       name = "TYPE_STRING2"; break;
                case TYPE_STRING4:       name = "TYPE_STRING4"; break;
                case TYPE_OBJECT:        name = "TYPE_OBJECT"; break;
                case TYPE_OBJECT_CX:     name = "TYPE_OBJECT_CX"; break;
                case TYPE_OBJECT_REF:    name = "TYPE_OBJECT_REF"; break;
                case TYPE_ARRAY:         name = "TYPE_ARRAY"; break;
                case TYPE_ARRAY_CX:      name = "TYPE_ARRAY_CX"; break;
                case TYPE_ARRAY_PRIM:    name = "TYPE_ARRAY_PRIM"; break;
                case TYPE_ARRAY_PRIM_CX: name = "TYPE_ARRAY_PRIM_CX"; break;
            }
            int count = debugCounts[i];
            double pct = ((double) count / total) * 100.0;
            String pctS = String.format("%.1f%%", pct);

            List<Object> rowValues = new ArrayList<Object>();
            rowValues.add(name);
            rowValues.add(count);
            rowValues.add(pctS);
            values.add(rowValues);
        }

        List<Object> rowValues = new ArrayList<Object>();
        rowValues.add("Total");
        rowValues.add(total);
        rowValues.add("100%");
        values.add(rowValues);

        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("Type Code", 's', true));
        columns.add(new Column("Count", 's', false));
        columns.add(new Column("% of Total", 's', false));

        ReportPrinter.printReport("Type Code Distribution", columns, values);
    }


    //    @Test
//    public void nullValue() throws IOException {
//        ByteArrayOutputStream bout = new ByteArrayOutputStream(10);
//        FastObjectOutputStream fout = new FastObjectOutputStream(bout);
//        fout.writeObject(Rand.randString(255));
//        fout.close();
//        print(bout.toByteArray());
//
//        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
//        FastObjectInputStream fin = new FastObjectInputStream(bin);
//        try {
//            Object obj = fin.readOject();
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//    }
//
    private static void print(byte[] buffer) {
        System.out.print(buffer.length + "#");
        for(int b = 0; b < buffer.length; b++) {
            byte by = buffer[b];
            System.out.print(
                BitUtil.markupBinaryString(
                    BitUtil.toBinaryString(by)));
            System.out.print("(" + by);
            if(by >= 32 && by <= 126) {
                System.out.print(";" + (char) by);
            }
            System.out.print(")");
            if(b != buffer.length - 1) {
                System.out.print("|");
            }
        }
        System.out.println();
    }
}