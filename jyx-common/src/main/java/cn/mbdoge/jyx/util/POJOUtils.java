package cn.mbdoge.jyx.util;

import lombok.SneakyThrows;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 貌似没什么作用，有更好的替代工具： Apache Commons BeanUtils
 * @author jyx
 */
@Deprecated
public class POJOUtils {

    @SneakyThrows
    public static void merge(Object src, Object dest) {
        Class<?> srcCls = src.getClass();
        Class<?> destCls = dest.getClass();

        BeanInfo beanInfo = Introspector.getBeanInfo(srcCls);
        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

        for (PropertyDescriptor pd : pds) {
            Method get = pd.getReadMethod();
            String name = pd.getName();

            if ("class".equals(name)) {
                continue;
            }
            PropertyDescriptor destPd;
            try {
                destPd = new PropertyDescriptor(name, destCls);
            } catch (IntrospectionException e) {
                continue;
            }
            Method set = destPd.getWriteMethod();
            try {
                Object value = get.invoke(src);
                if (value != null) {
                    set.invoke(dest, value);
                }

            } catch (IllegalAccessException | InvocationTargetException ignored) {
            }
        }
    }

}
