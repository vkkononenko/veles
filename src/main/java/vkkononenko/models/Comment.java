package vkkononenko.models;

import vkkononenko.models.bases.RankBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by v.kononenko on 18.03.2019.
 */
@Entity
public class Comment extends RankBase implements Comparable<Comment> {

    @Override
    public int compareTo(Comment o) {
        return (int) (o.rank - this.rank);
    }
}
