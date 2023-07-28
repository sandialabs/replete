package replete.ui.text.validating;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;

import replete.event.ChangeNotifier;
import replete.text.StringUtil;
import replete.threads.SwingTimerManager;
import replete.ui.lay.Lay;
import replete.ui.text.DocumentChangeListener;
import replete.ui.text.RTextField;
import replete.ui.validation.Validatable;
import replete.ui.validation.ValidationContext;

public class ValidatingTextField extends RTextField implements Validatable {


    ///////////
    // ENUMS //
    ///////////

    public enum State {
        UNVALIDATED,      // isValidInput() == <Unknown>
        PENDING,          // isValidInput() == true
        VALIDATED,        // isValidInput() == true
        VALID,            // isValidInput() == true
        INVALID           // isValidInput() == false
    }


    ////////////
    // FIELDS //
    ////////////

    private static final int   DEFAULT_PENDING_TIMEOUT   = 650;
    private static final int   DEFAULT_VALIDATED_TIMEOUT = 300;
    private static final Color DEFAULT_DEFAULT_COLOR     = new JTextField().getBackground();
    private static final Predicate<String> DEFAULT_UNVALIDATABLE_DECIDER = new Predicate<String>() {
        public boolean test(String t) {
            return t.isEmpty();
        }
    };
    private static final Validator DEFAULT_VALIDATOR = null;
    private static final Map<State, Color> DEFAULT_STATE_COLORS = new HashMap<>();

    private int pendingTimeout = -1;    // Set later
    private int validatedTimeout = -1;
    private Color defaultColor = DEFAULT_DEFAULT_COLOR;
    protected Predicate<String> unvalidatableDecider = DEFAULT_UNVALIDATABLE_DECIDER;
    private Validator validator = DEFAULT_VALIDATOR;
    protected Map<State, Color> stateColors;                // Color to apply after first entering state
    protected State state = null;      // Will be set in constructor/validateInput

    // Internal
    private Timer tmrPendingToValidated;
    private Timer tmrValidatedToValid;
    private boolean suppressCheck = false;
    private boolean firstValidation = true;


    ////////////////////
    // INITIALIZATION //
    ////////////////////

    static {
        DEFAULT_STATE_COLORS.put(State.UNVALIDATED, null);                    // null => Use default color
        DEFAULT_STATE_COLORS.put(State.PENDING,     null);
        DEFAULT_STATE_COLORS.put(State.VALIDATED,   Lay.clr("205,255,205"));  // Light Green
        DEFAULT_STATE_COLORS.put(State.VALID,       null);
        DEFAULT_STATE_COLORS.put(State.INVALID,     Lay.clr("255,205,205"));
    }


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ValidatingTextField() {
        super();
        init();
    }
    public ValidatingTextField(Validator validator) {
        super();
        setValidator(validator);
        init();
    }
    public ValidatingTextField(String text) {
        super(text);
        init();
    }
    public ValidatingTextField(String text, Validator validator) {
        super(text);
        setValidator(validator);
        init();
    }
    public ValidatingTextField(int columns) {
        super(columns);
        init();
    }
    public ValidatingTextField(int columns, Validator validator) {
        super(columns);
        setValidator(validator);
        init();
    }
    public ValidatingTextField(String text, int columns) {
        super(text, columns);
        init();
    }
    public ValidatingTextField(String text, int columns, Validator validator) {
        super(text, columns);
        setValidator(validator);
        init();
    }
    public ValidatingTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
        init();
    }

    protected void init() {
        stateColors = new HashMap<>(DEFAULT_STATE_COLORS);

        // This will create the various timers.
        setPendingTimeout(DEFAULT_PENDING_TIMEOUT);
        setValidatedTimeout(DEFAULT_VALIDATED_TIMEOUT);

        triggerValidation();
        firstValidation = false;

        addChangeListener(textChangedListener);

//        addActionListener(e -> {
//            if(isValidInput()) {
//                setState(State.VALIDATED);
//            }
//            // Else do nothing as will already be in invalid state.
//        });
    }

    private DocumentChangeListener textChangedListener = e -> triggerValidation();
    private DocumentChangeListener debugTextChangedListener = e -> {
        System.out.println("Text  => {" + getText() + "}");
        triggerValidation();
    };


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public int getPendingTimeout() {
        return pendingTimeout;
    }
    public int getReturnToDefaultTimeout() {
        return validatedTimeout;
    }
    public Color getDefaultColor() {
        return defaultColor;
    }
    public Predicate<String> getUnvalidatableDecider() {
        return unvalidatableDecider;
    }
    public Validator getValidator() {
        return validator;
    }
    public Color getStateColor(State state) {
        return stateColors.get(state);
    }
    public State getState() {
        return state;
    }

    // Mutators

    public void setPendingTimeout(int pendingTimeout) {
        this.pendingTimeout = pendingTimeout;

        if(tmrPendingToValidated != null) {
            tmrPendingToValidated.stop();
        }
        tmrPendingToValidated = SwingTimerManager.create(pendingTimeout, e -> setState(State.VALIDATED));
    }
    public void setValidatedTimeout(int validatedTimeout) {
        this.validatedTimeout = validatedTimeout;

        if(tmrValidatedToValid != null) {
            tmrValidatedToValid.stop();
        }
        tmrValidatedToValid = SwingTimerManager.create(validatedTimeout, e -> setState(State.VALID));
    }
    public void setDefaultColor(Color defaultColor) {
        this.defaultColor = defaultColor;
        updateBackgroundColor();
    }
    public void setUnvalidatableDecider(Predicate<String> unvalidatableDecider) {
        this.unvalidatableDecider = unvalidatableDecider;
    }
    public void setValidator(Validator validator) {
        this.validator = validator;
    }
    public void setStateColor(State state, Color color) {
        stateColors.put(state, color);
        updateBackgroundColor();
    }

    public void enableDebug() {
        removeChangeListener(textChangedListener);
        addChangeListener(debugTextChangedListener);
        addValidUnvalidatableTimeoutListener(e -> {
            if(isUnvalidatableInput()) {
                System.out.println("Unval => (" + getText() + ")");
            } else if(isValidInput()) {
                System.out.println("Valid => (" + getText() + ")");
            }
        });
        addStateListener(e -> System.out.println("State => [" + state + "]"));
        System.out.println("State => [" + state + "]");
        System.out.println("Text  => {" + getText() + "}");
        if(isUnvalidatableInput()) {
            System.out.println("Unval => (" + getText() + ")");
        } else if(isValidInput()) {
            System.out.println("Valid => (" + getText() + ")");
        }
    }

    // Internal

    protected void setState(State state) {
        boolean changedState = (this.state != state);
        this.state = state;

        // Do this regardless of changedState, in case the
        // state colors have changed.
        updateBackgroundColor();

        if(changedState || state == State.PENDING) {
            switch(state) {
                case UNVALIDATED:
                    cancel();
                    fireValidUnvalidatableTimeoutNotifier();
                    break;

                // The user has just typed something so we need to now give
                // a small window until we can confirm it's valid.
                case PENDING:
                    tmrValidatedToValid.stop();
                    tmrPendingToValidated.restart();
                    break;

                case VALIDATED:
                    tmrPendingToValidated.stop();
                    tmrValidatedToValid.restart();       // Will eventually return color from green to white.
                    fireValidUnvalidatableTimeoutNotifier();
                    break;

                case VALID:
                    cancel();
                    break;

                case INVALID:
                    cancel();
                    break;
            }
        }

        if(changedState) {
            fireStateNotifier();
        }
    }

    private void updateBackgroundColor() {
        Color newBg = stateColors.get(state);
        if(newBg == null) {
            newBg = defaultColor;
        }
        setBackground(newBg);
    }


    //////////
    // MISC //
    //////////

    public boolean isValidInput() {
        return validator == null || validator.accept(this, getText());
    }
    public boolean isUnvalidatableInput() {
        return unvalidatableDecider != null && unvalidatableDecider.test(getText());
    }

    public void setValidText(String t) {
        suppressCheck = true;
        super.setText(t);
        suppressCheck = false;
        setState(State.VALID);
    }

    public void setValidText(Object o) {
        setValidText(StringUtil.cleanNull(o));
    }

    public void triggerValidation() {    // Externally accessible in case validator's own state/logic might change the state.
        triggerValidation(false);
    }
    public void triggerValidation(boolean validatedIfValid) {    // Externally accessible in case validator's own state/logic might change the state.
        if(suppressCheck) {
            return;
        }
        if(isUnvalidatableInput()) {
            setState(State.UNVALIDATED);
        } else if(isValidInput()) {
            if(firstValidation) {
                setState(State.VALID);
            } else if(validatedIfValid) {
                setState(State.VALIDATED);
            } else {
                setState(State.PENDING);
            }
        } else {
            setState(State.INVALID);
        }
    }

    protected void cancel() {
        tmrPendingToValidated.stop();     // It's possible that one or both of these
        tmrValidatedToValid.stop();       // are already stopped, but OK to stop again.
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    protected ChangeNotifier validUnvalidatableTimeoutNotifier = new ChangeNotifier(this);
    public void addValidUnvalidatableTimeoutListener(ChangeListener listener) {
        validUnvalidatableTimeoutNotifier.addListener(listener);
    }
    protected void fireValidUnvalidatableTimeoutNotifier() {
        validUnvalidatableTimeoutNotifier.fireStateChanged();
    }

    private ChangeNotifier stateNotifier = new ChangeNotifier(this);
    public void addStateListener(ChangeListener listener) {
        stateNotifier.addListener(listener);
    }
    private void fireStateNotifier() {
        stateNotifier.fireStateChanged();
    }

    @Override
    public void validateInput(ValidationContext context) {
        String text = getText();
        if(validator instanceof ContextValidator) {
            ContextValidator eValidator =
                (ContextValidator) validator;

            /* boolean result = */ eValidator.validateInput(this, text, context);
            // The result here is unnecessary and only serves as an experiment
            // to see how clean it can make the developers who are making
            // these types of validators, now that it can help eliminate
            // a bunch of extra if statements.  This is because in Java,
            // "x() && y();" is not a valid statement by itself like it is
            // in other programming languages, but "return x() && y();" is

            if(!context.getCurrentFrame().getChildren().isEmpty()) {
                throw new IllegalStateException(
                    "No children validation contexts (via 'check()') should be added when validating the text field"
                );
            }

        } else if(validator != null) {
            if(!validator.accept(this, text)) {
                context.error(ContextValidator.INV_FMT, text);
            }
        }
    }
}
