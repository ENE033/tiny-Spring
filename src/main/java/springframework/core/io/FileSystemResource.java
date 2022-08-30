package springframework.core.io;

import springframework.beans.BeansException;

import java.io.*;

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
    public InputStream getInputStream() {
        if (file == null) {
            file = new File(path);
        }
        InputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new BeansException(" File not found ：" + file.getName(), e);
        }
        return fileInputStream;
    }
}
