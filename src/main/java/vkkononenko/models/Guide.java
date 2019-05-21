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
    
    private String name;

    private Long count = 1L;
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Comment> comments;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
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

    public static Boolean compare(Long countFirst, Long countSecond) {
        return ((countSecond - countFirst) > 0);
    }
}
