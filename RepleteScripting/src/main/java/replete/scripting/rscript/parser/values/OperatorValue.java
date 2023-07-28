package replete.scripting.rscript.parser.values;

import java.util.ArrayList;
import java.util.List;

import replete.scripting.rscript.evaluation.functions.Associativity;
import replete.scripting.rscript.parser.gen.ASTOperator;
import replete.scripting.rscript.parser.values.oprend.BinaryOperatorValueRenderer;
import replete.scripting.rscript.parser.values.oprend.IndexOperatorValueRenderer;
import replete.scripting.rscript.parser.values.oprend.OperatorValueRenderer;
import replete.scripting.rscript.parser.values.oprend.UnaryOperatorValueRenderer;
import replete.scripting.rscript.rendering.ASTRenderingContext;

public class OperatorValue extends NodeValue {


    ////////////////////////////
    // OPERATOR VALUE CATALOG //
    ////////////////////////////

    public static final OperatorValue ASSIGN_NORMAL   = new OperatorValue(2, "=");
    public static final OperatorValue ASSIGN_PLUS     = new OperatorValue(2, "+=");
    public static final OperatorValue ASSIGN_MINUS    = new OperatorValue(2, "-=");
    public static final OperatorValue ASTERISK_ASSIGN = new OperatorValue(2, "*=");
    public static final OperatorValue FSLASH_ASSIGN   = new OperatorValue(2, "/=");
    public static final OperatorValue PERCENT_ASSIGN  = new OperatorValue(2, "%=");
    public static final OperatorValue EXPON_ASSIGN    = new OperatorValue(2, "**=");

    public static final OperatorValue LOGICAL_OR  = new OperatorValue(2, "||");
    public static final OperatorValue LOGICAL_AND = new OperatorValue(2, "&&");

    public static final OperatorValue EQUALITY_EQ_OP = new OperatorValue(2, "==");
    public static final OperatorValue EQUALITY_NE_OP = new OperatorValue(2, "!=");

    public static final OperatorValue RELATIONAL_LT_OP = new OperatorValue(2, "<");
    public static final OperatorValue RELATIONAL_LE_OP = new OperatorValue(2, "<=");
    public static final OperatorValue RELATIONAL_GT_OP = new OperatorValue(2, ">");
    public static final OperatorValue RELATIONAL_GE_OP = new OperatorValue(2, ">=");

    public static final OperatorValue ADD      = new OperatorValue(2, "+");
    public static final OperatorValue SUBTRACT = new OperatorValue(2, "-");
    public static final OperatorValue MULTIPLY = new OperatorValue(2, "*");
    public static final OperatorValue DIVIDE   = new OperatorValue(2, "/");
    public static final OperatorValue MODULUS  = new OperatorValue(2, "%");
    public static final OperatorValue EXPON    = new OperatorValue(2, "**");

    public static final OperatorValue UPLUS  = new OperatorValue(1, "+", new UnaryOperatorValueRenderer());
    public static final OperatorValue UMINUS = new OperatorValue(1, "-", new UnaryOperatorValueRenderer());
    public static final OperatorValue LNOT   = new OperatorValue(1, "!", new UnaryOperatorValueRenderer());
    public static final OperatorValue BNOT   = new OperatorValue(1, "~", new UnaryOperatorValueRenderer());

    public static final OperatorValue DOT   = new OperatorValue(2, ".");
    public static final OperatorValue INDEX = new OperatorValue(2, "[]", new IndexOperatorValueRenderer());


    ////////////
    // FIELDS //
    ////////////

    private int operandCount;
    private String image;
    private OperatorValueRenderer renderer;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public OperatorValue(int operandCount, String image) {
        this(operandCount, image, new BinaryOperatorValueRenderer());
    }
    public OperatorValue(int operandCount, String image, OperatorValueRenderer renderer) {
        this.operandCount = operandCount;
        this.image = image;
        this.renderer = renderer;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getOperandCount() {
        return operandCount;
    }
    public String getImage() {
        return image;
    }


    ///////////////
    // RENDERING //
    ///////////////

    public void render(ASTOperator node, ASTRenderingContext context) {
        renderer.render(node, context);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return image;
    }


    ////////////////////////////////
    // PRECEDENCE & ASSOCIATIVITY //
    ////////////////////////////////

    private static PrecedenceManager precedenceMgr = new PrecedenceManager();

    public static int getPrecedenceLevel(OperatorValue op) {
        return precedenceMgr.getLevel(op);
    }
    public static Associativity getAssociativity(OperatorValue op) {
        return precedenceMgr.getAssociativity(op);
    }

    static {
        precedenceMgr
            .addLevel(Associativity.RIGHT_TO_LEFT, ASSIGN_NORMAL, ASSIGN_PLUS, ASSIGN_MINUS, ASTERISK_ASSIGN, FSLASH_ASSIGN, PERCENT_ASSIGN, EXPON_ASSIGN)
            .addLevel(Associativity.LEFT_TO_RIGHT, LOGICAL_OR)
            .addLevel(Associativity.LEFT_TO_RIGHT, LOGICAL_AND)
            .addLevel(Associativity.LEFT_TO_RIGHT, EQUALITY_EQ_OP, EQUALITY_NE_OP)
            .addLevel(Associativity.LEFT_TO_RIGHT, RELATIONAL_LT_OP, RELATIONAL_LE_OP, RELATIONAL_GT_OP, RELATIONAL_GE_OP)
            .addLevel(Associativity.LEFT_TO_RIGHT, ADD, SUBTRACT)
            .addLevel(Associativity.LEFT_TO_RIGHT, MULTIPLY, DIVIDE, MODULUS)
            .addLevel(Associativity.RIGHT_TO_LEFT, UPLUS, UMINUS, LNOT, BNOT)
            .addLevel(Associativity.RIGHT_TO_LEFT, EXPON)
            // <UNITS PRECEDENCE LEVEL HERE>
            .addLevel(Associativity.LEFT_TO_RIGHT, DOT, INDEX);
    }

    private static class PrecedenceManager {

        private List<List<OperatorValue>> levels = new ArrayList<>();
        private List<Associativity> levelAssociativity = new ArrayList<>();

        public PrecedenceManager addLevel(Associativity associativity, OperatorValue... operators) {
            List<OperatorValue> level = new ArrayList<>();
            for(OperatorValue op : operators) {
                level.add(op);
            }
            levels.add(level);
            levelAssociativity.add(associativity);
            return this;
        }
        public int getLevel(OperatorValue op) {
            for(int lvl = 0; lvl < levels.size(); lvl++) {
                if(levels.get(lvl).contains(op)) {
                    return lvl;
                }
            }
            throw new IllegalArgumentException("operator not found");
        }
        public Associativity getAssociativity(OperatorValue op) {
            for(int lvl = 0; lvl < levels.size(); lvl++) {
                if(levels.get(lvl).contains(op)) {
                    return levelAssociativity.get(lvl);
                }
            }
            throw new IllegalArgumentException("operator not found");
        }
    }
}
