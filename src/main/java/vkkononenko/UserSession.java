package vkkononenko;

import vkkononenko.models.SystemUser;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.Serializable;

@Named
@SessionScoped
public class UserSession implements Serializable {

    @Inject
    private SystemUser systemUser;

    private String login;

    private String password;

    @PersistenceContext(name = "veles")
    private EntityManager em;

    public void onLoad() {

    }

    @Transactional
    public void logIntoSystem() {
        try {
            Query q = em.createQuery("SELECT s from SystemUser s where s.login = :login");
            q.setParameter("login", login);
            systemUser = (SystemUser) q.getSingleResult();

            if (systemUser.getPassword().equals(password)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Ура!", "Вы вошли в систему!"));
                FacesContext.getCurrentInstance().getExternalContext().redirect("dashboard.xhtml");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка!", "Неверная пара логин-пароль!"));
                systemUser = null;
            }
        }
        catch (NoResultException | IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка!", "Пользователя с данным логином не существует"));
            systemUser = null;
            return;
        }
    }

    public Long getId() {
        return systemUser.getId();
    }

    public SystemUser getSystemUser() {
        return systemUser;
    }

    public void setSystemUser(SystemUser systemUser) {
        this.systemUser = systemUser;
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
}
