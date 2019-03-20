package vkkononenko.beans;

import vkkononenko.SecurityUtils;
import vkkononenko.UserSession;
import vkkononenko.models.Repository;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Created by v.kononenko on 18.02.2019.
 */
@Named
@ViewScoped
public class ViewNeedGradeRepositories extends SecurityUtils implements Serializable {

    @Inject
    private UserSession userSession;

    private List<Repository> repositoryList;

    @Transactional
    public void onLoad() {
        repositoryList = userSession.getSystemUser().getNeedGrade();
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
