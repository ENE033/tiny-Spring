package springframework.core.io;

public class DefaultResourceLoader implements ResourceLoader {

    /**
     * 通过location路径来获取文件资源
     *
     * @param location
     * @return
     */
    @Override
    public Resource getResource(String location) {
        return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()));
    }
}
