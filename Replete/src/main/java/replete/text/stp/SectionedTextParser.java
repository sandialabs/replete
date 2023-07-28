package replete.text.stp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import replete.text.StringUtil;

public class SectionedTextParser {


    ////////////
    // FIELDS //
    ////////////

    private List<State> states = new ArrayList<>();
    private List<Transition> transitions = new ArrayList<>();
    private State currentState = State.START;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    // Mutators

    public SectionedTextParser addState(String name, String startPattern) {
        addState(name, startPattern, null);
        return this;
    }
    public SectionedTextParser addState(String name, String startPattern, Consumer<String> receiver) {
        states.add(new State(name, startPattern, receiver));
        return this;
    }
    public SectionedTextParser addTransition(StateCriteria prev, StateCriteria next, TransitionListener listener) {
        transitions.add(new Transition(prev, next, listener));
        return this;
    }

    public void parse(BufferedReader reader) throws IOException {
        String line;
        while((line = reader.readLine()) != null) {
            boolean nextLine = false;
            for(State state : states) {
                if(state.getStartPattern() != null) {
                    if(StringUtil.matches(line, state.getStartPattern(), true)) {
                        transitionTo(state, line);
                        nextLine = true;
                        break;
                    }
                }
            }
            if(nextLine) {
                continue;
            }
            if(currentState.getReceiver() != null) {
                currentState.getReceiver().accept(line);
            }
        }
        transitionTo(State.END, null);
    }

    private void transitionTo(State state, String line) {
        for(Transition tr : transitions) {
            if(tr.getPreviousStateCriteria().test(currentState) &&
                    tr.getNextStateCriteria().test(state)) {
                String[] captures =
                    line != null && state.getStartPattern() != null ?
                    StringUtil.extractCaptures(line, state.getStartPattern()) :
                    null;
                TransitionListener listener = tr.getListener();
                listener.stateChanged(currentState, state, line, captures);
            }
        }
        currentState = state;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        SectionedTextParser parser = new SectionedTextParser();
        parser.addState("project", "^.INFO. ---+< ([^:]+):([^ ]+) >---+$");
        parser.addState("goal", "^.INFO. --- ([^:]+):[^:]+:([^ ]+) .*$");
        parser.addState("end", "^.INFO. -+$");
        parser.addTransition(StateCriteria.ANY, StateCriteria.ANY, //new SingleStateCriteria("project"),
            (p, n, line, captures) -> {
                System.out.println(p + " -> " + n + ": captures=" + Arrays.toString(captures));
            }
        );

        File file = new File("C:\\Users\\dtrumbo\\work\\eclipse-2\\MavenUtils\\all.txt");
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
            parser.parse(reader);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
