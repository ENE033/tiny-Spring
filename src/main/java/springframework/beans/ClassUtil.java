package springframework.beans;

public class ClassUtil {

    public static final String CGLIB_CLASS_SEPARATOR = "$$EnhancerByCGLIB$$";

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ignore) {

        }
        if (cl == null) {
            cl = ClassUtil.class.getClassLoader();
        }
        return cl;
    }

    public static boolean isCglibProxyClass(Class<?> clazz) {
        return clazz != null && isCglibProxyClassName(clazz.getName());
    }

    public static boolean isCglibProxyClassName(String className) {
        return className != null && className.contains(CGLIB_CLASS_SEPARATOR);
    }

}
