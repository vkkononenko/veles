package vkkononenko.models;

import vkkononenko.models.bases.RankBase;

import javax.persistence.*;
import java.util.List;

/**
 * Created by v.kononenko on 12.03.2019.
 */
@Entity
public class Guide extends RankBase {

    protected String name;

    @Column(columnDefinition = "text")
    protected String text;

    @ManyToOne
    private SystemUser makeBy;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Comment> comments;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public SystemUser getMakeBy() {
        return makeBy;
    }

    public void setMakeBy(SystemUser makeBy) {
        this.makeBy = makeBy;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
