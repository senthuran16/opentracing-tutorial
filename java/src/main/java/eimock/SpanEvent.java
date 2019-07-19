package eimock;

public class SpanEvent implements Comparable<SpanEvent> {
    public String eventTime;
    public SpanEventType eventType;
    public long delayBeforeEvent;
    public MessageJump messageJump;

    public SpanEvent(SpanEventType eventType, String eventTime, MessageJump messageJump) {
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.messageJump = messageJump;
    }

    public void setDelayBeforeEvent(long delayBeforeEvent) {
        this.delayBeforeEvent = delayBeforeEvent;
    }

    @Override
    public int compareTo(SpanEvent o) {
        if (Long.parseLong(this.eventTime) > Long.parseLong(o.eventTime)) {
            return 1;
        } else if (Long.parseLong(this.eventTime) < Long.parseLong(o.eventTime)) {
            return -1;
        }
        return 0;
    }
}
