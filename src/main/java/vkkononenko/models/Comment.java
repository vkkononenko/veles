package vkkononenko.models;

import vkkononenko.models.bases.RankBase;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by v.kononenko on 18.03.2019.
 */
@Entity
public class Comment extends RankBase {

    @Column(columnDefinition = "text")
    private String text;
}
