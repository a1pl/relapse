package cc.squall.relapse.events;


import lombok.Getter;
import meteordevelopment.orbit.ICancellable;

public class EventChatSend implements ICancellable {
    @Getter
    private final String message;
    private boolean cancelled;

    public EventChatSend(String message) { this.message = message; }

    @Override public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    @Override public boolean isCancelled() { return cancelled; }
}