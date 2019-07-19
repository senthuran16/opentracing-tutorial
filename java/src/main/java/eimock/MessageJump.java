package eimock;

class MessageJump implements Comparable<MessageJump> {
    public int messageJumpId;

    public int metaTenantId;
    public String messageFlowId;
    public String host;
    public String hashcode;
    public String componentName;
    public String componentType;
    public int componentIndex;
    public String componentId;
    public String startTime;
    public String endTime;
    public long duration;
    public String beforePayload;
    public String afterPayload;
    public String contextPropertyMap;
    public String transportPropertyMap;
    public String children;
    public String entryPoint;
    public String entryPointHashcode;
    public int faultCount;
    public String eventTimestamp;

    public long delayFromInitialPoint;

    public MessageJump(int messageJumpId, int metaTenantId, String messageFlowId, String host, String hashcode, String componentName, String componentType, int componentIndex, String componentId, String startTime, String endTime, long duration, String beforePayload, String afterPayload, String contextPropertyMap, String transportPropertyMap, String children, String entryPoint, String entryPointHashcode, int faultCount, String eventTimestamp) {
        this.messageJumpId = messageJumpId;
        this.metaTenantId = metaTenantId;
        this.messageFlowId = messageFlowId;
        this.host = host;
        this.hashcode = hashcode;
        this.componentName = componentName;
        this.componentType = componentType;
        this.componentIndex = componentIndex;
        this.componentId = componentId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.beforePayload = beforePayload;
        this.afterPayload = afterPayload;
        this.contextPropertyMap = contextPropertyMap;
        this.transportPropertyMap = transportPropertyMap;
        this.children = children;
        this.entryPoint = entryPoint;
        this.entryPointHashcode = entryPointHashcode;
        this.faultCount = faultCount;
        this.eventTimestamp = eventTimestamp;
    }

    public void setDelayFromTimestamp(String timestamp) {
        long delay = Long.parseLong(this.startTime) - Long.parseLong(timestamp);
        if (delay >= 0) {
            this.delayFromInitialPoint = delay;
        } else {
            this.delayFromInitialPoint = 0;
        }
    }

    @Override
    public int compareTo(MessageJump o) {
        if (Long.parseLong(this.startTime) > Long.parseLong(o.startTime)) {
            return 1;
        } else if (Long.parseLong(o.startTime) > Long.parseLong(this.startTime)) {
            return -1;
        } else if (Long.parseLong(this.endTime) < Long.parseLong(o.endTime)) {
            return 1;
        } else if (Long.parseLong(o.endTime) < Long.parseLong(this.endTime)) {
            return -1;
        }
        return 0;
    }
}
