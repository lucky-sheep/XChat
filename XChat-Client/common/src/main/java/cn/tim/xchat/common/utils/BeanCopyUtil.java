package cn.tim.xchat.common.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class BeanCopyUtil {

    /**
     * 利用反射实现对象之间相同属性复制
     * @param source 要复制的
     * @param target 复制给
     */
    public static void copyProperties(Object source, Object target) throws Exception {
        copyPropertiesExclude(source, target, null);
    }

    /**
     * 复制对象属性
     * @param excludesArray 排除属性列表
     */
    public static void copyPropertiesExclude(Object from, Object to,
                                             String[] excludesArray) throws Exception {
        List<String> excludesList = null;
        if (excludesArray != null && excludesArray.length > 0) {

            excludesList = Arrays.asList(excludesArray); // 构造列表对象
        }

        Method[] fromMethods = from.getClass().getDeclaredMethods();
        Method[] toMethods = to.getClass().getDeclaredMethods();
        Method fromMethod, toMethod;
        String fromMethodName, toMethodName;

        for (Method method : fromMethods) {
            fromMethod = method;
            fromMethodName = fromMethod.getName();

            if (!fromMethodName.contains("get"))
                continue;
            // 排除列表检测
            if (excludesList != null
                    && excludesList.contains(fromMethodName.substring(3)
                    .toLowerCase())) {

                continue;
            }
            toMethodName = "set" + fromMethodName.substring(3);
            toMethod = findMethodByName(toMethods, toMethodName);

            if (toMethod == null)
                continue;
            Object value = fromMethod.invoke(from);

            if (value == null)
                continue;
            // 集合类判空处理
            if (value instanceof Collection) {

                Collection<?> newValue = (Collection<?>) value;

                if (newValue.size() <= 0)
                    continue;
            }

            toMethod.invoke(to, value);
        }
    }

    /**
     * 对象属性值复制，仅复制指定名称的属性值
     */
    public static void copyPropertiesInclude(Object from, Object to,
                                             String[] includesArray) throws Exception {
        List<String> includesList;
        if (includesArray != null && includesArray.length > 0) {
            includesList = Arrays.asList(includesArray);
        } else {
            return;
        }
        Method[] fromMethods = from.getClass().getDeclaredMethods();
        Method[] toMethods = to.getClass().getDeclaredMethods();
        Method fromMethod, toMethod;
        String fromMethodName, toMethodName;

        for (Method method : fromMethods) {
            fromMethod = method;
            fromMethodName = fromMethod.getName();

            if (!fromMethodName.contains("get"))
                continue;

            // 排除列表检测
            String str = fromMethodName.substring(3);

            if (!includesList.contains(str.substring(0, 1).toLowerCase()
                    + str.substring(1))) {
                continue;
            }

            toMethodName = "set" + fromMethodName.substring(3);
            toMethod = findMethodByName(toMethods, toMethodName);

            if (toMethod == null)
                continue;

            Object value = fromMethod.invoke(from);

            if (value == null)
                continue;

            // 集合类判空处理
            if (value instanceof Collection) {

                Collection<?> newValue = (Collection<?>) value;

                if (newValue.size() <= 0)
                    continue;
            }

            toMethod.invoke(to, value);
        }
    }

    /**
     * 从方法数组中获取指定名称的方法
     */
    public static Method findMethodByName(Method[] methods, String name) {
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }
}