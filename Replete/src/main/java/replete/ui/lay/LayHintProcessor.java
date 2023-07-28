package replete.ui.lay;

import java.awt.Component;

import replete.ui.lay.Lay.HintList;

public interface LayHintProcessor {
    void process(String value, Component cmp, HintList allHints) throws Exception;
}
