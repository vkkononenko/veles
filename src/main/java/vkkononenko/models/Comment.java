package vkkononenko.models;

import vkkononenko.models.bases.RankBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by v.kononenko on 18.03.2019.
 */
@Entity
public class Comment extends RankBase {

    @Column(columnDefinition = "text")
    private String text;

    @ManyToOne
    private SystemUser makeBy;

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
}
