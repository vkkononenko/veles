package vkkononenko.beans;

import org.hibernate.Hibernate;
import vkkononenko.UserSession;
import vkkononenko.models.Repository;

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
public class ViewRepositories implements Serializable {

    @Inject
    private UserSession userSession;

    private List<Repository> repositoryList;

    @PersistenceContext(name = "veles")
    private EntityManager em;

    @Transactional
    public void onLoad() {
        em.refresh(userSession.getSystemUser());
        repositoryList = userSession.getSystemUser().getRepositories();
    }

    public void redirectToCreateNewEntity() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("repository-view.xhtml");
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
