package vkkononenko.models;

import vkkononenko.models.bases.EntityBase;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Blob;
import java.util.List;

@Entity
public class SystemUser extends EntityBase {

    private static final long serialVersionUID = -2834044366197086722L;

    @Column(unique = true)
    private String login;

    private String password;

    private String name;

    private String orgName;

    @Column(columnDefinition = "text")
    protected String status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "friends",
            joinColumns = @JoinColumn(name = "first_friend"),
            inverseJoinColumns = @JoinColumn(name = "second_friend")
    )
    private List<SystemUser> friends;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Repository> repositories;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Guide> guides;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "needGrade",
            joinColumns = @JoinColumn(name = "system_user_id"),
            inverseJoinColumns = @JoinColumn(name = "repository_id")
    )
    private List<Repository> needGrade;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public List<SystemUser> getFriends() {
        return friends;
    }

    public void setFriends(List<SystemUser> friends) {
        this.friends = friends;
    }

    public List<Repository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<Repository> repositories) {
        this.repositories = repositories;
    }

    public List<Guide> getGuides() {
        return guides;
    }

    public void setGuides(List<Guide> guides) {
        this.guides = guides;
    }

    public List<Repository> getNeedGrade() {
        return needGrade;
    }

    public void setNeedGrade(List<Repository> needGrade) {
        this.needGrade = needGrade;
    }
}
