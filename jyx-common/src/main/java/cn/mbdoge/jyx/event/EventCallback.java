package cn.mbdoge.jyx.event;

import java.util.EventListener;

public interface EventCallback extends EventListener {
    void call(AbstractEvent event);
}
