package cn.mbdoge.jyx.event;


import lombok.Getter;
import lombok.Setter;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractEvent extends EventObject {
    @Getter
    private final String type;
//    private final Map<String, Object> data = new HashMap<>();

    @Getter
    private long time;
    private boolean stoped = false;
//    private boolean canStop = false;
//    private boolean immediatelyStoped = false;

    public AbstractEvent(String type, Object source) {
        super(source);
        this.type = type;
        this.time = System.currentTimeMillis();
//        if (data != null) {
//            this.data.putAll(data);
//        }
    }

//    public final void setData (Map<String, Object> data) {
//        if (data != null) {
//            this.data.putAll(data);
//        }
//    }
//
//    public Map<String, Object> getData() {
//        return new HashMap<>(data);
//    }
//
//    public Object getParams (String name) {
//        return this.data.get(name);
//    }

    public void stopPropagation(){
        this.stoped = true;
    }

//    public void stopPropagationImmediately(){
//        this.stoped = this.immediatelyStoped = true;
//    }

    public boolean shouldStopPropagationImmediately() {
//        return this.canStop || this.immediatelyStoped;
        return this.stoped;
    }

    @Override
    public String toString() {
        return "event = [" + type +"]";
    }
}
