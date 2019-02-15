package vkkononenko.models;

import vkkononenko.models.SystemUser;
import vkkononenko.models.bases.EntityBase;

import javax.persistence.Entity;

/**
 * Created by v.kononenko on 14.01.2019.
 */
@Entity
public class Grade extends EntityBase {

    private SystemUser systemUser;

    private boolean accepted = false;

    private String comment;

    public Grade() {}

    public Grade(SystemUser systemUser) {
        this.systemUser = systemUser;
        accepted = false;
    }

    public SystemUser getSystemUser() {
        return systemUser;
    }

    public void setSystemUser(SystemUser systemUser) {
        this.systemUser = systemUser;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
