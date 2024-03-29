package springframework.core.io;

import springframework.beans.BeansException;
import springframework.util.ClassUtils;

import java.io.InputStream;

public class ClassPathResource implements Resource {

    private final String path;

    private final ClassLoader classLoader;

    /**
     * 设置path
     * 使用线程上下文类加载器，无法获取则使用该类的类加载器
     *
     * @param path
     */
    public ClassPathResource(String path) {
        this(path, null);
    }

    /**
     * 设置path
     * 设置classLoader，使用提供的类加载器，没有提供则使用线程上下文类加载器，无法获取则使用该类的类加载器
     *
     * @param path
     * @param classLoader
     */
    public ClassPathResource(String path, ClassLoader classLoader) {
        this.path = path;
        if (classLoader != null) {
            this.classLoader = classLoader;
        } else {
            this.classLoader = ClassUtils.getDefaultClassLoader();
        }
    }

    @Override
    public String getResourceLocation() {
        return path;
    }

    /**
     * 使用类加载器获取path文件的输入流
     *
     * @return
     */
    @Override
    public InputStream getInputStream() {
        InputStream is = classLoader.getResourceAsStream(path);
        if (is == null) {
            throw new BeansException(this.path + " does not exist");
        }
        return is;
    }
}
