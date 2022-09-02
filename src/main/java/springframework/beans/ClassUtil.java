package springframework.beans;

public class ClassUtil {

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
}
