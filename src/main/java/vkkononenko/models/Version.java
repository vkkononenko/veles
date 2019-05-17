package vkkononenko.models;

import vkkononenko.models.bases.EntityBase;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Version extends EntityBase {
    private static final long serialVersionUID = 4073670823157462669L;

    @Column(columnDefinition = "text")
    private String data;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Comment> comments;

    public Version() {

    }

    public Version(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
