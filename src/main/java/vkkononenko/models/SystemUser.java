package vkkononenko.models;

import vkkononenko.models.bases.EntityBase;

import javax.persistence.*;
import java.util.List;

@Entity
public class SystemUser extends EntityBase {

    @Column(unique = true)
    private String login;

    private String password;

    private String name;

    private String orgName;

    private String avatarPath;

    @OneToMany(fetch = FetchType.EAGER)
    private List<SystemUser> friends;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Repository> repositories;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "needGrade",
            joinColumns = @JoinColumn(name = "system_user_id"),
            inverseJoinColumns = @JoinColumn(name = "repository_id")
    )
    private List<Repository> needGrade;

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

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
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

    public List<Repository> getNeedGrade() {
        return needGrade;
    }

    public void setNeedGrade(List<Repository> needGrade) {
        this.needGrade = needGrade;
    }
}
