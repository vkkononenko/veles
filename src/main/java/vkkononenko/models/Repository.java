package vkkononenko.models;

import com.sun.faces.util.CollectionsUtils;
import org.primefaces.util.CollectionUtils;
import vkkononenko.models.bases.EntityBase;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Repository extends EntityBase {

    private String name;

    private boolean secret;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "followers",
            joinColumns = @JoinColumn(name = "repository_id"),
            inverseJoinColumns = @JoinColumn(name = "system_user_id")
    )
    private List<SystemUser> followers;

    @ManyToOne
    private SystemUser makeBy;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Version> versions;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Grade> grades;

    public boolean isAccepted() {
        if(grades == null) {
            return true;
        }
        for(Grade grade : grades) {
            if(!grade.isAccepted()) {
                return false;
            }
        }
        return true;
    }

    public Repository() {
        followers = new ArrayList<>();
        versions = new ArrayList<>();
        grades = new ArrayList<>();
    }

    public Grade getGradeByUser(SystemUser systemUser) {
        for(Grade grade : grades) {
            if(grade.getSystemUser().getId() == systemUser.getId()) {
                return grade;
            }
        }
        return null;
    }

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

    public SystemUser getMakeBy() {
        return makeBy;
    }

    public void setMakeBy(SystemUser makeBy) {
        this.makeBy = makeBy;
    }

    public List<Version> getVersions() {
        return versions;
    }

    public void setVersions(List<Version> versions) {
        this.versions = versions;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }
}
