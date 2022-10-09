package springframework.util;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class ClassUtils {

    public static final String CGLIB_CLASS_SEPARATOR = "$$EnhancerByCGLIB$$";

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ignore) {

        }
        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
        }
        return cl;
    }

    public static boolean isCglibProxyClass(Class<?> clazz) {
        return clazz != null && isCglibProxyClassName(clazz.getName());
    }

    public static boolean isCglibProxyClassName(String className) {
        return className != null && className.contains(CGLIB_CLASS_SEPARATOR);
    }

    public static Class<?> getUserClass(Class<?> clazz) {
        if (isCglibProxyClass(clazz)) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null && superclass != Object.class) {
                return superclass;
            }
        }
        return clazz;
    }

    /**
     * 获取clazz及其父类的所有接口
     *
     * @param clazz
     * @return
     */
    public static Set<Class<?>> getAllInterfacesForClassAsSet(Class<?> clazz) {
        Set<Class<?>> interfaces = new LinkedHashSet<>();
        if (clazz.isInterface()) {
            interfaces.add(clazz);
        }
        Class<?> current = clazz;
        while (current != null) {
            interfaces.addAll(Arrays.asList(clazz.getInterfaces()));
            current = current.getSuperclass();
        }
        return interfaces;
    }
}
