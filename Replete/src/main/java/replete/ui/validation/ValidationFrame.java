package replete.ui.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import replete.text.StringUtil;

public class ValidationFrame implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    protected Map<String, ValidationFrame> children = new LinkedHashMap<>();
    protected List<ValidationMessage> messages = new ArrayList<>();


    ///////////////
    // ACCESSORS //
    ///////////////

    public Map<String, ValidationFrame> getChildren() {
        return children;
    }
    public List<ValidationMessage> getMessages() {
        return messages;
    }

    // Computed

    public boolean hasInfo() {
        return hasMessage(true, false, false);
    }
    public boolean hasWarning() {
        return hasMessage(false, true, false);
    }
    public boolean hasError() {
        return hasMessage(false, false, true);
    }
    public boolean hasMessage(boolean info, boolean warn, boolean error) {
        for(ValidationMessage msg : messages) {
            if(info && msg.getType().getLevel() == MessageLevel.INFO      ||
                    warn && msg.getType().getLevel() == MessageLevel.WARN ||
                    error && msg.getType().getLevel() == MessageLevel.ERROR) {
                return true;
            }
        }
        for(ValidationFrame child : children.values()) {
            if(child.hasMessage(info, warn, error)) {
                return true;
            }
        }
        return false;
    }
    public String getFirstErrorMessage() {
        for(ValidationMessage msg : messages) {
            if(msg.getType().getLevel() == MessageLevel.ERROR) {
                return msg.getReason() + (msg.getEvidence() != null ?
                    " {" + msg.getEvidence() + "}" : "");
            }
        }
        for(String key : children.keySet()) {
            ValidationFrame child = children.get(key);
            String err = child.getFirstErrorMessage();
            if(err != null) {
                return key + " > " + err;
            }
        }
        return null;
    }
    public int[] getMessageCount() {
        return getMessageCountInternal();
    }
    private int[] getMessageCountInternal() {
        int[] counts = new int[3];
        for(ValidationMessage msg : messages) {
            if(msg.getType().getLevel() == MessageLevel.INFO) {
                counts[0]++;
            } else if(msg.getType().getLevel() == MessageLevel.WARN) {
                counts[1]++;
            } else {
                counts[2]++;
            }
        }
        for(String key : children.keySet()) {
            ValidationFrame child = children.get(key);
            int[] childCounts = child.getMessageCountInternal();
            counts[0] += childCounts[0];
            counts[1] += childCounts[1];
            counts[2] += childCounts[2];
        }
        return counts;
    }


    //////////
    // MISC //
    //////////

    public void overlay(ValidationFrame other) {
        messages.addAll(other.messages);
        for(String key : other.children.keySet()) {
            ValidationFrame otherChild = other.children.get(key);
            if(!children.containsKey(key)) {
                children.put(key, otherChild);
            } else {
                children.get(key).overlay(otherChild);
            }
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return toString(false);
    }

    // Related Accessors (Computed)

    public String toString(boolean includeEmpty) {
        StringBuilder buffer = new StringBuilder();
        toStringInternal(0, includeEmpty, buffer);
        return buffer.toString().trim();
    }

    // This method parallels ValidationContextPanel.populate()
    private boolean toStringInternal(int level, boolean includeEmpty, StringBuilder buffer) {
        boolean hasContent = false;
        String sp = StringUtil.spaces(level * 4);
        String sp2 = StringUtil.spaces((level + 1) * 4);

        if(!messages.isEmpty()) {
            for(ValidationMessage msg : messages) {
                buffer.append(sp + msg + "\n");    // toString here doesn't include exception
                if(msg.getException() != null) {
                    buffer.append(sp2 + "{EXCEPTION} " + msg.getException() + "\n");
                }
                hasContent = true;
            }
        }

        if(!children.isEmpty()) {
            for(String s : children.keySet()) {
                String childLabel = sp + s + " =>\n";
                buffer.append(childLabel);
                boolean childHasContent =
                    children.get(s).toStringInternal(level + 1, includeEmpty, buffer);
                if(includeEmpty || childHasContent) {
                    // Leave above appended label intact
                } else {
                    int len = buffer.length();
                    buffer.delete(len - childLabel.length(), len);
                }
                hasContent = hasContent || childHasContent;
            }
        }

        return hasContent;
    }

    public void transform(ValidationFrameTransformOptions options) {
        for(int i = messages.size() - 1; i >= 0; i--) {
            ValidationMessage msg = messages.get(i);
            if(!options.getKeepLevels().contains(msg.getType().getLevel())) {
                messages.remove(i);
                continue;
            }
            if(options.getTransformer() != null) {
                messages.set(i, options.getTransformer().transform(msg));
            }
        }

        List<String> removeMe = new ArrayList<>();
        for(String name : children.keySet()) {
            ValidationFrame childFrame = children.get(name);
            childFrame.transform(options);
            if(options.isRemoveEmpty() && childFrame.messages.isEmpty() && childFrame.children.isEmpty()) {
                removeMe.add(name);
            }
        }

        for(String name : removeMe) {
            children.remove(name);
        }
    }

    public Map<String, ValidationMessage> find(ValidationFrameFindOptions options) {
        Map<String, ValidationMessage> results = new LinkedHashMap<>();
        find("", options, results);
        return results;
    }
    public void find(String path, ValidationFrameFindOptions options, Map<String, ValidationMessage> results) {
        for(int i = messages.size() - 1; i >= 0; i--) {
            ValidationMessage msg = messages.get(i);
            if(options.getCriteria() != null && options.getCriteria().test(msg)) {
                results.put(path + "/msg" + i, msg);
            }
        }

        for(String name : children.keySet()) {
            ValidationFrame childFrame = children.get(name);
            String newPath = StringUtil.isBlank(path) ? name : path + "/" + name;
            childFrame.find(newPath, options, results);
        }
    }
}
