package springframework.core.io;

import springframework.beans.BeansException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class UrlResource implements Resource {
    private final URL Url;

    public UrlResource(URL url) {
        Url = url;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        URLConnection urlConnection = this.Url.openConnection();
        InputStream inputStream = null;
        try {
            inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                throw new BeansException(" InputStream is null ");
            }
        } catch (IOException | BeansException e) {
            e.printStackTrace();
            if (urlConnection instanceof HttpURLConnection) {
                ((HttpURLConnection) urlConnection).disconnect();
            }
        }
        return inputStream;
    }
}
