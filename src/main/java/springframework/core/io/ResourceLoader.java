package springframework.core.io;

public interface ResourceLoader {

    /**
     * 包路径的前缀
     */
    String CLASSPATH_URL_PREFIX = "classpath:";

    /**
     * 获取文件路径资源
     *
     * @param location
     * @return
     */
    Resource getResource(String location);

}
