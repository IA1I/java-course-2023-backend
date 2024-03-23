package edu.java.bot.dto;

import io.mola.galimatias.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class User {
    private final long id;
    private final List<URL> urls;

    public User(long id) {
        this.id = id;
        this.urls = new ArrayList<>();
    }

    public void addUrl(URL url) {
        urls.add(url);
    }

    public void removeUrl(URL url) {
        urls.remove(url);
    }

    public boolean contains(URL url) {
        return urls.contains(url);
    }

}
