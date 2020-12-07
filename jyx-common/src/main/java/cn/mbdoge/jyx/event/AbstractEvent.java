package cn.mbdoge.jyx.event;


import lombok.Getter;
import lombok.Setter;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jyx
 */
public abstract class AbstractEvent extends EventObject {
    @Getter
    private final String type;
    @Getter
    private final long time;
    private boolean stoped = false;
//    private boolean canStop = false;
//    private boolean immediatelyStoped = false;

    public AbstractEvent(String type, Object source) {
        super(source);
        this.type = type;
        this.time = System.currentTimeMillis();
    }

    public void stopPropagation(){
        this.stoped = true;
    }

    public boolean shouldStopPropagationImmediately() {
        return this.stoped;
    }

    @Override
    public String toString() {
        return "event = [" + type +"]";
    }
}
