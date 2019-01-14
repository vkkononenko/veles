package vkkononenko.beans;

import vkkononenko.models.SystemUser;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
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
@ViewScoped
public class RegistrationView implements Serializable {
    @PersistenceContext(name = "veles")
    private EntityManager em;

    @Inject
    private SystemUser systemUser;

    public void onLoad() {

    }

    @Transactional
    public void register() {
        Query q = em.createQuery("SELECT s from SystemUser s where s.login = :login");
        q.setParameter("login", systemUser.getLogin());
        if(q.getResultList().size() == 0) {
           em.persist(systemUser);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Сообщние!", "Вы успешно зарегистрировались в системе"));
        }
        else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка!", "Пользователь с данным логином уже зарегистрирован, выберите другой"));
        }
    }

    public SystemUser getSystemUser() {
        return systemUser;
    }

    public void setSystemUser(SystemUser systemUser) {
        this.systemUser = systemUser;
    }
}