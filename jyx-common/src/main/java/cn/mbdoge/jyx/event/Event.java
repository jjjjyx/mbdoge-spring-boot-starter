package cn.mbdoge.jyx.event;


import java.util.HashMap;
import java.util.Map;

public class Event {
    private final String name;
    private final Map<String, Object> data = new HashMap<>();
//    private final Object[] args;

    private boolean stoped = false;
    private boolean canStop = false;
    private boolean immediatelyStoped = false;

    public Event(String name, Map<String, Object> data) {
        this.name = name;

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
        this.stoped = true;
        this.immediatelyStoped = true;
    }
    public boolean shouldStopPropagationImmediately() {
        return this.canStop || this.immediatelyStoped;
    }

}
