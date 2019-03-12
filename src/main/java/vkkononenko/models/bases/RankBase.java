package vkkononenko.models.bases;

import javax.persistence.MappedSuperclass;

/**
 * Created by v.kononenko on 12.03.2019.
 */
@MappedSuperclass
public class RankBase extends EntityBase {

    protected Long rank = 0L;

    public Long getRank() {
        return rank;
    }

    public void setRank(Long rank) {
        this.rank = rank;
    }
}
