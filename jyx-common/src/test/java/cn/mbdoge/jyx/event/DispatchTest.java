package cn.mbdoge.jyx.event;

import jdk.nashorn.internal.runtime.regexp.joni.constants.Arguments;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DispatchTest {
    private Dispatch dispatch;
    @BeforeEach
    void setUp() {
        dispatch = new Dispatch();
    }

    @Test
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

    class A {

    }



}