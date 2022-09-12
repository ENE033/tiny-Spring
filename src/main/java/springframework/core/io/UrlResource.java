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
    public String getResourceLocation() {
        return Url.toString();
    }

    @Override
    public InputStream getInputStream() {
        URLConnection urlConnection = null;
        InputStream inputStream;
        try {
            urlConnection = this.Url.openConnection();
            inputStream = urlConnection.getInputStream();
        } catch (IOException e) {
            if (urlConnection instanceof HttpURLConnection) {
                ((HttpURLConnection) urlConnection).disconnect();
            }
            throw new BeansException(" Obtain Url inputStream fail ：" + this.Url.getPath(), e);
        }
        return inputStream;
    }
}
