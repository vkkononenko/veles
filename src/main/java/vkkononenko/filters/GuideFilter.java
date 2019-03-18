package vkkononenko.filters;

import java.io.Serializable;

/**
 * Created by v.kononenko on 18.03.2019.
 */
public class GuideFilter implements Serializable {
    private Long id;
    private String name;
    private String author;
    private String include;

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

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
    }
}
