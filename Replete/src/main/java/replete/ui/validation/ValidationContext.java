package replete.ui.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import replete.collections.Pair;

public class ValidationContext implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    private List<ValidationFrame> path = new ArrayList<>();
    private ValidationFrame rootFrame = new ValidationFrame();
    private List<Pair<Predicate<ValidationContext>, Consumer<ValidationContext>>> closeActions = new ArrayList<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ValidationContext() {
        path.add(rootFrame);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public ValidationFrame getRootFrame() {
        return rootFrame;
    }

    // Accessors (Computed)

    public ValidationFrame getCurrentFrame() {
        return path.get(path.size() - 1);
    }
    public boolean hasMessage(boolean info, boolean warn, boolean error) {
        return rootFrame.hasMessage(info, warn, error);
    }
    public boolean hasMessage() {
        return rootFrame.hasMessage(true, true, true);
    }
    public boolean hasInfo() {
        return rootFrame.hasMessage(true, false, false);
    }
    public boolean hasWarning() {
        return rootFrame.hasMessage(false, true, false);
    }
    public boolean hasError() {
        return rootFrame.hasMessage(false, false, true);
    }
    public String getFirstErrorMessage() {       // Help with transition from simpler BeanPanel.getValidationMessage
        return rootFrame.getFirstErrorMessage(); // method (essentially does same thing)
    }
    public int[] getQuickCount() {
        return rootFrame.getMessageCount();
    }

    // Mutators

    public void addCloseAction(Predicate<ValidationContext> test, Consumer<ValidationContext> action) {
        closeActions.add(new Pair<>(test, action));
    }


    /////////////////
    // ADD MESSAGE //
    /////////////////

    // So many overloads because most likely, developers would
    // want to keep their validation methods terse.  There
    // are info(), warn(), and error() methods which take
    //
    //   Reason (Required)    - Why the message is being added for
    //                          this frame
    //   Evidence (Optional)  - A string representation of the piece of
    //                          data that was of interest (or invalid)
    //   Condition (Optional) - Allows you to move your if inside this
    //                          code for more 1 liner conditions
    //
    //   Condition Example:
    //
    //     Instead of:
    //       if(value < 10) {
    //           context.error("bbb", "aaaa");
    //       }
    //
    //     You can do this:
    //       context.error("bbb", "aaaa", value < 10);
    //
    // And there are infoFor(), warnFor(), errorFor() which
    // are just like the previous, but take as their first
    // argument a final frame name under which to register
    // the messages.
    //
    //   Name (Required) - Name of component to which the messages should
    //                     be registered (essentially one extra layer of
    //                     indirection without having to call check)
    //
    // This is used in cases where the component itself is
    // not self-validatable (any component that hasn't
    // yet implemented Validatable).  Validatable components
    // are registered by calling the check() methods.
    //
    // The return type of boolean here is a really slick way
    // save the developer a bunch of bloated code related to
    // deciding whether some certain validation checks should
    // even be performed if they are dependent on earlier
    // checks in the same method.

    public boolean info(String reason) {
        return msgSink(null, MessageType.INFO, reason, null, null, true, null);
    }
    public boolean warn(String reason) {
        return msgSink(null, MessageType.WARN, reason, null, null, true, null);
    }
    public boolean error(String reason) {
        return msgSink(null, MessageType.ERROR, reason, null, null, true, null);
    }
    public boolean custom(MessageType type, String reason) {
        return msgSink(null, type, reason, null, null, true, null);
    }

    public boolean info(String reason, boolean condition) {
        return msgSink(null, MessageType.INFO, reason, null, null, condition, null);
    }
    public boolean warn(String reason, boolean condition) {
        return msgSink(null, MessageType.WARN, reason, null, null, condition, null);
    }
    public boolean error(String reason, boolean condition) {
        return msgSink(null, MessageType.ERROR, reason, null, null, condition, null);
    }
    public boolean custom(MessageType type, String reason, boolean condition) {
        return msgSink(null, type, reason, null, null, condition, null);
    }

    public boolean info(String reason, String evidence) {
        return msgSink(null, MessageType.INFO, reason, evidence, null, true, null);
    }
    public boolean warn(String reason, String evidence) {
        return msgSink(null, MessageType.WARN, reason, evidence, null, true, null);
    }
    public boolean error(String reason, String evidence) {
        return msgSink(null, MessageType.ERROR, reason, evidence, null, true, null);
    }
    public boolean custom(MessageType type, String reason, String evidence) {
        return msgSink(null, type, reason, evidence, null, true, null);
    }

    public boolean info(String reason, String evidence, boolean condition) {
        return msgSink(null, MessageType.INFO, reason, evidence, null, condition, null);
    }
    public boolean warn(String reason, String evidence, boolean condition) {
        return msgSink(null, MessageType.WARN, reason, evidence, null, condition, null);
    }
    public boolean error(String reason, String evidence, boolean condition) {
        return msgSink(null, MessageType.ERROR, reason, evidence, null, condition, null);
    }
    public boolean custom(MessageType type, String reason, String evidence, boolean condition) {
        return msgSink(null, type, reason, evidence, null, condition, null);
    }

    public boolean infoFor(String name, String reason) {
        return msgSink(name, MessageType.INFO, reason, null, null, true, null);
    }
    public boolean warnFor(String name, String reason) {
        return msgSink(name, MessageType.WARN, reason, null, null, true, null);
    }
    public boolean errorFor(String name, String reason) {
        return msgSink(name, MessageType.ERROR, reason, null, null, true, null);
    }
    public boolean customFor(MessageType type, String name, String reason) {
        return msgSink(name, type, reason, null, null, true, null);
    }

    public boolean infoFor(String name, String reason, boolean condition) {
        return msgSink(name, MessageType.INFO, reason, null, null, condition, null);
    }
    public boolean warnFor(String name, String reason, boolean condition) {
        return msgSink(name, MessageType.WARN, reason, null, null, condition, null);
    }
    public boolean errorFor(String name, String reason, boolean condition) {
        return msgSink(name, MessageType.ERROR, reason, null, null, condition, null);
    }
    public boolean customFor(MessageType type, String name, String reason, boolean condition) {
        return msgSink(name, type, reason, null, null, condition, null);
    }

    public boolean infoFor(String name, String reason, String evidence) {
        return msgSink(name, MessageType.INFO, reason, evidence, null, true, null);
    }
    public boolean warnFor(String name, String reason, String evidence) {
        return msgSink(name, MessageType.WARN, reason, evidence, null, true, null);
    }
    public boolean errorFor(String name, String reason, String evidence) {
        return msgSink(name, MessageType.ERROR, reason, evidence, null, true, null);
    }
    public boolean customFor(MessageType type, String name, String reason, String evidence) {
        return msgSink(name, type, reason, evidence, null, true, null);
    }

    public boolean infoFor(String name, String reason, String evidence, boolean condition) {
        return msgSink(name, MessageType.INFO, reason, evidence, null, condition, null);
    }
    public boolean warnFor(String name, String reason, String evidence, boolean condition) {
        return msgSink(name, MessageType.WARN, reason, evidence, null, condition, null);
    }
    public boolean errorFor(String name, String reason, String evidence, boolean condition) {
        return msgSink(name, MessageType.ERROR, reason, evidence, null, condition, null);
    }
    public boolean customFor(MessageType type, String name, String reason, String evidence, boolean condition) {
        return msgSink(name, type, reason, evidence, null, condition, null);
    }

    public boolean error(String reason, Throwable e) {
        return msgSink(null, MessageType.ERROR, reason, null, e, true, null);
    }
    public boolean error(String reason, String evidence, Throwable e) {
        return msgSink(null, MessageType.ERROR, reason, evidence, e, true, null);
    }

    public boolean msg(MessageType type, String reason,
                        String evidence, Throwable exception, boolean condition) {
        return msgSink(null, type, reason, evidence, exception, condition, null);
    }
    public boolean msgFor(String name, MessageType type, String reason,
                       String evidence, Throwable exception, boolean condition) {
        return msgSink(name, type, reason, evidence, exception, condition, null);
   }

    public boolean connect(String name, ValidationContext context) {
        return msgSink(name, null, null, null, null, true, context);
    }

    private boolean msgSink(String name, MessageType type, String reason,
                        String evidence, Throwable exception, boolean condition,
                        ValidationContext connectContext) {
        return registerMessage(
            new MessageRequest()
                .setName(name)
                .setType(type)
                .setReason(reason)
                .setEvidence(evidence)
                .setException(exception)
                .setCondition(condition)
                .setConnectContext(connectContext)
        );
    }

    private boolean registerMessage(MessageRequest request) {
        checkClosed();
        if(!request.condition) {
            return true;           // Allows condition chains to continue
        }
        ValidationFrame curFrame = path.get(path.size() - 1);
        String name = request.name;
        if(name != null) {
            curFrame = getChildFrame(curFrame, name);
            if(request.connectContext != null) {
                // No reason at this point to clear all before adding.
                curFrame.messages.addAll(request.connectContext.rootFrame.messages);
                curFrame.children.putAll(request.connectContext.rootFrame.children);
                return false;   // Don't move on
            }
        }
        ValidationMessage message = request.toMessage();
        curFrame.messages.add(message);
        return false;
    }


    //////////////
    // CHILDREN //
    //////////////

    public void check(String name, Validatable v) {                  // TRYING TO MERGE THIS AND ^^^
        checkClosed();
        ValidationFrame curFrame = path.get(path.size() - 1);
        ValidationFrame childFrame = getChildFrame(curFrame, name);

        path.add(childFrame);
        v.validateInput(this);
        path.remove(path.size() - 1);
    }

    // Instead of calling:
    //   context.check("Inner", pnl);
    // just forward the method call onto the inner Validatable's
    // same method to "hide" the fact that this wrapper
    // panel exists as far as the validation context itself
    // is concerned.
    public void check(Validatable v) {
        checkClosed();
        v.validateInput(this);
    }


    ////////////
    // CUSTOM //
    ////////////

    // When a given context has full control over multiple
    // layers of contexts who aren't self-validating.
    // To open and close the context manually when there is no
    // Validatable to have it done automatically.

    public void push(String name) {
        checkClosed();
        ValidationFrame curFrame = path.get(path.size() - 1);
        ValidationFrame childFrame = getChildFrame(curFrame, name);
        path.add(childFrame);
    }

    public void pop() {
        checkClosed();
        path.remove(path.size() - 1);
    }

    public ValidationFrame getChildFrame(String name) {
        ValidationFrame curFrame = path.get(path.size() - 1);
        return getChildFrame(curFrame, name);
    }


    //////////
    // MISC //
    //////////

    private void checkClosed() {
        if(path.isEmpty()) {
            throw new IllegalStateException("Cannot perform further actions on validation context once it is closed.");
        }
    }

    public void close() {
        for(Pair<Predicate<ValidationContext>, Consumer<ValidationContext>> testAndAction : closeActions) {
            Predicate<ValidationContext> test = testAndAction.getValue1();
            if(test.test(this)) {
                Consumer<ValidationContext> action = testAndAction.getValue2();
                action.accept(this);
            }
        }
        path.clear();       // Context now "closed"
    }

    public void overlay(ValidationContext context) {
        rootFrame.overlay(context.rootFrame);
    }

    public Map<String, ValidationMessage> find(ValidationFrameFindOptions options) {
        return rootFrame.find(options);
    }

    private ValidationFrame getChildFrame(ValidationFrame curFrame, String name) {
        ValidationFrame childFrame = curFrame.children.get(name);
        if(childFrame == null) {
            childFrame = new ValidationFrame();
            curFrame.children.put(name, childFrame);
        }
        return childFrame;
    }

    public static ValidationContext createTestContext() {
        ValidationContext context = new ValidationContext();
        context.warn("aaa", "aaaa");
        context.error("bbb", "aaaa");
        context.info("aaaa", "aaaa");
        context.msg(MessageType.ERROR, "xxxx", "xxxx", new RuntimeException(), true);
        context.check("subframe", c -> {
            c.warn("aaa", "aaaa");
            c.error("bbb", "aaaa");
            context.check("name", c2 -> {
                c2.error("xxx", "asfsfsf");
            });
            context.check("name2", c2 -> {
            });
        });
        return context;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return toString(false);
    }

    // Related Accessors (Computed)

    public String toString(boolean includeEmpty) {   // TODO: Would be nice to have other options like
        return rootFrame.toString(includeEmpty);     // whether to include each category, info, warning,
    }                                                // error, etc.


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class MessageRequest {
        private String name;
        private MessageType type;
        private String reason;
        private String evidence;
        private Throwable exception;
        private boolean condition;
        private ValidationContext connectContext;

        public ValidationMessage toMessage() {
            return new ValidationMessage(type, reason, evidence, exception);
        }
        public MessageRequest setName(String name) {
            this.name = name;
            return this;
        }
        public MessageRequest setType(MessageType type) {
            this.type = type;
            return this;
        }
        public MessageRequest setReason(String reason) {
            this.reason = reason;
            return this;
        }
        public MessageRequest setEvidence(String evidence) {
            this.evidence = evidence;
            return this;
        }
        public MessageRequest setException(Throwable exception) {
            this.exception = exception;
            return this;
        }
        public MessageRequest setCondition(boolean condition) {
            this.condition = condition;
            return this;
        }
        public MessageRequest setConnectContext(ValidationContext connectContext) {
            this.connectContext = connectContext;
            return this;
        }
    }
}
