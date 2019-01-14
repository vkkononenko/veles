package vkkononenko.models;

import vkkononenko.models.bases.EntityBase;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Repository extends EntityBase {

    private String name;

    private boolean secret;

    @OneToMany(fetch = FetchType.EAGER)
    private List<SystemUser> followers;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Version> versions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSecret() {
        return secret;
    }

    public void setSecret(boolean secret) {
        this.secret = secret;
    }

    public List<SystemUser> getFollowers() {
        return followers;
    }

    public void setFollowers(List<SystemUser> followers) {
        this.followers = followers;
    }

    public List<Version> getVersions() {
        return versions;
    }

    public void setVersions(List<Version> versions) {
        this.versions = versions;
    }
}
