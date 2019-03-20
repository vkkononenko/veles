package vkkononenko.beans;

import org.hibernate.Hibernate;
import vkkononenko.SecurityUtils;
import vkkononenko.UserSession;
import vkkononenko.models.Repository;
import vkkononenko.models.SystemUser;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Created by v.kononenko on 14.01.2019.
 */
@Named
@ViewScoped
public class ViewRepositories extends SecurityUtils implements Serializable {

    @PersistenceContext(name = "veles")
    private EntityManager em;

    private Long id;

    @Inject
    private UserSession userSession;

    @Inject
    private SystemUser systemUser;

    private List<Repository> repositoryList;

    @Transactional
    public void onLoad() {
        if(id == null) {
            repositoryList = userSession.getSystemUser().getRepositories();
        } else {
            systemUser = em.find(SystemUser.class, id);
            repositoryList = systemUser.getRepositories();
        }
    }

    public void redirectToCreateNewEntity() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("repository-view.xhtml");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public List<Repository> getRepositoryList() {
        return repositoryList;
    }

    public void setRepositoryList(List<Repository> repositoryList) {
        this.repositoryList = repositoryList;
    }
}
