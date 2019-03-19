package vkkononenko.models;

import vkkononenko.models.bases.RankBase;

import javax.persistence.*;
import java.util.Comparator;
import java.util.List;

/**
 * Created by v.kononenko on 12.03.2019.
 */
@Entity
public class Guide extends RankBase implements Comparable<Guide> {

    protected String name;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Comment> comments;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public int compareTo(Guide o) {
        return (int) (o.rank - this.rank);
    }
}
