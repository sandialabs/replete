package replete.numbers;

import java.util.Collections;
import java.util.TreeMap;

import replete.collections.RTreeMap;

public class NumUtil {


    ////////////
    // FIELDS //
    ////////////

    private final static TreeMap<Integer, String> numToLetters = new RTreeMap<>(
        1,    "I",
        4,    "IV",
        5,    "V",
        9,    "IX",
        10,   "X",
        40,   "XL",
        50,   "L",
        90,   "XC",
        100,  "C",
        400,  "CD",
        500,  "D",
        900,  "CM",
        1000, "M"
    );

    private final static TreeMap<Integer, String> reverseNumToLetters =
        new RTreeMap<>(numToLetters, Collections.reverseOrder());


    ////////////////////
    // ROMAN NUMERALS //
    ////////////////////

    public final static String toRoman(int number) {
        int key = numToLetters.floorKey(number);
        if(number == key) {
            return numToLetters.get(number);
        }
        return numToLetters.get(key) + toRoman(number - key);
    }

    public static int fromRoman(String roman) {
        roman = roman.toUpperCase();
        int number = 0;
        while(!roman.isEmpty()) {
            for(int key : reverseNumToLetters.keySet()) {
                String value = reverseNumToLetters.get(key);
                if(roman.startsWith(value)) {
                    number += key;
                    roman = roman.substring(value.length());
                    break;
                }
            }
        }
        return number;
    }


    ///////////////////
    // TYPE CHECKING //
    ///////////////////

    public static boolean isBoolean(String s) {
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false");
    }
    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    public static boolean isLong(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    public static boolean isFloat(String s) {
        try {
            Float.parseFloat(s);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch(Exception e) {
            return false;
        }
    }


    ////////////////
    // CONVERSION //
    ////////////////

    public static Boolean b(String s) {
        try {
            return Boolean.parseBoolean(s);
        } catch(Exception e) {
            return null;
        }
    }
    public static Integer i(String s) {
        try {
            return Integer.parseInt(s);
        } catch(Exception e) {
            return null;
        }
    }
    public static Integer i(String s, int dflt) {
        try {
            return Integer.parseInt(s);
        } catch(Exception e) {
            return dflt;
        }
    }
    public static Long l(String s) {
        try {
            return Long.parseLong(s);
        } catch(Exception e) {
            return null;
        }
    }
    public static Float f(String s) {
        try {
            return Float.parseFloat(s);
        } catch(Exception e) {
            return null;
        }
    }
    public static Double d(String s) {
        try {
            return Double.parseDouble(s);
        } catch(Exception e) {
            return null;
        }
    }

    public static String s(int i) {
        return Integer.toString(i);
    }
    public static String s(double d) {
        return Double.toString(d);
    }
    public static String s(long l) {
        return Long.toString(l);
    }
    public static String s(float f) {
        return Float.toString(f);
    }
    public static String s(boolean b) {
        return Boolean.toString(b);
    }


    /////////////
    // PERCENT //
    /////////////

    public static String pct(double ratio) {
        return "" + ((int) (1000.0 * ratio) / 10.0) + "%";
    }
    public static String pct(double numer, double denom) {
        return "" + ((int) (1000.0 * numer / denom) / 10.0) + "%";
    }
    public static String pctInt(double numer, double denom) {
        return "" + ((int) (100.0 * numer / denom)) + "%";
    }
    public static void printProg(String lbl, long increment, long n, long total) {
        if(n % increment == 0) {
            System.out.println(lbl + ": " + NumUtil.pct(n, total));
        }
    }


    //////////
    // MISC //
    //////////

    public static int[] gridIndexToRowColRowMajor(int gridWidth, int index) {
        int[] rowCol =  new int[2];
        rowCol[0] = index / gridWidth;
        rowCol[1] = index % gridWidth;
        return rowCol;
    }
    public static int[] gridIndexToRowColColMajor(int gridHeight, int index) {
        int[] rowCol =  new int[2];
        rowCol[0] = index % gridHeight;
        rowCol[1] = index / gridHeight;
        return rowCol;
    }
    public static int gridRowColToIndexRowMajor(int gridWidth, int row, int col) {
        return row * gridWidth + col;
    }
    public static int gridRowColToIndexColMajor(int gridHeight, int row, int col) {
        return col * gridHeight + row;
    }

    public static int smallestNonNegative(int... numbers) {
        int min = Integer.MAX_VALUE;
        for(int number : numbers) {
            if(number >= 0) {
                if(number < min) {
                    min = number;
                }
            }
        }
        return min;
    }
    public static boolean isEven(int num) {
        return num % 2 == 0;
    }
    public static boolean isOdd(int num) {
        return num % 2 != 0;
    }

    // https://randomascii.wordpress.com/2012/02/25/comparing-floating-point-numbers-2012-edition/
    // http://java2s.com/Book/Java/Examples/Compare_two_double_type_variables_within_epsilon.htm
    // http://grepcode.com/file/repo1.maven.org/maven2/org.apache.commons/commons-math/2.1/org/apache/commons/math/util/MathUtils.java#MathUtils.equals%28double%2Cdouble%2Cdouble%29
    // https://stackoverflow.com/questions/3281237/is-the-use-of-machine-epsilon-appropriate-for-floating-point-equality-tests/28751350#28751350
    // https://stackoverflow.com/questions/11390853/java-double-comparison
    // http://docs.oracle.com/cd/E19957-01/806-3568/ncg_goldberg.html
    public static boolean equals(double x, double y) {
        return (Double.isNaN(x) && Double.isNaN(y)) || x == y;
    }
    public static boolean equals(double x, double y, double eps) {
        return equals(x, y) || (Math.abs(y - x) <= eps);
    }
    public static int compareTo(double x, double y, double eps) {
        if (equals(x, y, eps)) {
            return 0;
        } else if (x < y) {
            return -1;
        }
        return 1;
    }

    public static int max(int... values) {
        int max = Integer.MIN_VALUE;
        for(int v : values) {
            if(v > max) {
                max = v;
            }
        }
        return max;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
//      System.out.println(numToLetters);
//      if(true) {
//          return;
//      }

        int[] numbers = {
            3977,
            1235,
            3124,
            93,
            1,
            455
        };

        for(int n : numbers) {
            System.out.println(n + "  " + toRoman(n));
        }

        for(int i = 0; i < 500_000; i++) {
            int test = RandomUtil.getRandomWithinRange(1, 11000);
            String roman = toRoman(test);
            int actual = fromRoman(roman);
            if(actual != test) {
                throw new RuntimeException("Error: " + test + " -> " + roman + " -> " + actual);
            }
        }

        System.out.println(equals(9, 9, 0.01));
        System.out.println(equals(9, 9.0001, 0.01));
        System.out.println(equals(9, 9.0001, 0.00009));
        System.out.println(equals(9, 9.0001, 0.0002));
    }
}
