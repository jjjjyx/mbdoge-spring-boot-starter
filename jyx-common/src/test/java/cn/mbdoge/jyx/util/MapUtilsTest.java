package cn.mbdoge.jyx.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MapUtilsTest {

    @Test
    void zipObject() {
        String args1 = "xxx";
        A a = new A();
        Map<String, Object> abc = MapUtils.zipObject(new String[]{}, a, "abc");

        Set<String> strings = abc.keySet();
        Assertions.assertIterableEquals(strings, Arrays.asList("0", "1"));

        Object o = abc.get("0");
        Assertions.assertTrue(o instanceof A);

        Object o2 = abc.get("1");
        Assertions.assertTrue(o2 instanceof String);

        // ========= 传入null
        abc = MapUtils.zipObject(null, a, "abc");

        strings = abc.keySet();
        Assertions.assertIterableEquals(strings, Arrays.asList("0", "1"));

        o = abc.get("0");
        Assertions.assertTrue(o instanceof A);

        o2 = abc.get("1");
        Assertions.assertTrue(o2 instanceof String);


        // ========= 不传入args
        abc = MapUtils.zipObject(null);
        Assertions.assertTrue(abc.isEmpty());


        // ========= 传入名称，个数小于 参数
        abc = MapUtils.zipObject(new String[]{"xx"}, a, "abc");

        strings = abc.keySet();
        Assertions.assertIterableEquals(strings, Arrays.asList("xx", "1"));

        o = abc.get("xx");
        Assertions.assertTrue(o instanceof A);

        o2 = abc.get("1");
        Assertions.assertTrue(o2 instanceof String);

        // ========= 传入名称，个数多余 参数
        abc = MapUtils.zipObject(new String[]{"xx", "str", "aa"}, a, "abc");

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