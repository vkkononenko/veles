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

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected List<SystemUser> gradeBy;

    public Long getRank() {
        return rank;
    }

    public void setRank(Long rank) {
        this.rank = rank;
    }

    public List<SystemUser> getGradeBy() {
        return gradeBy;
    }

    public void setGradeBy(List<SystemUser> gradeBy) {
        this.gradeBy = gradeBy;
    }
}
