package cn.mbdoge.jyx.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jyx
 */
public final class MapUtils {
    private MapUtils(){}
    public static Map<String, Object> zipObject (String[] props, Object... values) {
        if (null == props) {
            props = new String[0];
        }
        Map<String, Object> data = new HashMap<>(10);
        if (values == null || values.length == 0) {
            return data;
        }


        int argsLength = values.length;
        int nameLength = props.length;
        for (int i = 0; i < nameLength && i < argsLength; i++) {
            data.put(props[i], values[i]);
        }

        for (int i = nameLength; i < argsLength; i++) {
            data.put(String.valueOf(i), values[i]);
        }
        return data;

    }
}
