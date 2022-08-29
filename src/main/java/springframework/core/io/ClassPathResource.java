package springframework.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;
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
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                //使用该类的类加载器
                this.classLoader = this.getClass().getClassLoader();
            } else {
                //使用线程上下文类加载器
                this.classLoader = cl;
            }
        }
    }

    /**
     * 使用类加载器获取path文件的输入流
     *
     * @return
     * @throws IOException
     */
    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is = classLoader.getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException(this.path + " does not exist");
        }
        return is;
    }
}
