package cn.mbdoge.jyx.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class DispatchTest {
    private Dispatch<D> dispatch;
    @BeforeEach
    void setUp() {
        dispatch = new Dispatch<>();
    }

    @Test
    @DisplayName("测试参数转换")
    void toMap() {
        String args1 = "xxx";
        A a = new A();
        Map<String, Object> abc = dispatch.toMap(new String[]{}, a, "abc");

        Set<String> strings = abc.keySet();
        Assertions.assertIterableEquals(strings, Arrays.asList("0", "1"));

        Object o = abc.get("0");
        Assertions.assertTrue(o instanceof A);

        Object o2 = abc.get("1");
        Assertions.assertTrue(o2 instanceof String);

        // ========= 传入null
        abc = dispatch.toMap(null, a, "abc");

        strings = abc.keySet();
        Assertions.assertIterableEquals(strings, Arrays.asList("0", "1"));

        o = abc.get("0");
        Assertions.assertTrue(o instanceof A);

        o2 = abc.get("1");
        Assertions.assertTrue(o2 instanceof String);


        // ========= 不传入args
        abc = dispatch.toMap(null);
        Assertions.assertTrue(abc.isEmpty());


        // ========= 传入名称，个数小于 参数
        abc = dispatch.toMap(new String[]{"xx"}, a, "abc");

        strings = abc.keySet();
        Assertions.assertIterableEquals(strings, Arrays.asList("xx", "1"));

        o = abc.get("xx");
        Assertions.assertTrue(o instanceof A);

        o2 = abc.get("1");
        Assertions.assertTrue(o2 instanceof String);

        // ========= 传入名称，个数多余 参数
        abc = dispatch.toMap(new String[]{"xx", "str", "aa"}, a, "abc");

        strings = abc.keySet();
        Assertions.assertIterableEquals(strings, Arrays.asList("xx", "str"));

        o = abc.get("xx");
        Assertions.assertTrue(o instanceof A);

        o2 = abc.get("str");
        Assertions.assertTrue(o2 instanceof String);
    }

    @Test
    @DisplayName("测试事件绑定")
    void name() throws InterruptedException {
        dispatch.addEventName(D.event);
        dispatch.addEventName(D.init);

        assertEquals(2, dispatch.eventSize());

        assertEquals(0, dispatch.eventSize(D.event));
        assertEquals(0, dispatch.eventSize(D.init));

        Callback callback = (e) -> { };
        dispatch.on(D.event, callback);
        assertEquals(1, dispatch.eventSize(D.event));

        dispatch.off(D.event, callback);
        assertEquals(0, dispatch.eventSize(D.event));

        dispatch.once(D.event, callback);
        assertEquals(1, dispatch.eventSize(D.event));

        dispatch.fire(D.event, "abc");
        Thread.sleep(100);
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
        Callback callback = (e) -> {
            e.stopPropagation();
            i.getAndIncrement();
        };
        Callback callback2 = (e) -> {
            i.getAndIncrement();
        };
        dispatch.on(D.event, callback);
        dispatch.on(D.event, callback2);

        Future<Boolean> abc = dispatch.fire(D.event, "abc");
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
        Callback callback = (e) -> {
            i.getAndIncrement();
        };
        Callback callback2 = (e) -> {
            i.getAndIncrement();
        };
        dispatch.on(D.event, callback);
        dispatch.on(D.event, callback2);

        Future<Boolean> abc = dispatch.fire(D.event, "abc");
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
        Callback callback = (e) -> {
            i.getAndIncrement();
        };

        dispatch.on(D.init, callback);

        Future<Boolean> abc = dispatch.fire(D.event, "abc");
        Boolean aBoolean = abc.get();
        assertFalse(aBoolean);
        assertEquals(0, i.get());
    }

    @Test
    @DisplayName("测试参数获取")
    void name5() throws ExecutionException, InterruptedException {
        dispatch.addEventName(D.event);
        assertEquals(1, dispatch.eventSize());

        Callback callback = (e) -> {
            Object params = e.getParams("0");
            assertTrue(params instanceof String);
            assertEquals(params, "abc");

            params = e.getParams("abc");
            assertNull(params);

            params = e.getParams("1");
            assertNull(params);

            Map<String, Object> data = e.getData();
            assertEquals(data.get("0"), "abc");

            assertEquals(1, data.size());

            assertEquals(D.event, e.getType());
        };

        dispatch.on(D.event, callback);

        Future<Boolean> abc = dispatch.fire(D.event, "abc");
        Boolean aBoolean = abc.get();
        assertFalse(aBoolean);

        dispatch.addEventName(D.init);
        assertEquals(2, dispatch.eventSize());
        callback = (e) -> {
            Object params = e.getParams("0");
            assertNull(params);

            params = e.getParams("abc");
            assertNull(params);

            params = e.getParams("arg1");
            assertTrue(params instanceof String);

            assertEquals(params, "abc");

            Map<String, Object> data = e.getData();
            assertEquals(data.get("arg1"), "abc");

            assertEquals(1, data.size());

            assertEquals(D.init, e.getType());
        };

        dispatch.on(D.init, callback);

        abc = dispatch.fire(D.event, "abc");
        aBoolean = abc.get();
        assertFalse(aBoolean);
    }

    class A {

    }

    enum D implements EventType {
        event,

        init {
            @Override
            public String[] getNames() {
                return new String[] {"arg1"};
            }
        }
    }


}