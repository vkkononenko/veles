package vkkononenko.utils;

import vkkononenko.models.SystemUser;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.Serializable;

@Named
@SessionScoped
public class ApplicationUtils implements Serializable {

    @PersistenceContext(name = "veles")
    private EntityManager em;

    private SystemUser systemUser;

    @Transactional
    public void init() {
        if (systemUser == null) {
            systemUser = new SystemUser();
            Query q = em.createQuery("SELECT s from SystemUser s where s.login = :login");
            q.setParameter("login", "Система \"Велес\"");
            if (q.getResultList().size() < 1) {
                systemUser.setId(0L);
                systemUser.setLogin("Система \"Велес\"");
                em.persist(em.merge(systemUser));
            } else {
                systemUser = (SystemUser) q.getSingleResult();
            }
        }
    }

    public SystemUser getSystemUser() {
        return systemUser;
    }

    public void setSystemUser(SystemUser systemUser) {
        this.systemUser = systemUser;
    }
}