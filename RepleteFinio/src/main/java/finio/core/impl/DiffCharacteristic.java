package finio.core.impl;

import finio.core.impl.NonTerminalDiffUtil.SimilarityLevel;
import replete.text.StringUtil;

public class DiffCharacteristic {
    SimilarityLevel level;
    String message;
    public DiffCharacteristic(SimilarityLevel level) {
        this(level, null);
    }
    public DiffCharacteristic(SimilarityLevel level, String message) {
        this.level = level;
        this.message = message;
    }
    @Override
    public String toString() {
        return "[" + level + "]" + StringUtil.suffixIf(message);
    }
}
