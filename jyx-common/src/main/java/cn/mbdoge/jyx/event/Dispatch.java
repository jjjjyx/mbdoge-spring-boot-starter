package cn.mbdoge.jyx.event;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Dispatch<T extends EventType> {

    private Map<T, List<Callback>> eventCallbacks = new HashMap<>();
    private ExecutorService executorService = Executors.newCachedThreadPool();

//    protected static String[] parseName (String eventName) {
//        if (isEmpty(eventName)) {
//            return new String[0];
//        }
//        return eventName.split("\\s+");
//    }
//    protected static boolean isEmpty (String str) {
//        return (str == null || "".equals(str));
//    }


    protected void listen(T type, Callback callBack) {
        List<Callback> callbackList = this.eventCallbacks.computeIfAbsent(type, k -> new ArrayList<>());
        callbackList.add(callBack);
//        log.info("监听事件 = eventName = {}, 已注册列表大小= {}, callback = {}", type.name(), callbackList.size() ,callBack.toString());
    }

    public void on(T type, Callback callBack){
//        String[] names = parseName(eventName);
//        for (String name : names) {
//
//        }
        this.listen(type, callBack);

    }
    public void off(T type, Callback callBack){
//        String[] names = parseName(eventName);
//        for (String name : names) {

//        }
//        log.trace("取消监听事件 = eventName = {}, callback = {}", type.name(), callBack.toString());
        List<Callback> callbackList = this.eventCallbacks.get(type);
        if (callbackList != null)
            callbackList.remove(callBack);
    }

    public void once(T type, final Callback callback) {
        Callback callback1 = new Callback() {
            @Override
            public void call(Event event) {
                off(type, this);
                callback.call(event);
            }
        };

        this.on(type, callback1);
    }


    public Future<Boolean> fire(final T type, Map<String, Object> data) {
        Objects.requireNonNull(type);
//        log.trace("触发事件 = eventName = {}, args.size = {}", type.name(), data.size());
        return executorService.submit(() -> {

            List<Callback> callbackList = this.eventCallbacks.get(type);
            if (callbackList == null || callbackList.size() == 0) {
//                log.trace("事件 {} 尚未注册，或者没有绑定事件 callbackList = {}", type.name(), callbackList);
                return false;
            }
//            log.trace("事件 {} 响应列表 size = {}", type.name(), callbackList.size());

            Event event = new Event(type, data);
            event.setTime(System.currentTimeMillis());

            for (Callback callback : callbackList) {
                callback.call(event);
                if(event.shouldStopPropagationImmediately()) {
                    break;
                }
            }
            return event.shouldStopPropagationImmediately();
        });
    }

//    public Future<Boolean> fire(final String eventName, String[] paramNames, Object... args) {
//        return this.fire(eventName, toMap(paramNames, args));
//    }

    public Future<Boolean> fire(final T type, Object... args) {
        return this.fire(type, toMap(type.getNames(), args));
    }

    protected Map<String, Object> toMap(String[] paramNames, Object... args) {
//        int index = 0;
        if (null == paramNames) {
            paramNames = new String[0];
        }

        Map<String, Object> data = new HashMap<>();
        int argsLength = args.length;
        int nameLength = paramNames.length;
        for (int i = 0; i < nameLength && i < argsLength; i++) {
            data.put(paramNames[i], args[i]);
        }

        for (int i = nameLength; i < argsLength; i++) {
            data.put(String.valueOf(i), args[i]);
        }
//        log.trace("paramNames length = {} args length = {}, paramNames = {}", nameLength, argsLength, data.keySet());

        return data;
    }

    public void addEventName(T property) {
        Objects.requireNonNull(property);

//        log.trace("注冊事件 ={}", property.name());
        if(!eventCallbacks.containsKey(property)) {
            eventCallbacks.put(property,new ArrayList<>());
        }
    }

    public int eventSize () {
        return this.eventCallbacks.size();
    }

    public int eventSize (T property) {
        List<Callback> callbackList = this.eventCallbacks.get(property);
        return callbackList == null ? 0 : callbackList.size();
    }

}
