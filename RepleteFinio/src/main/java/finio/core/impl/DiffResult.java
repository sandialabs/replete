package finio.core.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DiffResult {
    List<DiffCharacteristic> chrxs = new ArrayList<>();
    Map<Object, DiffResult> childrenResults = new LinkedHashMap<>();
}
