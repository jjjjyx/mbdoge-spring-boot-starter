package cn.mbdoge.jyx.event;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class Dispatch {

    private Map<String, List<EventCallback>> eventCallbacks = new HashMap<>();
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public Dispatch() {
    }
    protected static String[] parseName (String eventName) {
        if (isEmpty(eventName)) {
            return new String[0];
        }
        return eventName.split("[\\s,]+");
    }

    protected static boolean isEmpty (String str) {
        return (str == null || "".equals(str));
    }


    protected void listen(String type, EventCallback callBack) {
        List<EventCallback> eventCallbackList = this.eventCallbacks.computeIfAbsent(type, k -> new ArrayList<>());
        if (!eventCallbackList.contains(callBack)) {
            eventCallbackList.add(callBack);
        }
//        log.info("监听事件 = eventName = {}, 已注册列表大小= {}, callback = {}", type.name(), callbackList.size() ,callBack.toString());
    }

    protected void unListen(String type, EventCallback callBack) {
        List<EventCallback> eventCallbackList = this.eventCallbacks.get(type);
        if (eventCallbackList != null)
            eventCallbackList.remove(callBack);
//        log.info("移除监听 = eventName = {}, 已注册列表大小= {}, callback = {}", type.name(), callbackList.size() ,callBack.toString());
    }

    public void bind(EventType eventType, EventCallback callBack) {
        this.listen(eventType.name(), callBack);
    }

    public void on(String eventNames, EventCallback callBack){
        String[] names = parseName(eventNames);
        for (String name : names) {
            this.listen(name, callBack);
        }
    }

    public void unbind(EventType eventType, EventCallback callBack) {
        this.unListen(eventType.name(), callBack);
    }

    public void off(String eventNames, EventCallback callBack){
        String[] names = parseName(eventNames);
        for (String name : names) {
            this.unListen(name, callBack);
        }
//        log.trace("取消监听事件 = eventName = {}, callback = {}", type.name(), callBack.toString());

    }

    public void once(final EventType eventNames, final EventCallback eventCallback) {
        this.once(eventNames.name(), eventCallback);
    }

    public void once(final String eventNames, final EventCallback eventCallback) {
        EventCallback temp = new EventCallback() {
            @Override
            public void call(AbstractEvent event) {
                eventCallback.call(event);
                off(eventNames, this);
            }
        };

        this.on(eventNames, temp);
    }


//    public Future<Boolean> fire(final T event, Map<String, Object> data) {
    public Future<Boolean> fire(final AbstractEvent event) {
        Objects.requireNonNull(event);
//        log.trace("触发事件 = eventName = {}, args.size = {}", event.name(), data.size());
        return executorService.submit(() -> {
            String key = event.getType();
            // 拷贝一份 防止在once 修改事件列表
            List<EventCallback> eventCallbackList = new ArrayList<>(this.eventCallbacks.get(key));
            if (eventCallbackList.size() == 0) {
//                log.trace("事件 {} 尚未注册，或者没有绑定事件 callbackList = {}", event.name(), callbackList);
                return false;
            }
//            log.trace("事件 {} 响应列表 size = {}", event.name(), callbackList.size());
            for (EventCallback eventCallback : eventCallbackList) {
                eventCallback.call(event);
                if (event.shouldStopPropagationImmediately()) {
                    break;
                }
            }

            return event.shouldStopPropagationImmediately();
        });
    }

//    public Future<Boolean> fire(final String eventName, String[] paramNames, Object... args) {
//        return this.fire(eventName, toMap(paramNames, args));
//    }

//    public Future<Boolean> fire(final String type, Object... args) {
//        return this.fire(type, toMap(type.getNames(), args));
//    }

    public void addEventName(String property) {
        Objects.requireNonNull(property);

//        log.trace("注冊事件 ={}", property.name());
        if(!eventCallbacks.containsKey(property)) {
            eventCallbacks.put(property,new ArrayList<>());
        }
    }

    public void addEventName(EventType eventType) {
       this.addEventName(eventType.name());
    }

    public int eventSize () {
        return this.eventCallbacks.size();
    }

    public int eventSize (String property) {
        List<EventCallback> eventCallbackList = this.eventCallbacks.get(property);
        return eventCallbackList == null ? 0 : eventCallbackList.size();
    }

    public int eventSize (EventType eventType) {
        return eventSize(eventType.name());
    }

}
