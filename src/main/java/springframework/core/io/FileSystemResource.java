package springframework.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileSystemResource implements Resource {
    String path;

    File file;

    public FileSystemResource(String path) {
        this.path = path;
    }

    public FileSystemResource(File file) {
        this.file = file;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (file == null) {
            file = new File(path);
        }
        return new FileInputStream(file);
    }
}
