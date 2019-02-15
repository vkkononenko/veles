package vkkononenko.models;

import vkkononenko.models.bases.EntityBase;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class SystemUser extends EntityBase {

    @Column(unique = true)
    private String login;

    private String password;

    private String name;

    private String orgName;

    @OneToMany(fetch = FetchType.EAGER)
    private List<SystemUser> friends;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Repository> repositories;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Grade> needGrade;

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

    public List<Grade> getNeedGrade() {
        return needGrade;
    }

    public void setNeedGrade(List<Grade> needGrade) {
        this.needGrade = needGrade;
    }
}
