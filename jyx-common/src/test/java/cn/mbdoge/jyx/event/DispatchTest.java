package cn.mbdoge.jyx.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class DispatchTest {
    private Dispatch dispatch;
    @BeforeEach
    void setUp() {
        dispatch = new Dispatch();
    }



    @Test
    @DisplayName("测试事件绑定")
    void name() throws InterruptedException, ExecutionException {
        dispatch.addEventName(D.event);
        dispatch.addEventName(D.init);

        assertEquals(2, dispatch.eventSize());

        assertEquals(0, dispatch.eventSize(D.event));
        assertEquals(0, dispatch.eventSize(D.init));


        EventCallback eventCallback = (e) -> { };
        dispatch.bind(D.event, eventCallback);
        assertEquals(1, dispatch.eventSize(D.event.name()));

        dispatch.unbind(D.event, eventCallback);
        assertEquals(0, dispatch.eventSize(D.event.name()));

        dispatch.once(D.event, eventCallback);
        assertEquals(1, dispatch.eventSize(D.event));

        Event event = new Event(D.event, "abc");
        Future<Boolean> fire = dispatch.fire(event);
        Boolean aBoolean = fire.get();
        assertEquals(0, dispatch.eventSize(D.event));
    }

    @Test
    @DisplayName("测试事件重复绑定")
    void name0() {
        dispatch.addEventName(D.event);
        EventCallback eventCallback = (e) -> { };

        dispatch.bind(D.event, eventCallback);
        assertEquals(1, dispatch.eventSize(D.event.name()));

        dispatch.bind(D.event, eventCallback);
        assertEquals(1, dispatch.eventSize(D.event.name()));
    }

    @Test
    @DisplayName("测试once重复绑定")
    void name01() throws ExecutionException, InterruptedException {
        dispatch.addEventName(D.event);
        EventCallback eventCallback1 = (e) -> {
            System.out.println("1 = " + 1);
        };
        EventCallback eventCallback2 = (e) -> {
            System.out.println("1 = " + 2);
        };

        EventCallback eventCallback3 = (e) -> {
            System.out.println("1 = " + 3);
        };

        EventCallback eventCallback4 = (e) -> {
            System.out.println("1 = " + 4);
        };

        dispatch.once(D.event, eventCallback1);
        dispatch.once(D.event, eventCallback2);
        dispatch.once(D.event, eventCallback4);
        dispatch.once(D.event, eventCallback3);

        assertEquals(4, dispatch.eventSize(D.event.name()));

        Event event = new Event(D.event, "abc");
        Future<Boolean> fire = dispatch.fire(event);
        Boolean aBoolean = fire.get();
        assertEquals(0, dispatch.eventSize(D.event));

    }

    /**
     * 测试冒泡
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("测试冒泡被阻止")
    void name2() throws ExecutionException, InterruptedException {
        dispatch.addEventName(D.event);
        assertEquals(1, dispatch.eventSize());

        AtomicInteger i = new AtomicInteger();
        EventCallback eventCallback = (e) -> {
            e.stopPropagation();
            i.getAndIncrement();
        };
        EventCallback eventCallback2 = (e) -> {
            i.getAndIncrement();
        };
        dispatch.bind(D.event, eventCallback);
        dispatch.bind(D.event, eventCallback2);

        Event event = new Event(D.event, "abc");

        Future<Boolean> abc = dispatch.fire(event);
        Boolean aBoolean = abc.get();
        assertTrue(aBoolean);
        assertEquals(1, i.get());
    }

    /**
     * 测试冒泡
     */
    @Test
    @DisplayName("测试冒泡")
    void name3() throws ExecutionException, InterruptedException {
        dispatch.addEventName(D.event);
        assertEquals(1, dispatch.eventSize());

        AtomicInteger i = new AtomicInteger();
        EventCallback eventCallback = (e) -> {
            i.getAndIncrement();
        };
        EventCallback eventCallback2 = (e) -> {
            i.getAndIncrement();
        };
        dispatch.bind(D.event, eventCallback);
        dispatch.bind(D.event, eventCallback2);

        Event event = new Event(D.event, "abc");
        Future<Boolean> abc = dispatch.fire(event);
        Boolean aBoolean = abc.get();
        assertFalse(aBoolean);
        assertEquals(2, i.get());
    }

    /**
     * 测试没有绑定的事件
     */
    @Test
    @DisplayName("测试没有绑定的事件")
    void name4() throws ExecutionException, InterruptedException {
        dispatch.addEventName(D.event);
        assertEquals(1, dispatch.eventSize());

        AtomicInteger i = new AtomicInteger();
        EventCallback eventCallback = (e) -> {
            i.getAndIncrement();
        };

        dispatch.bind(D.init, eventCallback);
        Event event = new Event(D.event, "abc");

        Future<Boolean> abc = dispatch.fire(event);
        Boolean aBoolean = abc.get();
        assertFalse(aBoolean);
        assertEquals(0, i.get());
    }

    @Test
    @DisplayName("测试参数获取")
    void name5() throws ExecutionException, InterruptedException {
        dispatch.addEventName(D.event);
        assertEquals(1, dispatch.eventSize());

        EventCallback eventCallback = (e) -> {
            Object params = e.getSource();
            assertTrue(params instanceof String);
            assertEquals(params, "abc");
            assertEquals(D.event.name(), e.getType());
        };

        dispatch.bind(D.event, eventCallback);

        Event event = new Event(D.event, "abc");
        Future<Boolean> abc = dispatch.fire(event);
        Boolean aBoolean = abc.get();
        assertFalse(aBoolean);

        dispatch.addEventName(D.init);
        assertEquals(2, dispatch.eventSize());
        eventCallback = (e) -> {
            Object params = e.getSource();
            assertTrue(params instanceof String);
            assertEquals(params, "abc");
            assertEquals(D.init.name(), e.getType());
        };

        dispatch.bind(D.init, eventCallback);

        event = new Event(D.init, "abc");
        abc = dispatch.fire(event);
        aBoolean = abc.get();
        assertFalse(aBoolean);
    }


    static class Event extends AbstractEvent {

        public Event(D type, Object data) {
            super(type.name(), data);
        }
    }


    enum D implements EventType {
        event,
        init
    }


}