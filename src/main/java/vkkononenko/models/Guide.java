package vkkononenko.models;

import vkkononenko.models.bases.RankBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

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
}
