package vkkononenko.models.bases;

import vkkononenko.models.SystemUser;
import javax.persistence.*;
import java.util.List;

/**
 * Created by v.kononenko on 12.03.2019.
 */
@MappedSuperclass
public class RankBase extends EntityBase {

    protected Long rank = 0L;
    
    protected Long count;

    @ManyToOne
    private SystemUser makeBy;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected List<SystemUser> gradeBy;

    @Column(columnDefinition = "text")
    protected String text;

    public Long getRank() {
        return rank;
    }

    public void setRank(Long rank) {
        this.rank = rank;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public SystemUser getMakeBy() {
        return makeBy;
    }

    public void setMakeBy(SystemUser makeBy) {
        this.makeBy = makeBy;
    }

    public List<SystemUser> getGradeBy() {
        return gradeBy;
    }

    public void setGradeBy(List<SystemUser> gradeBy) {
        this.gradeBy = gradeBy;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
