package cn.mbdoge.jyx.event;


import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class Event {
    @Getter
    private final EventType type;
    private final Map<String, Object> data = new HashMap<>();
//    private final Object[] args;

    @Getter @Setter
    private long time;
    private boolean stoped = false;
    private boolean canStop = false;
    private boolean immediatelyStoped = false;

    public Event(EventType type, Map<String, Object> data) {
        this.type = type;

        if (data != null) {
            this.data.putAll(data);
        }
    }

    public Map<String, Object> getData() {
        return new HashMap<>(data);
    }

    public <T> T getParams (String name) {
        return (T) this.data.get(name);
    }

    public void stopPropagation(){
        this.stoped = true;
    }


    public void stopPropagationImmediately(){
        this.stoped = this.immediatelyStoped = true;
    }
    public boolean shouldStopPropagationImmediately() {
        return this.canStop || this.immediatelyStoped;
    }

}
