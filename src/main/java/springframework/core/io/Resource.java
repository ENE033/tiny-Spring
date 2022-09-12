package springframework.core.io;

import java.io.IOException;
import java.io.InputStream;

public interface Resource {

    String getResourceLocation();

    /**
     * 获取输入流
     *
     * @return
     * @throws IOException
     */
    InputStream getInputStream();

}
