package cn.mbdoge.jyx.event;

import java.util.EventListener;

/**
 * @author jyx
 */
public interface EventCallback<T extends AbstractEvent> extends EventListener {
    /**
     * 事件的响应
     * @param event 时间对象
     * @throws Exception 当前抛出异常时，不会影响其他的事件绑定
     */
    default void mCall(AbstractEvent event) throws Exception {
        try {
            this.call((T) event);
        } catch (ClassCastException ignored) {
        }
    }

    /**
     * 事件的响应
     * @param event 时间对象
     * @throws Exception 当前抛出异常时，不会影响其他的事件绑定
     */
    void call(T event) throws Exception;
}
