package vkkononenko.filters;

import vkkononenko.models.SystemUser;

import java.io.Serializable;

/**
 * Created by v.kononenko on 12.03.2019.
 */
public class RepositoryFilter implements Serializable {
    private Long id;
    private String name;
    private String author;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
