package finio.extractors.jo.rules;

import java.io.Serializable;

import finio.core.KeyPath;


public abstract class Rule implements Serializable {
    public abstract boolean appliesTo(Object O, KeyPath P);
}
