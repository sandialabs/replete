package replete.event;

import java.util.ArrayList;
import java.util.List;

import replete.progress.ProgressMessage;

public class ProgressEvent {


    ///////////
    // FIELD //
    ///////////

    private List<ProgressMessage> messages;   // Allows for a queue of messages in one event.


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ProgressEvent(ProgressMessage message) {
        messages = new ArrayList<>();
        messages.add(message);
    }
    public ProgressEvent(List<ProgressMessage> messages) {
        this.messages = messages;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public List<ProgressMessage> getMessages() {
        return messages;
    }

    // Computed

    public ProgressMessage getMessage() {
        return messages.get(messages.size() - 1);
    }
}
