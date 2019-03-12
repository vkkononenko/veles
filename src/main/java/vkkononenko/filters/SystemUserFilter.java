package vkkononenko.filters;

import java.io.Serializable;

/**
 * Created by v.kononenko on 12.03.2019.
 */
public class SystemUserFilter implements Serializable {

    private String login;
    private String name;
    private String orgName;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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
}
