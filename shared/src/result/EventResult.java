package result;

import model.Event;

public class EventResult extends Result {
    private Event event;
    private Event[] data;

    public EventResult(Event event, boolean success) {
        this.event = event;
        this.success = success;
    }

    public EventResult(Event[] events, boolean success) {
        this.data = events;
        this.success = success;
    }

    public EventResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
}
