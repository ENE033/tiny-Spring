package springframework.core.io;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DefaultResourceLoader implements ResourceLoader {

    /**
     * 通过location路径来获取文件资源
     *
     * @param location
     * @return
     */
    @Override
    public Resource getResource(String location) {
        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()));
        } else {
            try {
                URL url = new URL(location);
                return new UrlResource(url);
            } catch (MalformedURLException e) {
                if (new File(location).exists()) {
                    return new FileSystemResource(location);
                } else {
                    return new ClassPathResource(location);
                }
            }
        }

    }
}
